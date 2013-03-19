package analytics.webservice.resources;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import analytics.charts.ChartFactory;


/**
 * This class analyzes a Post request and creates a json response
 * 
 * @author Razvan Nedelcu
 */
public class ChartContentResource extends ServerResource {
	
	@Post
	public String getJsonContent(Representation entity) throws IOException {
		
		String stringUserInput = new Form(this.getRequestEntity()).getValues("userData");
		JSONObject jsonUserInput = JSONObject.fromObject(stringUserInput);
		
		String[] startingDateTokens = jsonUserInput.getString("startingDate").split("/");
		String[] endingDateTokens = jsonUserInput.getString("endingDate").split("/");
		String chartType = jsonUserInput.getString("chartType");
		JSONArray productsObject = jsonUserInput.getJSONArray("products");
		
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.set(Integer.parseInt(startingDateTokens[2]),
					 Integer.parseInt(startingDateTokens[0]),
					 Integer.parseInt(startingDateTokens[1]));
		Date startingDate = calendar.getTime();
		calendar.clear();
		calendar.set(Integer.parseInt(endingDateTokens[2]),
					 Integer.parseInt(endingDateTokens[0]),
					 Integer.parseInt(endingDateTokens[1]));
		Date endingDate = calendar.getTime();
		List<String> products = new ArrayList<String>();
		
		for(int i = 0; i < productsObject.size(); i++) {
			products.add( productsObject.getString(i) );
		}
		
		return ChartFactory.getChartInstance(startingDate, endingDate, products, chartType).getChartData().toString();
	}
}
