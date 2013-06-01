package analytics.utils;

import java.util.ArrayList;
import java.util.List;

public class DbscanHelper {
	private static final String categoriesSection = "prodCategories";
	private static final String weightsSection = "prodWeights";
	private static List<String> categories;
	private static List<Integer> weights;
	private static DbscanHelper instance;
	private static final String epsilon;
	public static final int minElems;
	
	private boolean isEpsilonNumeric;
	private int numericEpsilon;
	private boolean hasEpsilonMathFn;
	private boolean hasEpsilonXPercent;
	private boolean hasEpsilonYPercent;
	private String epsilonMathFn;
	private int[] epsilonInterval;
	
	static{
		weights = new ArrayList<Integer>();
		ConfigurationSettings config = ConfigurationSettings.getInstance();
		epsilon = config.getValue("clustering", "eps");
		minElems = Integer.parseInt(config.getValue("clustering", "min"));
		buildData();
		
		instance = new DbscanHelper();
	}
	
	private DbscanHelper() {
		try{
			this.numericEpsilon = Integer.parseInt(epsilon);
			this.isEpsilonNumeric = true;
		} catch(NumberFormatException e) {
			isEpsilonNumeric = false;
			this.hasEpsilonMathFn = epsilon.startsWith("max") || epsilon.startsWith("min");
			if(this.hasEpsilonMathFn) {
				this.epsilonMathFn = epsilon.trim().substring(0, 3);
			}
			this.epsilonInterval = this.parseEpsilon();
		}
	}

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
	
	public int getEpsilonValue(int refValue) {
		if(this.isEpsilonNumeric)
			return this.numericEpsilon;
		
		int x = this.hasEpsilonXPercent ? this.epsilonInterval[0] * refValue / 100 : this.epsilonInterval[0];
		int y = this.hasEpsilonYPercent ? this.epsilonInterval[1] * refValue / 100 : this.epsilonInterval[1];
		int eps = x;
		
		if(this.hasEpsilonMathFn) {
			if(this.epsilonMathFn.equalsIgnoreCase("max")) {
				eps = Math.max(x, y);
			}else {
				eps = Math.min(x, y);
			}
		}
		
		return eps;
	}
	
	private int[] parseEpsilon() {
		if(this.isEpsilonNumeric)
			return new int[] {this.numericEpsilon, this.numericEpsilon};
		
		String strX = "", strY = "";
		int x = 0, y = 0;
		String[] vals;
		
		if(this.hasEpsilonMathFn){
			vals = epsilon.substring(epsilon.indexOf("(") + 1, epsilon.indexOf(")")).split(",");	
		} else {
			vals = new String[] {epsilon, epsilon};
		}
		
		strX = vals[0].trim();
		strY = vals[1].trim();
		
		if(strX.contains("%")) {
			this.hasEpsilonXPercent = true;
			x = Integer.parseInt(strX.substring(0, strX.indexOf("%")).trim());
		}else {
			this.hasEpsilonXPercent = false;
			x = Integer.parseInt(strX);
		}
			
		if(strY.contains("%")) {
			this.hasEpsilonYPercent = true;
			y = Integer.parseInt(strY.substring(0, strY.indexOf("%")).trim());
		}else {
			this.hasEpsilonYPercent = false;
			y = Integer.parseInt(strY);
		}
		
		return new int[] {x, y};
	}
}
