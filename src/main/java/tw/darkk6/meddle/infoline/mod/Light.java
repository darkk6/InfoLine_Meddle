package tw.darkk6.meddle.infoline.mod;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.fybertech.meddleapi.ConfigFile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import tw.darkk6.meddle.api.util.APILog;
import tw.darkk6.meddle.infoline.util.Reference;
import tw.darkk6.meddle.infoline.util.Util;

public class Light extends IModBase {

	public static final String MOD_CATALOG="light";
	
	private LightCfg config; 
	public Light(ConfigFile cfg) {
		super(cfg);
		config=new LightCfg();
		config.update(cfg);
	}

	@Override
	public String parseResult(String str) {
		if(str.indexOf("{light}")==-1) return str;
		return str.replaceAll("\\{light\\}",getLightString());
	}
	
	private String getLightString(){
		if(!config.isEnabled()) return "";
		String fmt = config.lightFormat;
		int[] lights = getLightLevel();
		String[] lightStr=new String[2];
		lightStr[0] = String.valueOf(lights[0]);
		lightStr[1] = String.valueOf(lights[1]);
		if(config.useColor){
			for(int i=0;i<lights.length;i++){
				if(lights[i]<8)
					lightStr[i] = APILog.TextFormatting.RED+lightStr[i]+APILog.TextFormatting.RESET;
			}
		}
		return fmt.replaceAll("\\{B\\}", lightStr[0])
				.replaceAll("\\{S\\}", lightStr[1]);
	}

	private int[] getLightLevel(){
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.thePlayer == null) return new int[]{0,0};
		BlockPos pos = Util.getPlayerPos();
		if(pos==null) return new int[]{0,0};
		WorldClient world = mc.theWorld;
		int lSky = getLightFor(world,ENUMSKYBLOCK_SKY,pos);
		int lBlock = getLightFor(world,ENUMSKYBLOCK_BLOCK,pos);
		// a => calculateSkylightSubtracted
		lSky = lSky - world.a(1.0F);
		lSky = Math.max(lSky, lBlock);
		int[] lights=new int[2];
		lights[0] = lBlock;
		lights[1] = lSky;
		return lights;
	}
	
	@Override
	protected IConfig getConfig() {
		return config;
	}

	private static final int ENUMSKYBLOCK_SKY=15;
	private static final int ENUMSKYBLOCK_BLOCK=0;
	private Class class_enumskyblock;
	private Method getLightForMethod;
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private int getLightFor(World world,int type,BlockPos pos){
		try{
			Class w=World.class;
			if(class_enumskyblock==null)
				class_enumskyblock=Class.forName("ahz");
			if(getLightForMethod==null)
				getLightForMethod=w.getMethod("b",class_enumskyblock,BlockPos.class);
			Object skyBlockObj=getEnumSkyBlock(type);
			if(skyBlockObj==null) return 0;
			return (Integer)getLightForMethod.invoke(world,skyBlockObj,pos);
		}catch(Exception e){
			APILog.error("Error when call getLightFor", Reference.LOG_TAG);
		}
		return 0;
	}
	private Field FIELD_SKY,FIELD_BLOCK;
	private Object getEnumSkyBlock(int type){
		try{
			if(class_enumskyblock==null) throw new RuntimeException("Call getEnumSkyBlock without calling getLightFor");
			if(FIELD_SKY==null)
				FIELD_SKY=class_enumskyblock.getField("a");
			if(FIELD_BLOCK==null)
				FIELD_BLOCK=class_enumskyblock.getField("b");
			if(type==ENUMSKYBLOCK_SKY) return FIELD_SKY.get(null);
			else if(type==ENUMSKYBLOCK_BLOCK) return FIELD_BLOCK.get(null);
		}catch(Exception e){
			APILog.error("Error when getting EnumSkyBlock", Reference.LOG_TAG);
		}
		return null;
	}
	
	public class LightCfg implements IConfig{
		public boolean isEnabled=true,useColor=true;
		public String lightFormat="{B} [{S}]";
		
		@Override
		public boolean isEnabled() {
			return isEnabled;
		}

		@Override
		public void update(ConfigFile cfg) {
			isEnabled=((Boolean)cfg.get(ConfigFile.key(
					MOD_CATALOG, "enableLightMod", Boolean.valueOf(isEnabled),
					"使否顯示光源等級,TAG:{light}"))).booleanValue();
			
			useColor=((Boolean)cfg.get(ConfigFile.key(
					MOD_CATALOG, "useLightColor", Boolean.valueOf(useColor),
					"危險亮度提示(亮度於 7 以下)"))).booleanValue();
			
			lightFormat=cfg.get(ConfigFile.key(
					MOD_CATALOG, "lightDisplayFormat", lightFormat,
					"顯示格式 \n{B}:Block light,方塊亮度    {S}:Sky light,加上天空亮度"));
		}
	}
}
