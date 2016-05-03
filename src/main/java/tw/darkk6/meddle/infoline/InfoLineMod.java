package tw.darkk6.meddle.infoline;

import net.fybertech.meddle.MeddleMod;
import net.fybertech.meddleapi.MeddleAPI;
import net.fybertech.meddleapi.side.ClientOnly;
import tw.darkk6.meddle.api.ClientEventAPI;
import tw.darkk6.meddle.infoline.proxy.ClientProxy;
import tw.darkk6.meddle.infoline.proxy.CommonProxy;
import tw.darkk6.meddle.infoline.util.Reference;

@ClientOnly
@MeddleMod(depends={"dynamicmappings", "meddleapi","clienteventapi"},id=Reference.MODID, name=Reference.MOD_NAME, version=Reference.MOD_VER, author="darkk6")
public class InfoLineMod {
	public static CommonProxy proxy = (CommonProxy) MeddleAPI.createProxyInstance(CommonProxy.class.getName(), ClientProxy.class.getName());
	
	public void init(){
		ClientEventAPI.checkApiVersionWithException("1.1");
		proxy.init();
	}
}
