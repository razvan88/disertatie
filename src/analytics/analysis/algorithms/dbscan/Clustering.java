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
	
	public Clustering() {
		rawPoints = new ArrayList<ClusterPoint>();
		clusters = new ArrayList<List<ClusterPoint>>();
		dbscanHelper = DbscanHelper.getInstance();
	}
	
	public void initData() {
		DBBasicOperations db = DBBasicOperations.getInstance();
		db.openConnection();
		HashMap<Integer, List<String>> categories = db.getCategories();
		db.closeConnection();
		
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
				clusters.add(cluster);
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
	
	public List<List<ClusterPoint>> getClusters() {
		return this.clusters;
	}
	
	//for testing purpose
	public static void main(String[] args) {
		Clustering alg = new Clustering();
		alg.initData();
		alg.runAlgorithm();
		List<List<ClusterPoint>> clusters = alg.getClusters();
		System.out.println(clusters);
	}
}
