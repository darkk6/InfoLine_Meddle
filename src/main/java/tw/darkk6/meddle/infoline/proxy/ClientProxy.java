package tw.darkk6.meddle.infoline.proxy;

import java.io.File;

import net.fybertech.meddle.Meddle;
import net.fybertech.meddleapi.MeddleClient;
import tw.darkk6.meddle.api.EventRegister;
import tw.darkk6.meddle.infoline.EventHandler;
import tw.darkk6.meddle.infoline.util.Config;
import tw.darkk6.meddle.infoline.util.Reference;

public class ClientProxy extends CommonProxy {
	
	private EventHandler eventhandler;
	
	@Override
	public void init(){
		Config.instance=new Config(new File(Meddle.getConfigDir(),Reference.MODID+".cfg"));
		eventhandler=new EventHandler();
		EventRegister.addRenderTickListener(eventhandler);
		MeddleClient.registerKeyBindStateHandler(eventhandler);
	}
}
