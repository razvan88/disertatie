package analytics.webservice.application;

import org.restlet.*;
import org.restlet.data.Protocol;

import static analytics.webservice.application.WebservicesParameters.*;

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
	
	private WebserviceApp() {}
	
	public static WebserviceApp getInstance() {
		return webservice;
	}
	
	public void startWebservice() throws Exception {
		if(component.isStarted())
			return;
		
		Server server = component.getServers().add(Protocol.HTTP, HTTP_PORT);  
		server.getContext().getParameters().add(MAX_THREADS_KEY, MAX_THREADS_VALUE); 
		component.getDefaultHost().attach( new WebserviceDispatcher());  
		component.start();
	}
	
	public void stopWebservice() throws Exception {
		if (component.isStopped())
			return;
		
		component.stop();
	}
}
