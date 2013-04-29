package analytics.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;


public class AprioriHelper {
	
	public static int[] getDataRow(List<Integer> products, Set<Integer> codes) {
		int[] row = new int[codes.size()];
		Object[] sortedProducts = products.toArray();
		int currentProductIndex = 0,
				maxProductIndex = sortedProducts.length - 1,
				index = -1;
		Arrays.sort(sortedProducts);
		
		for(int code : codes) {
			index ++;
			
			if(currentProductIndex > maxProductIndex) {
				row[index] = 0;
				continue;
			}
			
			if(code != (Integer)sortedProducts[currentProductIndex]) {
				row[index] = 0;
			} else {
				row[index] = 1;
				currentProductIndex++;
			}
		}
		
		return row;
	}
	
	public static List<String> getAllCombinations(int totalColumns) {
		List<String> allCombinations = new ArrayList<String>();
		for(int max = 1; max <= totalColumns; max++) {
			for(int i = 0; i < totalColumns - max + 1; i++) {
				List<String> combinations = AprioriHelper.getRecursiveCombinations(i, totalColumns - 1, max);
				for(String combination : combinations) {
					allCombinations.add(combination);
				}
			}
		}
		
		return allCombinations;
	}
	
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
	
	public static List<String> removeColumnsCombination(List<String> oldList, String combination) {
		List<String> newList = new ArrayList<String>();
		
		for(String comb : oldList) {
			if(!DataChanges.containsTokens(comb, combination))
				newList.add(comb);
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
}
