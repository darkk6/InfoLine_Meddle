package tw.darkk6.meddle.infoline;

import java.util.ArrayList;

import net.fybertech.meddle.MeddleMod;
import net.fybertech.meddleapi.ConfigFile;
import net.fybertech.meddleapi.MeddleAPI;
import net.fybertech.meddleapi.side.ClientOnly;
import tw.darkk6.meddle.api.ClientEventAPI;
import tw.darkk6.meddle.infoline.mod.Biome;
import tw.darkk6.meddle.infoline.mod.Clock;
import tw.darkk6.meddle.infoline.mod.Coordinate;
import tw.darkk6.meddle.infoline.mod.Health;
import tw.darkk6.meddle.infoline.mod.IModBase;
import tw.darkk6.meddle.infoline.mod.Light;
import tw.darkk6.meddle.infoline.proxy.ClientProxy;
import tw.darkk6.meddle.infoline.proxy.CommonProxy;
import tw.darkk6.meddle.infoline.util.Reference;

@ClientOnly
@MeddleMod(depends={"dynamicmappings", "meddleapi","clienteventapi"},id=Reference.MODID, name=Reference.MOD_NAME, version=Reference.MOD_VER, author="darkk6")
public class InfoLineMod {
	public static CommonProxy proxy = (CommonProxy) MeddleAPI.createProxyInstance(CommonProxy.class.getName(), ClientProxy.class.getName());
	
	// loadModsWithConfig 會由 Config 的 private void reload 呼叫
	// 寫在這裡的目的是比較好控
	public static ArrayList<IModBase> modList;;
	public static void loadModsWithConfig(ConfigFile cfg){
		if(modList==null){
			modList=new ArrayList<IModBase>();
			//包含初始化與載入設定檔案
			modList.add(new Clock(cfg));
			modList.add(new Light(cfg));
			modList.add(new Coordinate(cfg));
			modList.add(new Biome(cfg));
			modList.add(new Health(cfg));
		}else{
			//已經載入 Mod , 只要更新就好
			for(IModBase mod:InfoLineMod.modList)
				mod.update(cfg);
		}
	}
	
	public void init(){
		ClientEventAPI.checkApiVersionWithException("1.3");
		proxy.init();
	}
}
