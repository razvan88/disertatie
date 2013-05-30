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
			List<ClusterPoint> neighbors = this.getNeighbors(point);
			if(neighbors.size() < DbscanHelper.minElems)
				point.setNoise(true);
			else {
				List<ClusterPoint> cluster = expandCluster(point, neighbors);
				clusters.add(cluster);
			}
				
		}
	}
	
	private List<ClusterPoint> getNeighbors(ClusterPoint point) {
		return null;
		//TODO
	}
	
	private List<ClusterPoint> expandCluster(ClusterPoint root, List<ClusterPoint> neighbors) {
		return null;
		//TODO
	}
	
}
