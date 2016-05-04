package tw.darkk6.meddle.infoline.mod;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.fybertech.meddleapi.ConfigFile;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import tw.darkk6.meddle.api.util.APILog;
import tw.darkk6.meddle.infoline.util.Util;

public class Biome extends IModBase {


	public static final String MOD_CATALOG="biome";
	
	private BiomeCfg config; 
	public Biome(ConfigFile cfg) {
		super(cfg);
		config=new BiomeCfg();
		config.update(cfg);
	}

	@Override
	public String parseResult(String str) {
		if(str.indexOf("{biome}")==-1) return str;
		return str.replaceAll("\\{biome\\}",getBiomeString());
	}

	private String getBiomeString(){
		if(!config.isEnabled()) return "";
		Minecraft mc=Minecraft.getMinecraft();
		BlockPos pos=Util.getPlayerPos();
		if(pos==null) return "{Error}";
		//== Check Can Snow ==
		// 要檢查的是腳底下那一格，所以要 .down()
		// .e() => canSnowAtBody(pos , checkLight )
		boolean canSnow=mc.theWorld.e(pos.down(),false);
		StringBuilder name=new StringBuilder();
		float temp=getBiomeNameTempture(pos,name);
		String fmt=config.displayFormat;
		if(canSnow){
			name.insert(0,APILog.TextFormatting.AQUA);
			name.append(APILog.TextFormatting.RESET);
		}
		StringBuilder tempStr=new StringBuilder();
		if(temp<=0.15f)// 下雪
			tempStr.append(APILog.TextFormatting.AQUA);
		else if( temp<=0.95f ){
			// 下雨 , DoNothing
		}else if(temp<=1.0f){// 0.95~1.0 似乎還會下雨
			tempStr.append(APILog.TextFormatting.YELLOW);
		}else//>0.95  熱帶 ， 不下雨
			tempStr.append(APILog.TextFormatting.GOLD);
		tempStr.append(String.format("%.02f", temp)).append(APILog.TextFormatting.RESET);
		
		return fmt.replaceAll("\\{B\\}",name.toString())
				.replaceAll("\\{T\\}", tempStr.toString());
	}
	
	@Override
	protected IConfig getConfig() {
		return config;
	}
	
	private Class cBiomeGenBase=null;
	private Method getFloatTemperature=null,getBiomeGenForCoordsBody=null;
	private Field fBiomeName=null;
	@SuppressWarnings("unchecked")
	private float getBiomeNameTempture(BlockPos playerPos,StringBuilder name){
		name.setLength(0);
		// getBiomeGenForCoords 似乎是 forge 的，改 用 getBiomeGenForCoordsBody
		BlockPos pos64=new BlockPos(playerPos.getX(),64,playerPos.getZ());
		try{
			if(getBiomeGenForCoordsBody==null){
				// Meddle 無法找到 .b() 這個 method , 自己來
				// b(final BlockPos) => getBiomeGenForCoordsBody()
				getBiomeGenForCoordsBody=World.class.getMethod("b",BlockPos.class);
			}
			Object biomeObject=getBiomeGenForCoordsBody.invoke(
					Minecraft.getMinecraft().theWorld, pos64);
			
			// aig => BiomeGenBase
			if(cBiomeGenBase==null) cBiomeGenBase=Class.forName("aig");
			// z => biomeName , is private
			if(fBiomeName==null) fBiomeName=cBiomeGenBase.getDeclaredField("z");
			fBiomeName.setAccessible(true);
			//取得名字完畢
			name.append(fBiomeName.get(biomeObject).toString());
			//取得溫度
			// a => getFloatTemperature(BlockPos) , final
			if(getFloatTemperature==null)
				getFloatTemperature=cBiomeGenBase.getMethod("a",BlockPos.class);
			return (Float)getFloatTemperature.invoke(biomeObject, playerPos);
		}catch(Exception e){
			name.append("Unknown");
			return -1.0f;
		}
	}
	
	public class BiomeCfg implements IConfig{

		public boolean isEnabled=true;
		public String displayFormat="{B} - {T}";
		
		@Override
		public boolean isEnabled() {
			return isEnabled;
		}

		@Override
		public void update(ConfigFile cfg) {
			isEnabled=((Boolean)cfg.get(ConfigFile.key(
					MOD_CATALOG, "enableBiomeMod", Boolean.valueOf(isEnabled),
					"使否顯示生態環境資訊 ,TAG:{biome}"))).booleanValue();
			
			displayFormat=cfg.get(ConfigFile.key(
					MOD_CATALOG, "biomeDisplayFormat", displayFormat,
					"顯示格式\n{B}:Biome Name,生態系名稱    {T}:Temperature,溫度"));
			
		}
		
	}

}
