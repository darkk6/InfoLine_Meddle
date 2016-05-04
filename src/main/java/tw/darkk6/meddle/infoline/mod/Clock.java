package tw.darkk6.meddle.infoline.mod;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.fybertech.meddleapi.ConfigFile;
import net.minecraft.client.Minecraft;
import tw.darkk6.meddle.api.util.APILog;

public class Clock extends IModBase {

	private static final int MC_TIME=0;
	private static final int MC_TICK=1;
	private static final int REAL_TIME=2;
	
	public static final String MOD_CATALOG="clock";
	
	private ClockCfg config;
	
	public Clock(ConfigFile cfg) {
		super(cfg);
		config=new ClockCfg();
		config.update(cfg);
	}

	@Override
	public String parseResult(String str) {
		if(str.indexOf("{clock}")==-1) return str;
		String clock=getClockString();
		return str.replaceAll("\\{clock\\}", clock);
	}

	@Override
	protected IConfig getConfig() {
		return config;
	}
	
	private String getClockString(){
		/*
		 * 	一天有 24*60 = 1440 分鐘
		 * 	==> 24000/1440 => 16.6_ 為 1 分鐘
		 * 	==> 24000/24 => 每 1000 為 1 小時
		*/
		if(!config.isEnabled()) return "";
		String clockString;
		if(config.clockMode==Clock.REAL_TIME){
			SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
			Date date = new Date();
			clockString=dateFormat.format(date);
		}else{
			// theWorld.Q() => world.getWorldTime()
			long time=Minecraft.getMinecraft().theWorld.Q();
			clockString=getFormatedTimeString(time);
		}
		return clockString;
	}
	
	private String getFormatedTimeString(long time){
		// Config.REAL_TIME 已經判斷過了所以不需要
		time %= 24000;
		int hour = (int)time / 1000;
		int min = (int)Math.floor((time % 1000)/(50f/3f));
		hour=(hour+6) % 24;
		String formatedTime=String.format("%02d:%02d",hour,min);
		if(!config.useColor)
			return config.clockMode==Clock.MC_TIME ? formatedTime : String.valueOf(time);
		
		StringBuilder show=new StringBuilder();
		//晚上:GRAY , 黃昏/清晨:GOLD , 白天 : YELLOW
		if(time>=13187L && time<23600L) show.append(APILog.TextFormatting.GRAY);
		else if( time<12540L ) show.append(APILog.TextFormatting.YELLOW);
		else show.append(APILog.TextFormatting.GOLD);
		show.append(config.clockMode==Clock.MC_TIME ? formatedTime : String.valueOf(time));
		show.append(APILog.TextFormatting.RESET);
		return show.toString();
	}
	
	
	public class ClockCfg implements IConfig{
		
		private boolean isEnabled=true,useColor=true;
		private int clockMode=0;
		
		@Override
		public void update(ConfigFile cfg){
			
			isEnabled=((Boolean)cfg.get(ConfigFile.key(
					MOD_CATALOG, "enableColorMod", Boolean.valueOf(isEnabled),
					"使否顯示現在時間,TAG:{clock}"))).booleanValue();
			
			useColor=((Boolean)cfg.get(ConfigFile.key(
					MOD_CATALOG, "useClockColor", Boolean.valueOf(useColor),
					"使用上色文字(晚上白天等顏色不同)"))).booleanValue();
			
			clockMode=((Integer)cfg.get(ConfigFile.key(
					MOD_CATALOG, "showClockMode", Integer.valueOf(clockMode),
					"顯示方式\n0:Minecraft time , 1:Minecraft ticks , 2:Real time"))
					).intValue();
			
			if(clockMode<0 || clockMode>2) clockMode=0;
		}
		
		
		@Override
		public boolean isEnabled(){ return isEnabled; }
	}
}
