package analytics.webservice.resources;


import java.io.IOException;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import analytics.charts.ChartFactory;
import analytics.utils.DateUtils;


/**
 * This class analyzes a Post request and creates a json response
 * 
 * @author Razvan Nedelcu
 */
public class ChartContentResource extends ServerResource {
	
	@SuppressWarnings("unchecked")
	@Post
	public String getJsonContent(Representation entity) throws IOException {
		String stringUserInput = new Form(this.getRequestEntity()).getValues("userData");
		JSONObject jsonUserInput = JSONObject.fromObject(stringUserInput);
		
		String firstDate = jsonUserInput.getString("startingDate");
		String secondDate = jsonUserInput.getString("endingDate");
		String chartType = jsonUserInput.getString("chartType");
		JSONArray productsObject = jsonUserInput.getJSONArray("products");
		JSONArray daysFilterObject = jsonUserInput.getJSONArray("daysFilter");
		
		DateUtils dateUtils = new DateUtils(firstDate, secondDate);
		dateUtils.sortDates();
		Date startingDate = dateUtils.getStartingDate();
		Date endingDate = dateUtils.getEndingDate();
		
		List<String> products = (List<String>)JSONArray.toList(productsObject);
		List<String> daysFilter = (List<String>)JSONArray.toList(daysFilterObject);
		
		return ChartFactory.getChartInstance(startingDate, endingDate, 
				daysFilter, products, chartType).getChartData().toString();
	}
}
