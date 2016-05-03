package tw.darkk6.meddle.infoline.util;

import java.io.File;

import net.fybertech.meddleapi.ConfigFile;

public class Config {
	public static final int TOP_LEFT=0;
	public static final int TOP_RIGHT=1;
	public static final int BOTTOM_LEFT=2;
	public static final int BOTTOM_RIGHT=3;
	
	public static final int MC_TIME=0;
	public static final int MC_TICK=1;
	public static final int REAL_TIME=2;
	
	public static Config instance;
	
	public static int position=0;//左上角，參考上面
	public static int mode=0;//哪種時間
	public static int xOffset=5,yOffset=5;
	public static boolean isEnable=true , notShowInGUI=false , useColor=true;
	
	//=== internal ===
	public static int[] defaultResolution;
	private static int defalutWidth=854;
	private static int defalutHeight=480;
	
	private long lastModify=0L;
	private File file;
	
	public Config(File file){
		this.file=file;
		reload();
		lastModify = file.lastModified();
	}
	
	public boolean update(){
		if(lastModify != file.lastModified()){
			reload();
			lastModify = file.lastModified();
			return true;
		}
		return false;
	}
	
	private void reload(){
		ConfigFile cfg=new ConfigFile(file);
		cfg.load();
		
		isEnable=((Boolean)cfg.get(ConfigFile.key(
				"general", "enableInfoLine", Boolean.valueOf(isEnable),
				"啟用 InfoLine Mod"))).booleanValue();
		
		notShowInGUI=((Boolean)cfg.get(ConfigFile.key(
				"general", "doNotShowWhenGUIOpen", Boolean.valueOf(notShowInGUI),
				"啟用開啟 GUI 時不要顯示"))).booleanValue();
		
		useColor=((Boolean)cfg.get(ConfigFile.key(
				"general", "useColoredText", Boolean.valueOf(useColor),
				"使用上色文字(晚上白天等顏色不同)"))).booleanValue();
		
		position=((Integer)cfg.get(ConfigFile.key(
				"general", "showPosition", Integer.valueOf(position),
				"顯示位置\nTOP_LEFT=0 , TOP_RIGHT=1 , BOTTOM_LEFT=2 , BOTTOM_RIGHT=3"))
				).intValue();
		
		if(position<0 || position>3) position=0;
		
		mode=((Integer)cfg.get(ConfigFile.key(
				"general", "showMode", Integer.valueOf(mode),
				"顯示位置\n0:Minecraft time , 1:Minecraft ticks , 2:Real time"))
				).intValue();
		
		if(mode<0 || mode>2) mode=0;
		
		xOffset=((Integer)cfg.get(ConfigFile.key(
				"general", "OffsetX", Integer.valueOf(xOffset),
				"與邊界的水平距離"))).intValue();
		
		yOffset=((Integer)cfg.get(ConfigFile.key(
				"general", "OffsetY", Integer.valueOf(yOffset),
				"與邊界的垂直距離"))).intValue();
		
		defalutWidth=((Integer)cfg.get(ConfigFile.key(
				"internal", "ScreenWidth", Integer.valueOf(defalutWidth),
				"無法取得視窗大小時，預設的視窗寬度"))).intValue();
		
		defalutHeight=((Integer)cfg.get(ConfigFile.key(
				"internal", "ScreenHeight", Integer.valueOf(defalutHeight),
				"無法取得視窗大小時，預設的視窗高度"))).intValue();
		
		if(defaultResolution==null) defaultResolution=new int[2];
		defaultResolution[0]=defalutWidth;
		defaultResolution[1]=defalutHeight;
		
		if(cfg.hasChanged()) cfg.save();
	}
	
}
