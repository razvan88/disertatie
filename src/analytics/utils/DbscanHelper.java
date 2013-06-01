package analytics.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import analytics.database.DBBasicOperations;

public class DbscanHelper {
	private static final String categoriesSection = "prodCategories";
	private static final String weightsSection = "prodWeights";
	private static List<String> categories;
	private static List<Integer> weights;
	private static DbscanHelper instance;
	private static final String epsilon;
	public static final int minElems;
	public static final int minClusterPoints;
	private static HashMap<Integer, List<Integer>> bills;
	private static DBBasicOperations dataBase;
	
	private boolean isEpsilonNumeric;
	private int numericEpsilon;
	private boolean hasEpsilonMathFn;
	private boolean hasEpsilonXPercent;
	private boolean hasEpsilonYPercent;
	private String epsilonMathFn;
	private int[] epsilonInterval;
	
	//TODO -remove this as it will be received from GUI
	public static final int menuItemsNo;
	
	static{
		weights = new ArrayList<Integer>();
		dataBase = DBBasicOperations.getInstance();
		ConfigurationSettings config = ConfigurationSettings.getInstance();
		epsilon = config.getValue("clustering", "epsilon");
		minElems = Integer.parseInt(config.getValue("clustering", "minNeighbors"));
		minClusterPoints = Integer.parseInt(config.getValue("clustering", "minClusterPoints"));
		menuItemsNo = Integer.parseInt(config.getValue("menu", "menuItems"));
		buildData();
		
		dataBase.openConnection();
		bills = dataBase.getTransactions();
		dataBase.closeConnection();
		
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
	
	private boolean listContains(List<Integer> elems, int val) {
		for(Integer elem : elems) {
			if(elem.intValue() == val) {
				return true;
			}
		}
		
		return false;
	}
	
	private int listIndexOf(List<Integer> elems, int val) {
		for(Integer elem : elems) {
			if(elem.intValue() == val) {
				return elems.indexOf(elem);
			}
		}
		
		return -1;
	}
	
	private int listMinValue(List<Integer> elems) {
		int min = Integer.MAX_VALUE;
		for(Integer elem : elems) {
			if(elem.intValue() < min) {
				min = elem.intValue();
			}
		}
		return min;
	}
	
	private List<Integer> listGetMaximumValuesIndexes(List<Integer> elems, int no) {
		List<Integer> indexes = new ArrayList<Integer>();
		boolean ready = false;
		int peak = Integer.MIN_VALUE;
		int maxValue = 0;
		int minValue = this.listMinValue(elems);
		
		//compute the peak value
		for(Integer elem : elems) {
			if(elem.intValue() >= peak) {
				peak = elem.intValue();
			}
		}
		
		maxValue = peak;
		while(!ready) {
			for(int i = 0; i < elems.size(); i++) {
				Integer elem = elems.get(i).intValue();
				//always take the maximum values
				if(elem == maxValue) {
					indexes.add(i);
					if(--no == 0) {
						ready = true;
						break;
					}
				}
			}
			maxValue--;
			if(maxValue < minValue) {
				break;
			}
		}
		
		return indexes;
	}
	
	public List<String> getMostConsumedProducts(List<Integer> billCodes, int howMany) {
		List<Integer> productsCodes = new ArrayList<Integer>();
		List<Integer> productsSales = new ArrayList<Integer>();
		
		for(int billCode : billCodes) {
			List<Integer> prods = bills.get(billCode);
			for(int prodCode : prods) {
				if(!this.listContains(productsCodes, prodCode)) {
					productsCodes.add(prodCode);
				}
				int prodIndex = this.listIndexOf(productsCodes, prodCode);
				if(productsSales.size() < prodIndex + 1) {
					productsSales.add(prodIndex, new Integer(1));
				}  else {
					int val = productsSales.get(prodIndex).intValue();
					productsSales.remove(prodIndex);
					productsSales.add(prodIndex, new Integer(val) + 1);
				}
			}
		}
		
		List<Integer> pozs = this.listGetMaximumValuesIndexes(productsSales, howMany);
		List<Integer> codes = new ArrayList<Integer>();
		for(int poz : pozs) {
			codes.add(productsCodes.get(poz).intValue());
		}
		
		return this.getProductNames(codes);
	}
	
	private List<String> getProductNames(List<Integer> products) {
		List<String> names;
		Integer[] prods = new Integer[products.size()];
		products.toArray(prods);
		
		dataBase.openConnection();
		names = dataBase.getNamesForProducts(prods);
		dataBase.closeConnection();
		
		return names;
	}
}
