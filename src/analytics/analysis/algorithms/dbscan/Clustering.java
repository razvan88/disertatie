package analytics.analysis.algorithms.dbscan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import analytics.database.DBBasicOperations;
import analytics.utils.DbscanHelper;

public class Clustering {
	private List<ClusterPoint> rawPoints;
	private List<List<ClusterPoint>> clusters;
	private DbscanHelper dbscanHelper;
	private DBBasicOperations dataBase;
	
	public Clustering() {
		rawPoints = new ArrayList<ClusterPoint>();
		clusters = new ArrayList<List<ClusterPoint>>();
		dbscanHelper = DbscanHelper.getInstance();
		this.dataBase = DBBasicOperations.getInstance();
	}
	
	public void initData() {
		this.dataBase.openConnection();
		HashMap<Integer, List<String>> categories = this.dataBase.getCategories();
		this.dataBase.closeConnection();
		
		for(Integer key : categories.keySet()) {
			rawPoints.add(new ClusterPoint(dbscanHelper.getValue(categories.get(key)), key));
		}
	}
	
	public void runAlgorithm() {
		for(ClusterPoint point : rawPoints) {
			if(point.wasVisited())
				continue;
			point.setVisited(true);
			int pointEps = dbscanHelper.getEpsilonValue(point.getValue());
			List<ClusterPoint> neighbors = this.getNeighbors(point, rawPoints, pointEps);
			if(neighbors.size() < DbscanHelper.minElems)
				point.setNoise(true);
			else {
				List<ClusterPoint> cluster = expandCluster(point, neighbors, 
						rawPoints, pointEps, DbscanHelper.minElems);
				if(cluster.size() >= DbscanHelper.minClusterPoints) {
					clusters.add(cluster);
				}
			}	
		}
	}
	
	private List<ClusterPoint> getNeighbors(ClusterPoint point, List<ClusterPoint> allPoints, int eps) {
		List<ClusterPoint> neighbors = new ArrayList<ClusterPoint>();
		int refVal = point.getValue();
		
		for(ClusterPoint p : allPoints) {
			if(Math.abs(p.getValue() - refVal) <= eps)
				neighbors.add(p);
		}
		
		return neighbors;
	}
	
	private List<ClusterPoint> expandCluster(ClusterPoint root, 
				List<ClusterPoint> rootNeighbors, List<ClusterPoint> allPoints, int eps, int minElems) {
		List<ClusterPoint> cluster = new ArrayList<ClusterPoint>();
		
		cluster.add(root);
		root.setClusterMember(true);
		
		for(int i = 0; i < rootNeighbors.size(); i++) {
			ClusterPoint point = rootNeighbors.get(i);
			if(!point.wasVisited()) {
				point.setVisited(true);
				List<ClusterPoint> pointNeighbors = this.getNeighbors(point, allPoints, eps);
				if(pointNeighbors.size() >= minElems) {
					rootNeighbors.addAll(pointNeighbors);
				}
			}
			if(!point.isClusterMember()) {
				point.setClusterMember(true);
				cluster.add(point);
			}
		}
		
		return cluster;
	}
	
	private List<List<Integer>> getClustersAsIntegers() {
		List<List<Integer>> integerClusters = new ArrayList<List<Integer>>();
		for(List<ClusterPoint> intCluster : this.clusters) {
			List<Integer> cluster = new ArrayList<Integer>();
			for(ClusterPoint point : intCluster) {
				cluster.add(new Integer(point.getBillCode()));
			}
			integerClusters.add(cluster);
		}
		return integerClusters;
	}
	
	public List<List<String>> getMostConsumedProductsForEachCluster(int menuItemsNo) {
		List<List<String>> prods = new ArrayList<List<String>>();
		
		for(List<Integer> cluster : this.getClustersAsIntegers()) {
			prods.add(this.dbscanHelper.getMostConsumedProducts(cluster, menuItemsNo));
		}
		
		return prods;
	}
	
	/*for testing purpose
	public static void main(String[] args) {
		Clustering alg = new Clustering();
		alg.initData();
		alg.runAlgorithm();
		List<List<String>> consumed = alg.getMostConsumedProductsForEachCluster(3);
		
		JSONObject allMenus = new JSONObject();
		for(List<String> menu : consumed) {
			JSONObject products = new JSONObject();
			for(String product : menu) {
				if(menu.indexOf(product) > 0) {
					products.accumulate("products", product);
				} else {
					products.put("category", product);
				}
			}
			allMenus.accumulate("menus", products);
		}
		
		System.out.println(allMenus.toString());
	}
	*/
}
