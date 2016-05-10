package tw.darkk6.meddle.infoline.mod;

import net.fybertech.meddleapi.ConfigFile;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class Health extends IModBase {


	public static final String MOD_CATALOG="health";
	
	private HealthCfg config; 
	public Health(ConfigFile cfg) {
		super(cfg);
		config=new HealthCfg();
		config.update(cfg);
	}

	@Override
	public String parseResult(String str) {
		if(str.indexOf("{health}")==-1) return str;
		return str.replaceAll("\\{health\\}",getHealthString());
	}

	private String getHealthString(){
		if(!config.isEnabled()) return "";
		String fmt=config.displayFormat;
		//格式中沒出現 {A},{M},{H}就直接結束
		if(!fmt.matches(".*\\{[AMH]\\}.*")) return "";
		Minecraft mc=Minecraft.getMinecraft();
		// i => pointedEntity
		Entity e=mc.i;
		if(e==null) return "";
		if(!(e instanceof EntityLivingBase)) return "";
		EntityLivingBase entity=(EntityLivingBase)e;
		// getCommandSenderName => getName in Entity
		String name = entity.getCommandSenderName();
		// bR , bX => getHealth , getMaxHealth in EntityLivingBase
		int now = (int)Math.ceil(entity.bR());
		int max = (int)Math.ceil(entity.bX());
		return fmt.replaceAll("\\{A\\}",name.toString())
				.replaceAll("\\{H\\}", String.valueOf(now))
				.replaceAll("\\{M\\}", String.valueOf(max));
	}
	
	@Override
	protected IConfig getConfig() {
		return config;
	}
	
	public class HealthCfg implements IConfig{

		private static final boolean DEFAULT_ENABLED=true;
		private static final String DEFAULT_DISPLAY="{A}:{H}/{M}";
		
		private boolean isEnabled=DEFAULT_ENABLED;
		private String displayFormat=DEFAULT_DISPLAY;
		
		@Override
		public boolean isEnabled() {
			return isEnabled;
		}

		@Override
		public void update(ConfigFile cfg) {
			isEnabled=((Boolean)cfg.get(ConfigFile.key(
					MOD_CATALOG, "enableHealthMod", Boolean.valueOf(DEFAULT_ENABLED),
					"使否顯示生物血量資訊 ,TAG:{health}"))).booleanValue();
			
			displayFormat=cfg.get(ConfigFile.key(
					MOD_CATALOG, "healthDisplayFormat", DEFAULT_DISPLAY,
					"顯示格式\n{A}:Name,生物名稱    {M}:MaxHP,最大血量 , {H}:Current HP,目前血量"));
			
		}
		
	}

}
