package analytics.webservice.application;


import org.restlet.*;
import org.restlet.routing.Router;

import analytics.webservice.resources.analysis.MenuCategoriesResource;
import analytics.webservice.resources.analysis.MenuCreationResource;
import analytics.webservice.resources.analysis.ProductsAssociationsResource;
import analytics.webservice.resources.analysis.ProductsDeterminationsResource;
import analytics.webservice.resources.reports.AvailableProductsResource;
import analytics.webservice.resources.reports.ChartContentResource;

/**
 * Used to create a root restlet that will receive all the
 * incoming requests
 * 
 * @author Razvan Nedelcu
 */
public class WebserviceDispatcher extends Application{
	
	@Override
	public synchronized Restlet createInboundRoot() {
		Router router = new Router(getContext());
		
		router.attach("/chartContent", ChartContentResource.class);
		router.attach("/productsContent", AvailableProductsResource.class);
		
		router.attach("/productsAssociations", ProductsAssociationsResource.class);
		router.attach("/productsDeterminations", ProductsDeterminationsResource.class);
		
		router.attach("/productsCategories", MenuCategoriesResource.class);
		router.attach("/menuCreation", MenuCreationResource.class);
		
		return router;
	}
}
