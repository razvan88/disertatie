package analytics.analysis.algorithms.apriori;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import analytics.database.DBBasicOperations;
import analytics.utils.AprioriHelper;
import analytics.utils.DataChanges;


public class Associations {
	private int data[][];
	//private final String dataFileName = "input.in";
	private List<Integer> sortedCodes = null;
	private HashMap<Integer, String> products = null;
	private HashMap<String, Double> results = null;
	
	public Associations() {}
	
	public void buildData() {
		DBBasicOperations db = DBBasicOperations.getInstance();
		db.openConnection();
		HashMap<Integer, List<Integer>> transactions = db.getTransactions();
		HashMap<Integer, String> products = db.getProducts();
		db.closeConnection();
		
		List<Integer> codes = new ArrayList<Integer>(products.keySet());
		Collections.sort(codes);
		
		List<Integer> transactionKeys = new ArrayList<Integer>(transactions.keySet());
		Collections.sort(transactionKeys);
		
		this.sortedCodes = codes;
		this.data = new int[transactions.size()][];
		this.products = products;
		int rows = 0;
		
		for(int transactionCode : transactionKeys) {
			int[] row = AprioriHelper.getDataRow(transactions.get(transactionCode), codes);
			this.data[rows++] = row;
		}
		
		//DataChanges.writeDataToFile(this.dataFileName, this.data);
	}
	
	/**
	 * @return a HashMap that has as key a string containing comma-separated columns' indexes
	 * and as values the support values
	 */
	public HashMap<String, Double> runAlgorithm(double support) {
		HashMap<String, Double> pairs = new HashMap<String, Double>();
		List<String> belowSupp = new ArrayList<String>();
		int columnsNo = this.data[0].length;
		
		for(int dimension = 1; dimension < columnsNo; dimension++) {
			List<String> combinations = AprioriHelper.getAllFilteredCombinations(columnsNo, dimension, belowSupp);
			
			if(combinations.isEmpty()) {
				break;
			}
			
			for(int i = 0; i < combinations.size(); i++) {
				String combination = combinations.get(i);
				String[] columns = combination.split(",");
				int[] cols = DataChanges.getIntFromString(columns);
				double supp = AprioriHelper.getColumnsSupport(this.data, cols);
				if(supp < support) {
					//NOTE: this is not needed anymore as only one entry will be removed
					//combinations = AprioriHelper.removeColumnsCombination(combinations, combination, belowSupp);
					//i--;
					belowSupp.add(combination);
				} else {
					if (!pairs.containsKey(combination)) {
						pairs.put(combination, supp);
					} else {
						throw new RuntimeException("Try to add duplicate key in support dictionary!");
					}
				}
			}
		}
		
		if (this.results == null)
			this.results = pairs;
		
		return pairs;
	}
	
	public List<List<String>> getResults(HashMap<String, Double> rawResult) {
		List<List<String>> products = new ArrayList<List<String>>();
		
		DBBasicOperations db = DBBasicOperations.getInstance();
		db.openConnection();

		for(String columns : rawResult.keySet()) {
			Integer[] cols = DataChanges.getIntFromString(columns);
			List<String> prodNames = db.getNamesForProducts(
					AprioriHelper.getCodesAtIndexes(cols, this.sortedCodes));
			prodNames.add(rawResult.get(columns).toString());
			products.add(prodNames);
		}
		db.closeConnection();
		
		return products;
	}
	
	public double getSupportForProducts(List<String> selectedProducts) {
		return AprioriHelper.getSupportForProducts(selectedProducts, 
				this.products, this.sortedCodes, this.data);
	}
	
	/**
	 * conf(X->Y) = supp(X U Y) / supp(X)
	 * @param baseProducts X
	 * @param determinedProducts Y
	 * @return the confidence
	 */
	public double getConfidenceWithNames(List<String> baseProducts, List<String> determinedProducts) {
		return AprioriHelper.getConfidenceWithNames(baseProducts, determinedProducts, 
				this.products, this.sortedCodes, this.data);
	}
	
	private double getConfidenceWithIndexes(List<Integer> baseProducts, 
			List<Integer> determinedProductsIndexes) {
		return AprioriHelper.getConfidenceWithIndexes(
				baseProducts, determinedProductsIndexes, this.data);
	}
	
	/**
	 * conf(X->Y) = supp(X U Y) / supp(X)
	 * @param baseProducts X
	 * @param confidence confidence coefficient. If 0, all combinations above 0 will be returned
	 * @return Y
	 */
	public HashMap<String, Double> getDeterminedProducts(List<String> baseProducts, double confidence) {
		HashMap<String, Double> confidenceCoefficient = new HashMap<String, Double>();
		List<String> zeroSupp = new ArrayList<String>();
		int columnsNo = this.data[0].length;
		
		List<Integer> codes = AprioriHelper.getIndexForCodes(
				AprioriHelper.getCodesForProducts(this.products, baseProducts),
				this.sortedCodes);
		
		for(int dimension = 1; dimension < columnsNo - codes.size() + 1; dimension++) {
			List<String> columnCombinations = AprioriHelper.getAllCustomCombinations(
					columnsNo, dimension, codes, zeroSupp);
			
			if(columnCombinations.isEmpty()) {
				break;
			}
			
			for(int i = 0; i < columnCombinations.size(); i++) {
				String combination = columnCombinations.get(i);
				String colsComb = combination;
				for(int code : codes) {
					colsComb += ("," + code);
				}
				String[] columns = colsComb.split(",");
				int[] cols = DataChanges.getIntFromString(columns);
				double supp = AprioriHelper.getColumnsSupport(this.data, cols);
				double conf = this.getConfidenceWithIndexes(codes, DataChanges.getListFromArray(cols));

				if(supp != 0 && confidence < conf) {
					if(!confidenceCoefficient.containsKey(combination)) {
						confidenceCoefficient.put(combination, conf);
					} else {
						throw new RuntimeException("Try to add duplicate key in support dictionary!");
					}
				} else {
					zeroSupp.add(combination);
				}
			}
		}
		
		return confidenceCoefficient;
	}
	
	//public static void main(String[] args) {
		//Associations alg = new Associations();
		//alg.buildData();
		
		//double supp = 0.05;
		//HashMap<String, Double> res = alg.runAlgorithm(supp);
		//List<List<String>> products = alg.getResults(res);
		//AprioriHelper.displaySupportResults(products, supp);
		
		//List<String> prods = new ArrayList<String>();
		//prods.add("CAPPUCCINO 180 ML");
		//System.out.println(alg.getSupportForProducts(prods));
		
		//List<String> baseProducts = new ArrayList<String>();
		//baseProducts.add("ESPRESSO 30 ML");
		///List<String> determinedProducts = new ArrayList<String>();
		//determinedProducts.add("CAPPUCCINO 180 ML");
		//System.out.println(alg.getConfidenceWithNames(baseProducts, determinedProducts));
		
		//List<String> prods = new ArrayList<String>();
		//prods.add("CAPPUCCINO 180 ML");
		//double conf = 0.1;
		//List<List<String>> result = alg.getResults(alg.getDeterminedProducts(prods, conf));
		//AprioriHelper.displayConfidenceResults(result, prods, conf);
	//}
}
