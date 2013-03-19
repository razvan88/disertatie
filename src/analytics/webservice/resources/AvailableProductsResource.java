package analytics.webservice.resources;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import net.sf.json.JSONObject;

import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import analytics.database.DBBasicOperations;

/**
 * This class returns, in form of a json object, all the products from database
 * 
 * @author Razvan Nedelcu
 *
 */
public class AvailableProductsResource extends ServerResource {
	
	@Get
	public String getJsonProducts(Representation entity) {
		DBBasicOperations dataBase = DBBasicOperations.getInstance();
		dataBase.openConnection();
		
		List<String> products = dataBase.getAvailableProducts();
		JSONObject jsonObj = new JSONObject();
		
		String[] sortedProducts = new String[products.size()];
		products.toArray(sortedProducts);
		Arrays.sort(sortedProducts, new Comparator<String>() {
			public int compare(String s1, String s2){
				return s1.compareTo(s2);
			}
		});
		
		for(String product : sortedProducts) {
			if(!product.startsWith("*"))
				jsonObj.accumulate("productNames", product);
		}
		
		dataBase.closeConnection();
		
		return jsonObj.toString();
	}

}
