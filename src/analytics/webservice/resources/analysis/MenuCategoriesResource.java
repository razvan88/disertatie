package analytics.webservice.resources.analysis;

import java.util.List;

import net.sf.json.JSONObject;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import analytics.utils.ConfigurationSettings;

public class MenuCategoriesResource extends ServerResource{
	
	@Get
	public String getMenuCategories() {
		ConfigurationSettings config = ConfigurationSettings.getInstance();
		List<String> categories = config.getSectionValues("prodCategories");
		JSONObject json = new JSONObject();
		for(String category : categories) {
			json.accumulate("categoriesNames", category);
		}
		return json.toString();
	}
}
