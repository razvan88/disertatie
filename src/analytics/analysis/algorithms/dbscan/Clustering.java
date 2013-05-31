package analytics.analysis.algorithms.dbscan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import analytics.database.DBBasicOperations;
import analytics.utils.DbscanHelper;

public class Clustering {
	private List<ClusterPoint> rawPoints;
	private List<List<ClusterPoint>> clusters;
	
	public Clustering() {
		rawPoints = new ArrayList<ClusterPoint>();
		clusters = new ArrayList<List<ClusterPoint>>();
	}
	
	public void initData() {
		DBBasicOperations db = DBBasicOperations.getInstance();
		db.openConnection();
		HashMap<Integer, List<String>> categories = db.getCategories();
		db.closeConnection();
		
		DbscanHelper dbscanHelper = DbscanHelper.getInstance();
		for(Integer key : categories.keySet()) {
			rawPoints.add(new ClusterPoint(dbscanHelper.getValue(categories.get(key)), key));
		}
	}
	
	public void runAlgorithm() {
		for(ClusterPoint point : rawPoints) {
			if(point.wasVisited())
				continue;
			point.setVisited(true);
			List<ClusterPoint> neighbors = this.getNeighbors(point, rawPoints, DbscanHelper.epsilon);
			if(neighbors.size() < DbscanHelper.minElems)
				point.setNoise(true);
			else {
				List<ClusterPoint> cluster = expandCluster(point, neighbors, rawPoints, DbscanHelper.epsilon, DbscanHelper.minElems);
				clusters.add(cluster);
			}
				
		}
	}
	
	private List<ClusterPoint> getNeighbors(ClusterPoint point, List<ClusterPoint> allPoints, int eps) {
		List<ClusterPoint> neighbors = new ArrayList<ClusterPoint>();
		neighbors.add(point);
		int refVal = point.getValue();
		
		for(ClusterPoint p : allPoints) {
			if(Math.abs(p.getValue() - refVal) <= eps)
				neighbors.add(p);
		}
		return neighbors;
	}
	
	private List<ClusterPoint> expandCluster(ClusterPoint root, List<ClusterPoint> rootNeighbors, List<ClusterPoint> allPoints, int eps, int minElems) {
		List<ClusterPoint> cluster = new ArrayList<ClusterPoint>();
		
		cluster.add(root);
		root.setClusterMember(true);
		
		for(ClusterPoint point : rootNeighbors) {
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
	
}
