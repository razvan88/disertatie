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

import analytics.analysis.algorithms.apriori.AprioriAlgorithm;
import analytics.analysis.algorithms.apriori.Associations;

public class ProductsDeterminationsResource extends ServerResource {

	@Post
	public String getJsonAssociations(Representation entity) {
		String stringUserInput = new Form(this.getRequestEntity()).getValues("products");
		JSONObject jsonUserInput = JSONObject.fromObject(stringUserInput);
		
		JSONArray baseProds = jsonUserInput.getJSONArray("baseProds");
		List<String> baseProducts = new ArrayList<String>();
		for(Object prod : baseProds) {
			baseProducts.add((String)prod);
		}
		
		JSONArray determinedProds = jsonUserInput.getJSONArray("determinedProds");
		List<String> determinedProducts = new ArrayList<String>();
		for(Object prod : determinedProds) {
			determinedProducts.add((String)prod);
		}
		
		boolean considerCoeff = jsonUserInput.getBoolean("considerCoeff");
		
		Associations algorithm = AprioriAlgorithm.getInstance();
		JSONObject resObj = new JSONObject();
		
		if(considerCoeff) {
			double coeff = jsonUserInput.getDouble("coeff");
			List<List<String>> res = algorithm.getResults(algorithm.getDeterminedProducts(baseProducts, coeff));
			resObj.put("baseProds", baseProds);
			resObj.put("coeff", coeff);
			resObj.put("result", res);
		} else {
			double conf = algorithm.getConfidenceWithNames(baseProducts, determinedProducts);
			resObj.put("baseProds", baseProds);
			resObj.put("determProds", determinedProds);
			NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
			DecimalFormat df = (DecimalFormat)nf;
			df.applyPattern("#.####");
			resObj.put("coeff", df.format(conf));
		}
		
		return resObj.toString();
	}
}
