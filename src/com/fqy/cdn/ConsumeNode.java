package com.fqy.cdn;

public class ConsumeNode {
	private int id;
	private int netNodeId;
	private int bandwidthDemand;

	public ConsumeNode(int id, int netNodeId, int bandwidthDemand) {
		super();
		this.id = id;
		this.netNodeId = netNodeId;
		this.bandwidthDemand = bandwidthDemand;
	}

	public int getId() {
		return id;
	}

	public int getNetNodeId() {
		return netNodeId;
	}

	public int getBandwidthDemand() {
		return bandwidthDemand;
	}

}
