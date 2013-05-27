package analytics.webservice.application;

import org.restlet.*;
import org.restlet.data.Protocol;

import analytics.utils.ConfigurationSettings;

/**
 * This class starts and stops the webservice
 * 
 * @author Razvan Nedelcu
 */
public class WebserviceApp {
	private static Component component;
	private static WebserviceApp webservice;
	
	static {
		component = new Component();
		webservice = new WebserviceApp();
	}
	
	private WebserviceApp() { }
	
	public static WebserviceApp getInstance() {
		return webservice;
	}
	
	public void startWebservice() throws Exception {
		if(component.isStarted())
			return;
		
		ConfigurationSettings config = ConfigurationSettings.getInstance();
		int httpPort = Integer.parseInt(config.getValue("server", "httpPort"));
		String maxThreadsKey = config.getValue("server", "maxThreadsKey");
		String maxThreadsVal = config.getValue("server", "maxThreadsValue");
		
		Server server = component.getServers().add(Protocol.HTTP, httpPort);  
		server.getContext().getParameters().add(maxThreadsKey, maxThreadsVal); 
		component.getDefaultHost().attach(new WebserviceDispatcher());  
		component.start();
	}
	
	public void stopWebservice() throws Exception {
		if (component.isStopped())
			return;
		
		component.stop();
	}
}
