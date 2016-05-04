package tw.darkk6.meddle.infoline.mod;

import net.fybertech.meddleapi.ConfigFile;

public abstract class IModBase {
	
	public IModBase(ConfigFile cfg){ }
	
	public boolean isEnabled(){
		return getConfig().isEnabled();
	}
	
	public void update(ConfigFile cfg){
		this.getConfig().update(cfg);
	}
	
	/**  Abstract methods  **/
	public abstract String parseResult(String str);
	protected abstract IConfig getConfig();
	
	//==== Config Interface ====
	public interface IConfig{
		boolean isEnabled();
		void update(ConfigFile cfg);
	}
}
