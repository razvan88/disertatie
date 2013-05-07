package analytics.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
	
	public static List<String> getAllCustomCombinations(int totalColumns, 
			int max, List<Integer> customColumns, List<String> zeroSupp) {
		List<String> allCombinations = new ArrayList<String>();
		
		for(int i = 0; i < totalColumns - max + 1; i++) {
			if(customColumns.contains(i))
				continue;
			List<String> combinations = AprioriHelper.getRecursiveCustomCombinations(
					i, totalColumns - 1, max, customColumns, zeroSupp);
			for(String combination : combinations) {
				allCombinations.add(combination);
			}
		}
		
		return allCombinations;
	}
	
	private static List<String> getRecursiveCustomCombinations(int minIndex, int maxIndex, 
			int remainedLength, List<Integer> customColumns, List<String> zeroSupp) {
		List<String> combinations = new ArrayList<String>();
		
		if(remainedLength == 0 ||
				zeroSupp.contains(((Integer)minIndex).toString()))
			return combinations;
		
		remainedLength--;
		String partialCombinationPrefix = minIndex + (remainedLength > 0 ? "," : "");
		if(remainedLength == 0)
			combinations.add(partialCombinationPrefix);
		
		int nextRecursiveMinIndex = minIndex + 1;

		while(nextRecursiveMinIndex <= maxIndex && remainedLength > 0) {
			if(!customColumns.contains(nextRecursiveMinIndex)){
				List<String> unprefixed = AprioriHelper.getRecursiveCustomCombinations(nextRecursiveMinIndex, 
						maxIndex, remainedLength, customColumns, zeroSupp);
				
				for(String partialCombinationUnprefixed : unprefixed) {
					 combinations.add(partialCombinationPrefix + partialCombinationUnprefixed);
				}
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
	
	public static double getColumnsSupport(int[][] data, List<Integer> columns) {
		double sum = 0;
		short count = 0;
		
		for(int i = 0; i < data.length; i++) {
			count = 1;
			for(int j = 0; j < columns.size(); j++) {
				if (data[i][columns.get(j).intValue()] != 1) {
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
	
	public static Integer[] getCodesAtIndexes(Integer[] indexes, List<Integer> codes) {
		Integer[] filteredCodes = new Integer[indexes.length];
		List<Integer> filteredCodesList = new ArrayList<Integer>();
		
		for(Integer index : indexes) {
			filteredCodesList.add(codes.get(index));
		}
		
		filteredCodesList.toArray(filteredCodes);
		return filteredCodes;
	}
	
	public static double getSupportForProducts (List<String> selectedProducts, 
			HashMap<Integer, String> products, List<Integer> sortedCodes, int[][] allData) {
		int columns[] = new int[selectedProducts.size()],
				cols = 0;
		
		for(String product : selectedProducts) {
			if(!products.containsValue(product))
				continue;
			Integer key = DataChanges.getKeyForValue(products, product);
			if(key != null && sortedCodes.contains(key)) {
				int index = sortedCodes.indexOf(key);
				if(index > -1 && index < allData[0].length) {
					columns[cols++] = index;
				}
			}
		}
		
		return AprioriHelper.getColumnsSupport(allData, columns);
	}
	
	public static double getConfidenceWithNames(List<String> baseProducts, List<String> determinedProducts, 
			HashMap<Integer, String> products, List<Integer> sortedCodes, int[][] allData) {
		
		double baseSupp = AprioriHelper.getSupportForProducts(baseProducts, products, sortedCodes, allData);
		List<String> allProducts = new ArrayList<String>(baseProducts);
		allProducts.addAll(determinedProducts);
		double allSupp = AprioriHelper.getSupportForProducts(allProducts, products, sortedCodes, allData);
		
		return allSupp / baseSupp;
	}
	
	public static double getConfidenceWithIndexes(List<Integer> baseProductsIndexes, 
			List<Integer> determinedProductsIndexes, int[][] allData) {
		
		List<Integer> allProds = new ArrayList<Integer>();
		allProds.addAll(baseProductsIndexes);
		allProds.addAll(determinedProductsIndexes);
		double allProdsSupport = AprioriHelper.getColumnsSupport(allData, allProds);
		double baseProdsSupport = AprioriHelper.getColumnsSupport(allData, baseProductsIndexes);
		
		return allProdsSupport / baseProdsSupport;
	}
	
	public static void displaySupportResults(List<List<String>> results, Double support) {
		if(results.isEmpty()) {
			System.out.println("The support is too big and there are no products combinations for it!");
			System.out.println("Please decrease it and run again.");
			return;
		}
		
		System.out.println("RESULTS");
		System.out.println("=======");
		
		System.out.println();
		System.out.println("***The following products combinations have a support grater than or equal to " + support);
		System.out.println();
		
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
	
	public static void displayConfidenceResults(List<List<String>> results, List<String> baseProds, Double confidence) {
		if(results.isEmpty()) {
			System.out.println("The confidence is too big and there are no determined products for it!");
			System.out.println("Please decrease it and run again.");
			return;
		}
		
		System.out.println("RESULTS");
		System.out.println("=======");
		
		System.out.println();
		System.out.println("***The following products determination have a confidence grater than or equal to " + confidence);
		System.out.println();
		
		for(List<String> determined : results) {
			for(String baseProd : baseProds) {
				System.out.println();
				System.out.println("  Products");
				System.out.println("  --------");
				System.out.println("    ###" + baseProd);
			}
			System.out.println("  Determined");
			System.out.println("  ----------");
			for(int i = 0; i < determined.size() - 1; i ++) {
				System.out.println("    >>>" + determined.get(i));
			}
			System.out.println("      ==>Confidence: " + determined.get(determined.size() - 1));
		}
	}
	
	public static List<Integer> getCodesForProducts(HashMap<Integer, String> products, List<String> names) {
		List<Integer> codes = new ArrayList<Integer>();
		int found = 0, 
				size = products.size();
		
		for(Integer key : products.keySet()) {
			if(names.contains(products.get(key))) {
				codes.add(key);
				if(++found == size) {
					break;
				}
			}
		}
		
		return codes;
	}
	
	public static List<Integer> getIndexForCodes(List<Integer> codes, List<Integer> allSortedCodes) {
		List<Integer> indexes = new ArrayList<Integer>();
		
		for(Integer code : codes) {
			indexes.add(allSortedCodes.indexOf(code));
		}
		
		return indexes;
	}
}
