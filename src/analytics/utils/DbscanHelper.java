package analytics.utils;

import java.util.List;

public class DbscanHelper {
	private static final String categoriesSection = "prodCategories";
	private static final String weightsSection = "prodWeights";
	private static List<String> categories;
	private static List<Integer> weights;
	private static DbscanHelper instance;
	
	static{
		instance = new DbscanHelper();
		buildData();
	}
	
	private DbscanHelper() {}

	private static void buildData() {
		ConfigurationSettings config = ConfigurationSettings.getInstance();
		categories = config.getSectionValues(categoriesSection);
		List<String> coeffs = config.getSectionValues(weightsSection);
		for(String coeff : coeffs) {
			weights.add(Integer.parseInt(coeff));
		}
	}
	
	public static DbscanHelper getInstance() {
		return instance;
	}
	
	public int getValue(List<String> productsNames) {
		int value = 0;
		
		for(String productName : productsNames) {
			int index = categories.indexOf(productName);
			value += (index > -1 ? weights.get(index) : 0);
		}
		
		return value;
	}
}
