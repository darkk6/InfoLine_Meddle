package tw.darkk6.meddle.infoline;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import net.fybertech.meddleapi.MeddleClient.IKeyBindingState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import tw.darkk6.meddle.api.listener.IRenderOverlayListener;
import tw.darkk6.meddle.api.util.APILog;
import tw.darkk6.meddle.infoline.mod.Coordinate;
import tw.darkk6.meddle.infoline.mod.IModBase;
import tw.darkk6.meddle.infoline.util.Config;
import tw.darkk6.meddle.infoline.util.Reference;

public class EventHandler implements IRenderOverlayListener,IKeyBindingState{

	@Override
	public void onRenderOverlayStart(){}
	
	@Override
	public void onRenderOverlayEnd(){
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
	
	@SuppressWarnings("unchecked")
	private void doInTick(){
		if(!Config.isEnabled) return;
		if(Minecraft.getMinecraft()==null) return;
		if(Minecraft.getMinecraft().theWorld==null) return;
		// m => currentScreen
		GuiScreen gui=Minecraft.getMinecraft().m;
		if(gui!=null && getGuiChatClass()!=null){
			if(guiChatClass.isAssignableFrom(gui.getClass()))
				checkAndInsertCoord(gui);
		}
		
		String textToDraw=Config.displayString;
		//跑遍每一個 mod 取代要顯示的文字
		for(IModBase mod:InfoLineMod.modList){
			textToDraw=mod.parseResult(textToDraw);
		}
		String[] arr=textToDraw.split("\\{[Nn]\\}");
		drawText(Arrays.asList(arr));
	}
	
	
	private Class classGuiTextField;
	private Field inputfield;
	private Method writeText;
	private boolean hasF1Released=true;
	@SuppressWarnings("unchecked")
	private void checkAndInsertCoord(Object guiChatObj){
		//檢查 F1 是否按下，將座標插入對話區
		if(!hasF1Released){
			if(!Keyboard.isKeyDown(Keyboard.KEY_F1)) hasF1Released=true;
			return;
		}
		if(!Keyboard.isKeyDown(Keyboard.KEY_F1)) return;
		hasF1Released=false;
		// bda => GuiTextField
		// .a => inputField [ in class GuiChat]
		String xyz=Coordinate.instance.getCoordInChatStr();
		if(xyz==null) return;
		try{
			if(inputfield==null) inputfield=guiChatClass.getDeclaredField("a");
			if(classGuiTextField==null) classGuiTextField=Class.forName("bdb");
			if(writeText==null) writeText=classGuiTextField.getMethod("b",String.class);
			inputfield.setAccessible(true);
			Object iptField = inputfield.get(guiChatObj);
			writeText.invoke(iptField,xyz);
		}catch(Exception e){
			APILog.error("Can not insert coordinate",Reference.LOG_TAG);
		}
	}
	
	private void drawText(List<String> strList){
		//計算出出現位置
		Minecraft mc = Minecraft.getMinecraft();
		// k => fontRendererObj
		FontRenderer render = mc.k;
		int[] tmp=getScaledWidthHeight();
		int width = tmp[0];
		int height = tmp[1];
		
		int lines=strList.size();
		int idx=0;
		for(String text:strList){
			int strWidth = render.getStringWidth(text);
			// a => FONT_HEIGHT
			int strHeight = render.a;
			int x = Config.xOffset, y = Config.yOffset;
			
			switch(Config.position){
				case Reference.POS_TOP_LEFT:
					y = ( (strHeight + Config.lineGap) * idx) + y;
					break;
				case Reference.POS_TOP_RIGHT:
					x = width - strWidth - x;
					y = ( (strHeight + Config.lineGap) * idx) + y;
					break;
				case Reference.POS_BOTTOM_LEFT:
					y = height - (strHeight+Config.lineGap)*(lines-idx) - y;
					break;
				case Reference.POS_BOTTOM_RIGHT:
					x = width - strWidth - x;
					y = height - (strHeight+Config.lineGap)*(lines-idx) - y;
					break;
			}
			// drawStringWithShadow or drawString => a
			render.a(text, x, y, 0xFFFFFFFF, true);
			idx++;
		}
	}
	
	private Class guiChatClass=null;
	private Class getGuiChatClass(){
		if(guiChatClass==null){
			try{guiChatClass=Class.forName("bec");}
			catch(Exception e){}
		}
		return guiChatClass;
	}
	
	// cache Objects
	private Constructor srConstructor;
	private Method getScaledWidth,getScaledHeight;
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private int[] getScaledWidthHeight(){
		try{
			Class cls=Class.forName("bcv");
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
