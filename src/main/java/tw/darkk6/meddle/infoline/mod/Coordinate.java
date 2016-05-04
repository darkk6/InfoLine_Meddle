package tw.darkk6.meddle.infoline.mod;

import net.fybertech.meddleapi.ConfigFile;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import tw.darkk6.meddle.api.util.APILog;
import tw.darkk6.meddle.infoline.util.Util;

public class Coordinate extends IModBase {

	public static final String MOD_CATALOG="coordinate";
	public static Coordinate instance;
	
	private CoordCfg config; 
	public Coordinate(ConfigFile cfg) {
		super(cfg);
		config=new CoordCfg();
		config.update(cfg);
		instance=this;
	}

	@Override
	public String parseResult(String str) {
		if(str.indexOf("{coordinate}")==-1) return str;
		return str.replaceAll("\\{coordinate\\}",getCoordString());
	}

	@Override
	protected IConfig getConfig() {
		return config;
	}

	public String getCoordInChatStr(){
		BlockPos pos = Util.getPlayerPos();
		if(pos==null) return null;
		String result=config.chatFormat;
		result=result.replaceAll("\\{X\\}",String.valueOf(pos.getX()))
				.replaceAll("\\{Y\\}",String.valueOf(pos.getY()))
				.replaceAll("\\{Z\\}",String.valueOf(pos.getZ()));
		return result;
	}
	
	private String getCoordString(){
		if(!config.isEnabled()) return "";
		BlockPos pos = Util.getPlayerPos();
		if(pos==null) return "Get playe coordinate error.";
		String facing="";
		Entity player=Minecraft.getMinecraft().thePlayer;
		facing=getFacing(player);
		String posY="";
		if(config.useColor){
			int y=pos.getY();
			if( 4<y && y<12 ) posY = APILog.TextFormatting.AQUA;
			else if( 11<y && y<23 ) posY = APILog.TextFormatting.BLUE;
			else if( 22<y && y<29 ) posY = APILog.TextFormatting.YELLOW;
			posY=posY+y+APILog.TextFormatting.RESET;
		}else
			posY=String.valueOf(pos.getY());
		String result=config.displayFormat;
		result=result.replaceAll("\\{X\\}",String.valueOf(pos.getX()))
				.replaceAll("\\{Y\\}",posY)
				.replaceAll("\\{Z\\}",String.valueOf(pos.getZ()))
				.replaceAll("\\{F\\}",facing);
		return result;
	}
	
	//Copied from Minecraft MathHelper 
	public int floor_double(double value){
        int i = (int)value;
        return value < (double)i ? i - 1 : i;
    }
	
	private String getFacing(Entity entity){
		if(entity==null) return "?";
		// .v => .rotationYaw
        double rotation = (entity.v - 90) % 360;
        if (rotation < 0) {
            rotation += 360.0;
        }
         if (0 <= rotation && rotation < 22.5) {
            return "W";
        } else if (22.5 <= rotation && rotation < 67.5) {
            return "NW";
        } else if (67.5 <= rotation && rotation < 112.5) {
            return "N";
        } else if (112.5 <= rotation && rotation < 157.5) {
            return "NE";
        } else if (157.5 <= rotation && rotation < 202.5) {
            return "E";
        } else if (202.5 <= rotation && rotation < 247.5) {
            return "SE";
        } else if (247.5 <= rotation && rotation < 292.5) {
            return "S";
        } else if (292.5 <= rotation && rotation < 337.5) {
            return "SW";
        } else if (337.5 <= rotation && rotation < 360.0) {
            return "W";
        } else {
            return "??";
        }
	}
	
	public class CoordCfg implements IConfig{

		private static final boolean DEFAULT_ENABLED=true,DEFAULT_USECOLOR=true;
		private static final String DEFAULT_DISPLAY="[{X}, {Y}, {Z}] §7[§c{F}§7]§r";
		private static final String DEFAULT_DISPLAYCHAT="{X}, {Y}, {Z}";
		
		private boolean isEnabled=DEFAULT_ENABLED,useColor=DEFAULT_USECOLOR;
		private String displayFormat=DEFAULT_DISPLAY;
		private String chatFormat=DEFAULT_DISPLAYCHAT;
		
		@Override
		public boolean isEnabled() {
			return isEnabled;
		}

		@Override
		public void update(ConfigFile cfg) {
			isEnabled=((Boolean)cfg.get(ConfigFile.key(
					MOD_CATALOG, "enableCoordMod", Boolean.valueOf(DEFAULT_ENABLED),
					"使否顯示所在座標,TAG:{coordinate}"))).booleanValue();
			
			useColor=((Boolean)cfg.get(ConfigFile.key(
					MOD_CATALOG, "useCoordColor", Boolean.valueOf(DEFAULT_USECOLOR),
					"Y 座標顏色提示礦物層"))).booleanValue();
			
			displayFormat=cfg.get(ConfigFile.key(
					MOD_CATALOG, "coordDisplayFormat", DEFAULT_DISPLAY,
					"顯示格式\n{F}:Facing,面向方向 ,{X}{Y}{Z}:Coordinate,座標"));
			
			chatFormat=cfg.get(ConfigFile.key(
					MOD_CATALOG, "coordInChatFormat", DEFAULT_DISPLAYCHAT,
					"快速插入座標顯示格式"));
		}
		
	}
}
