package analytics.webservice.resources.analysis;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import analytics.analysis.algorithms.AprioriAlgorithm;
import analytics.analysis.algorithms.Associations;

public class ProductsAssociations extends ServerResource {

	@Post
	public String getJsonAssociations(Representation entity) {
		String stringUserInput = new Form(this.getRequestEntity()).getValues("products");
		JSONObject jsonUserInput = JSONObject.fromObject(stringUserInput);
		
		JSONArray baseProds = jsonUserInput.getJSONArray("baseProds");
		List<String> products = new ArrayList<String>();
		for(Object prod : baseProds) {
			products.add((String)prod);
		}
		
		boolean considerCoeff = jsonUserInput.getBoolean("considerCoeff");
		
		Associations algorithm = AprioriAlgorithm.getInstance();
		JSONObject resObj = new JSONObject();
		
		if(considerCoeff) {
			double coeff = jsonUserInput.getDouble("coeff");
			List<List<String>> result = algorithm.getResults(algorithm.runAlgorithm(coeff));
			resObj.put("prods", result);
			resObj.put("coeff", coeff);
		} else {
			double support = algorithm.getSupportForProducts(products);
			resObj.put("prods", baseProds);
			NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
			DecimalFormat df = (DecimalFormat)nf;
			df.applyPattern("#.####");
			resObj.put("coeff", df.format(support));
		}
		
		return resObj.toString();
	}
}
