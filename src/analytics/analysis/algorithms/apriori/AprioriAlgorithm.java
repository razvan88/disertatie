package analytics.analysis.algorithms.apriori;

public class AprioriAlgorithm {
	private static Associations associations;
	
	static {
		associations = new Associations();
		associations.buildData();
	}
	
	private AprioriAlgorithm() {}
	
	public static Associations getInstance() {
		return associations;
	}
}
