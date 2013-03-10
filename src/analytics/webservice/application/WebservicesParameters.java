package analytics.webservice.application;

/**
 * Interface used for storing as final strings
 * the parameters used for customizing the webservice
 * 
 * @author Razvan Nedelcu
 */
public interface WebservicesParameters {
	int HTTP_PORT = 31888;
	
	String MAX_THREADS_KEY = "maxThreads";
	String MAX_THREADS_VALUE = "512";
}
