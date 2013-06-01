package analytics.analysis.algorithms.dbscan;

public class DBSCAN {
	private static Clustering instance;
	
	static {
		instance = new Clustering();
		instance.initData();
		instance.runAlgorithm();
	}
	
	private DBSCAN() {}
	
	public static Clustering getInstance() {
		return instance;
	}
}
