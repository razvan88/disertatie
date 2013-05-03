package analytics.analysis.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import weka.associations.Apriori;

import analytics.database.DBBasicOperations;
import analytics.utils.AprioriHelper;
import analytics.utils.DataChanges;


public class Associations {
	private double deltaValue;
	private double lowerBoundMinSupportValue;
	private double minMetricValue;
	private int numRulesValue;
	private double upperBoundMinSupportValue;
	private double support;
	private int data[][], rows = 0;
	private final String dataFileName = "input.in";
	private List<Integer> sortedCodes;
	
	public Associations() {
		this.initiateAlgVariables();
		this.sortedCodes = new ArrayList<Integer>();
	}
	
	private void initiateAlgVariables() {
		//TODO - REFACTOR THIS PART
		this.support = 0.2;
		this.deltaValue = 0.05;
		this.lowerBoundMinSupportValue = 0.1;
		this.minMetricValue = 0.5;
		this.numRulesValue = 20;
		this.upperBoundMinSupportValue = 1.0;
	}
	
	public void buildData() {
		DBBasicOperations db = DBBasicOperations.getInstance();
		db.openConnection();
		HashMap<Integer, List<Integer>> transactions = db.getTransactions();
		HashMap<Integer, String> products = db.getProducts();
		db.closeConnection();
				
		Integer[] productsCodes = new Integer[products.size()];
		products.keySet().toArray(productsCodes);
		Arrays.sort(productsCodes);
		List<Integer> codes = new ArrayList<Integer>(products.keySet());
		Collections.sort(codes);
		List<Integer> transactionKeys = new ArrayList<Integer>(transactions.keySet());
		Collections.sort(transactionKeys);
		this.sortedCodes = codes;
		this.data = new int[transactions.size()][];
		
		for(int transactionCode : transactionKeys) {
			int[] row = AprioriHelper.getDataRow(transactions.get(transactionCode), codes);
			this.data[this.rows++] = row;
		}
		
		DataChanges.writeDataToFile(this.dataFileName, this.data);
	}
	
	/**
	 * @return a HashMap that has as key a string containing comma-separated columns' indexes
	 * and as values the support values
	 */
	public HashMap<String, Double> runAlgorithm() {
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
				if(supp < this.support) {
					combinations = AprioriHelper.removeColumnsCombination(combinations, combination, belowSupp);
					i--;
				} else {
					if (!pairs.containsKey(combination)) {
						pairs.put(combination, supp);
					} else {
						throw new RuntimeException("Try to add duplicate key in support dictionary!");
					}
				}
			}
		}
		
		return pairs;
	}
	
	public List<List<String>> getResults(HashMap<String, Double> rawResult) {
		List<List<String>> products = new ArrayList<List<String>>();
		
		DBBasicOperations db = DBBasicOperations.getInstance();
		db.openConnection();

		for(String columns : rawResult.keySet()) {
			Integer[] cols = DataChanges.getIntFromString(columns);
			List<String> prodNames = db.getNamesForProducts(
					this.getCodesAtIndexes(cols, this.sortedCodes));
			prodNames.add(rawResult.get(columns).toString());
			products.add(prodNames);
		}
		db.closeConnection();
		
		return products;
	}
	
	private Integer[] getCodesAtIndexes(Integer[] indexes, List<Integer> codes) {
		Integer[] filteredCodes = new Integer[indexes.length];
		List<Integer> filteredCodesList = new ArrayList<Integer>();
		
		for(Integer index : indexes) {
			filteredCodesList.add(codes.get(index));
		}
		
		filteredCodesList.toArray(filteredCodes);
		return filteredCodes;
	}
	
	public static void main(String[] args) {
		Associations alg = new Associations();
		alg.buildData();
		
		HashMap<String, Double> res = alg.runAlgorithm();
		List<List<String>> products = alg.getResults(res);
		
		AprioriHelper.displayResults(products, alg.support);
	}
}
