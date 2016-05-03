package tw.darkk6.meddle.infoline;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.fybertech.meddleapi.MeddleClient.IKeyBindingState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import tw.darkk6.meddle.api.listener.IRenderTickListener;
import tw.darkk6.meddle.api.util.APILog;
import tw.darkk6.meddle.infoline.util.Config;
import tw.darkk6.meddle.infoline.util.Reference;

public class EventHandler implements IRenderTickListener,IKeyBindingState{

	@Override
	public void onRenderTickStart(){}
	
	@Override
	public void onRenderTickEnd(){
		//繪製必須在 RenderEnd 才會生效
		doInTick();
	}
	
	@Override
	public void onsetKeyBindState(int code, boolean state, KeyBinding keybinding) {
		//按下 Esc 的時候重新載入設定檔
		if(state && code==Keyboard.KEY_ESCAPE){
			if(Config.instance.update())
				APILog.infoChat("重新載入設定檔完成", Reference.LOG_TAG);
		}
	}
	
	private void doInTick(){
		/*
		 * 	一天有 24*60 = 1440 分鐘
		 * 	==> 24000/1440 => 16.6_ 為 1 分鐘
		 * 	==> 24000/24 => 每 1000 為 1 小時
		*/
		//試著取得 theWorld.Q() => world.getWorldTime()
		if(!Config.isEnable) return;
		if(Minecraft.getMinecraft()==null) return;
		if(Minecraft.getMinecraft().theWorld==null) return;
		String textToDraw;
		if(Config.mode==Config.REAL_TIME){
			SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
			Date date = new Date();
			textToDraw=dateFormat.format(date);
		}else{
			// m => currentScreen
			GuiScreen gui=Minecraft.getMinecraft().m;
			if(!drawOnGUI(gui)) return;//只畫在指定的 GUI 上
			long time=Minecraft.getMinecraft().theWorld.Q();
			textToDraw=getFormatedTimeString(time);
		}
		drawText(textToDraw);
	}
	
	private void drawText(String text){
		//計算出出現位置
		Minecraft mc = Minecraft.getMinecraft();
		// k => fontRendererObj
		FontRenderer render = mc.k;
		int[] tmp=getScaledWidthHeight();
		int width = tmp[0];
		int height = tmp[1];
		int strWidth = render.getStringWidth(text);
		// a => FONT_HEIGHT
		int strHeight = render.a;
		int x = Config.xOffset, y = Config.yOffset;

		//按照計算，直接使用 x , y 就是左上角，不用特別運算
		switch(Config.position){
			case Config.TOP_RIGHT:
				x = width - strWidth - x;
				break;
			case Config.BOTTOM_LEFT:
				y = height - strHeight - y;
				break;
			case Config.BOTTOM_RIGHT:
				x = width - strWidth - x;
				y = height - strHeight - y;
				break;
		}
		// drawStringWithShadow or drawString => a
		render.a(text, x, y, 0xFFFFFFFF, true);
	}
	
	private String getFormatedTimeString(long time){
		// Config.REAL_TIME 已經判斷過了所以不需要
		time %= 24000;
		int hour = (int)time / 1000;
		int min = (int)Math.floor((time % 1000)/(50f/3f));
		hour=(hour+6) % 24;
		String formatedTime=String.format("%02d:%02d",hour,min);
		if(!Config.useColor)
			return Config.mode==Config.MC_TIME ? formatedTime : String.valueOf(time);
		
		StringBuilder show=new StringBuilder();
		//晚上:GRAY , 黃昏/清晨:GOLD , 白天 : YELLOW
		if(time>=13187L && time<23600L) show.append(APILog.TextFormatting.GRAY);
		else if( time<12540L ) show.append(APILog.TextFormatting.YELLOW);
		else show.append(APILog.TextFormatting.GOLD);
		show.append(Config.mode==Config.MC_TIME ? formatedTime : String.valueOf(time));
		show.append(APILog.TextFormatting.RESET);
		return show.toString();
	}
	
	private Class guiChatClass=null;
	@SuppressWarnings("unchecked")
	private boolean drawOnGUI(GuiScreen gui) {
		if(!Config.notShowInGUI) return true;
		try{ 
			if(guiChatClass==null) guiChatClass=Class.forName("beb");
		}catch(Exception e){ return true; }
		//有開啟 GUI 的話，除非是開啟對話框視窗，否則一律不繪製
		if (gui == null)
			return true;
		if (guiChatClass.isAssignableFrom(gui.getClass()))// gui instanceof GuiChat
			return true;

		return false;
	}
	
	// cache Objects
	private Constructor srConstructor;
	private Method getScaledWidth,getScaledHeight;
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private int[] getScaledWidthHeight(){
		try{
			Class cls=Class.forName("bcu");
			if(srConstructor==null) srConstructor =cls.getConstructor(Minecraft.class);
			if(getScaledWidth==null) getScaledWidth=cls.getMethod("a");
			if(getScaledHeight==null) getScaledHeight=cls.getMethod("b");
			Object srObject = srConstructor.newInstance(Minecraft.getMinecraft());
			int[] result=new int[2];
			result[0] = (Integer)getScaledWidth.invoke(srObject);
			result[1] = (Integer)getScaledHeight.invoke(srObject);
			return result;
		}catch(Exception e){
			APILog.error("Can not get ScaledResolution, use config default", Reference.LOG_TAG);
			return Config.defaultResolution;
		}
	}
}
