package analytics.webservice.resources;

import java.util.List;

import net.sf.json.JSONObject;

import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import analytics.database.DBBasicOperations;

/**
 * This class returns, in form of a json object, all the products from database
 * 
 * @author Razvan Nedelcu
 *
 */
public class AvailableProductsResource extends ServerResource {
	
	@Post
	public JSONObject getJsonProducts() {
		DBBasicOperations dataBase = DBBasicOperations.getInstance();
		dataBase.openConnection();
		
		List<String> products = dataBase.getAvailableProducts();
		JSONObject jsonObj = new JSONObject();
		
		for(String product : products) {
			jsonObj.accumulate("productNames", product);
		}
		
		dataBase.closeConnection();
		
		return jsonObj;
	}

}
