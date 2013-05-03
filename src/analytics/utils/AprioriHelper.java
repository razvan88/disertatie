package analytics.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class AprioriHelper {
	
	public static int[] getDataRow(List<Integer> products, List<Integer> codes) {
		int[] row = new int[codes.size()];
		Integer[] sortedProducts = new Integer[products.size()];
		products.toArray(sortedProducts);
		int currentProductIndex = 0,
				maxProductIndex = sortedProducts.length - 1,
				index = -1;
		Arrays.sort(sortedProducts);
		
		for(int code : codes) {
			index ++;
			
			if(currentProductIndex > maxProductIndex) {
				break;
			}
			
			if(code == (Integer)sortedProducts[currentProductIndex]) {
				row[index] = 1;
				currentProductIndex++;
			}
		}
		
		return row;
	}
	
	public static List<String> getAllFilteredCombinations (int totalColumns, int max, List<String> undesiredCombinations) {
		List<String> allCombinations = new ArrayList<String>();
		
		for(int i = 0; i < totalColumns - max + 1; i++) {
			List<String> combinations = AprioriHelper.getFilteredRecursiveCombinations(i, totalColumns - 1, max, undesiredCombinations);
			for(String combination : combinations) {
				allCombinations.add(combination);
			}
		}
		
		return allCombinations;
	}
	
	private static List<String> getFilteredRecursiveCombinations(int minIndex, int maxIndex, 
			int remainedLength, List<String> undesiredCombinations) {
		List<String> combinations = new ArrayList<String>();
		
		if(undesiredCombinations.contains(((Integer)minIndex).toString()) ||
				remainedLength == 0)
			return combinations;
		
		remainedLength--;
		String partialCombinationPrefix = minIndex + (remainedLength > 0 ? "," : "");
		if(remainedLength == 0)
			combinations.add(partialCombinationPrefix);
		
		int nextRecursiveMinIndex = minIndex + 1;

		while(nextRecursiveMinIndex <= maxIndex && remainedLength > 0) {
			List<String> unprefixed = AprioriHelper.getFilteredRecursiveCombinations(nextRecursiveMinIndex, 
					maxIndex, remainedLength, undesiredCombinations);
			
			for(String partialCombinationUnprefixed : unprefixed) {
				 combinations.add(partialCombinationPrefix + partialCombinationUnprefixed);
			}
			
			nextRecursiveMinIndex += 1;
		}
		
		return combinations;
	}
	
	@Deprecated
	public static List<String> getAllCombinations(int totalColumns, int max) {
		List<String> allCombinations = new ArrayList<String>();
		
		for(int i = 0; i < totalColumns - max + 1; i++) {
			List<String> combinations = AprioriHelper.getRecursiveCombinations(i, totalColumns - 1, max);
			for(String combination : combinations) {
				allCombinations.add(combination);
			}
		}
		
		return allCombinations;
	}
	
	@Deprecated
	private static List<String> getRecursiveCombinations(int minIndex, int maxIndex, int remainedLength) {
		List<String> combinations = new ArrayList<String>();
		
		if(remainedLength == 0)
			return combinations;
		
		remainedLength--;
		String partialCombinationPrefix = minIndex + (remainedLength > 0 ? "," : "");
		if(remainedLength == 0)
			combinations.add(partialCombinationPrefix);
		
		int nextRecursiveMinIndex = minIndex + 1;

		while(nextRecursiveMinIndex <= maxIndex && remainedLength > 0) {
			List<String> unprefixed = AprioriHelper.getRecursiveCombinations(nextRecursiveMinIndex, 
					maxIndex, remainedLength);
			
			for(String partialCombinationUnprefixed : unprefixed) {
				 combinations.add(partialCombinationPrefix + partialCombinationUnprefixed);
			}
			
			nextRecursiveMinIndex += 1;
		}
		
		return combinations;
	}
	
	public static List<String> removeColumnsCombination(List<String> oldList, String combination, List<String> belowSupp) {
		List<String> newList = new ArrayList<String>();
		
		for(String comb : oldList) {
			if(!DataChanges.containsTokens(comb, combination))
				newList.add(comb);
			else
				belowSupp.add(comb);
		}
		
		return newList;
	}
	
	public static double getColumnsSupport(int[][] data, int[] columns) {
		double sum = 0;
		short count = 0;
		
		for(int i = 0; i < data.length; i++) {
			count = 1;
			for(int j = 0; j < columns.length; j++) {
				if (data[i][columns[j]] != 1) {
					count = 0;
					break;
				}
			}
			sum += count;
		}
		
		return sum / data.length;
	}
	
	public static List<String> filterBelowSuppColumns (List<String> combinations, List<String> belowSuppCombinations) {
		List<String> filteredCols = new ArrayList<String> ();
		for(String combination : combinations) {
			if(!isContained(combination, belowSuppCombinations)) {
				filteredCols.add(combination);
			}
		}
		
		return filteredCols;
	}
	
	private static boolean isContained(String target, List<String> source) {
		boolean isContained = false;
		for(String belowSupp : source) {
			if(DataChanges.containsTokens(target, belowSupp)) {
				isContained = true;
				break;
			}
		}
		return isContained;
	}
	
	public static void displayResults(List<List<String>> results, Double support) {
		if(results.isEmpty()) {
			System.out.println("The support is too big and there are no products combinations for it!");
			System.out.println("Please decrease it and run again.");
			return;
		}
		
		System.out.println("RESULTS");
		System.out.println("=======");
		
		System.out.println();
		System.out.println("***The following products combinations have a support grater than or equal to " + support);
		
		for(List<String> products : results) {
			System.out.println();
			System.out.println("  Products");
			System.out.println("  --------");
			for(int i = 0; i < products.size() - 1; i ++) {
				System.out.println("    >>>" + products.get(i));
			}
			System.out.println("      ==>Support: " + products.get(products.size() - 1));
		}
	}
}
