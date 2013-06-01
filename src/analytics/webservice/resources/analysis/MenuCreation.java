package analytics.webservice.resources.analysis;

import java.util.List;

import net.sf.json.JSONObject;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import analytics.analysis.algorithms.dbscan.Clustering;
import analytics.analysis.algorithms.dbscan.DBSCAN;

public class MenuCreation extends ServerResource{

	/*
	 * userData={menuItemsNo: 3}
	 */
	
	@Get
	public String getMenus() {
		String stringJson = this.getQuery().getValues("userData");
		JSONObject json = JSONObject.fromObject(stringJson);
		int menuItemsNo = json.getInt("menuItemsNo");
		
		Clustering dbscan = DBSCAN.getInstance();
		List<List<String>> menus = dbscan.getMostConsumedProductsForEachCluster(menuItemsNo);
		
		JSONObject allMenus = new JSONObject();
		for(List<String> products : menus) {
			JSONObject menu = new JSONObject();
			for(String product : products) {
				if(products.indexOf(product) > 0) {
					menu.accumulate("products", product);
				} else {
					menu.put("category", product);
				}
			}
			allMenus.accumulate("menus", menu);
		}
		
		return allMenus.toString();
	}
}
