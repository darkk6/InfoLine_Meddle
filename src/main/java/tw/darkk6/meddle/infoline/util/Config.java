package tw.darkk6.meddle.infoline.util;

import java.io.File;

import net.fybertech.meddleapi.ConfigFile;
import tw.darkk6.meddle.infoline.InfoLineMod;

public class Config {
	
	public static Config instance;
	
	public static int position=0;//左上角，參考上面
	public static int xOffset=5,yOffset=5,lineGap=0;
	public static boolean isEnabled=true;
	public static String displayString="{clock} {coordinate} {biome}{N}{light}";
	
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
		
		isEnabled=((Boolean)cfg.get(ConfigFile.key(
				"general", "enableInfoLine", Boolean.valueOf(isEnabled),
				"啟用 InfoLine Mod"))).booleanValue();
		
		lineGap=((Integer)cfg.get(ConfigFile.key(
				"general", "lineGap", Integer.valueOf(lineGap),
				"多行顯示時的行距間隙"))
				).intValue();
		
		if(lineGap>5 || lineGap<0) lineGap=0;
		
		position=((Integer)cfg.get(ConfigFile.key(
				"general", "showPosition", Integer.valueOf(position),
				"顯示位置\nTOP_LEFT=0 , TOP_RIGHT=1 , BOTTOM_LEFT=2 , BOTTOM_RIGHT=3"))
				).intValue();
		
		if(position<0 || position>3) position=0;
		
		xOffset=((Integer)cfg.get(ConfigFile.key(
				"general", "OffsetX", Integer.valueOf(xOffset),
				"與邊界的水平距離"))).intValue();
		
		yOffset=((Integer)cfg.get(ConfigFile.key(
				"general", "OffsetY", Integer.valueOf(yOffset),
				"與邊界的垂直距離"))).intValue();
		
		displayString=cfg.get(ConfigFile.key(
				"general","displayString",displayString,
				"顯示的文字樣板，使用{N}代表換行"));
		
		//====== Internal =======
		defalutWidth=((Integer)cfg.get(ConfigFile.key(
				"internal", "ScreenWidth", Integer.valueOf(defalutWidth),
				"無法取得視窗大小時，預設的視窗寬度"))).intValue();
		
		defalutHeight=((Integer)cfg.get(ConfigFile.key(
				"internal", "ScreenHeight", Integer.valueOf(defalutHeight),
				"無法取得視窗大小時，預設的視窗高度"))).intValue();
		
		if(defaultResolution==null) defaultResolution=new int[2];
		defaultResolution[0]=defalutWidth;
		defaultResolution[1]=defalutHeight;
		
		//呼叫 InfoLineMod.loadModsWithConfig 載入所有 mod
		InfoLineMod.loadModsWithConfig(cfg);
		
		cfg.save();
		//if(cfg.hasChanged()) cfg.save();
		//if(!file.exists()) cfg.save();
	}
	
}
