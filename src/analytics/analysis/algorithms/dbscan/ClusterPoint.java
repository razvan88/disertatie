package analytics.analysis.algorithms.dbscan;

public class ClusterPoint {
	private int value;
	private int billCode;
	private boolean wasVisited;
	private boolean isNoise;
	private boolean isClusterMember;
	
	public ClusterPoint(int value, int billCode) {
		this.value = value;
		this.billCode = billCode;
		this.wasVisited = false;
		this.isNoise = false;
		this.isClusterMember = false;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public void setValue(int billCode) {
		this.billCode = billCode;
	}
	
	public int getBillCode() {
		return this.billCode;
	}
	
	public void setBillCode(int billCode) {
		this.billCode = billCode;
	}
	
	public boolean wasVisited() {
		return this.wasVisited;
	}
	
	public void setVisited(boolean visited) {
		this.wasVisited = visited;
	}
	
	public boolean isNoise() {
		return this.isNoise;
	}
	
	public void setNoise(boolean noise) {
		this.isNoise = noise;
	}
	
	public boolean isClusterMember() {
		return this.isClusterMember;
	}
	
	public void setClusterMember(boolean clusterMember) {
		this.isClusterMember = clusterMember;
	}
}
