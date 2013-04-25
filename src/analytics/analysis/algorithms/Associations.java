package analytics.analysis.algorithms;

import weka.associations.Apriori;

public class Associations {
	private Apriori algorithm;
	
	public Associations() {
		this.algorithm = new Apriori();
	}
}
