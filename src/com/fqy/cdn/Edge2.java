package com.fqy.cdn;

public class Edge2 {
	private int endNode;
	private int capacity;
	private int perCost;
	private int revId;

	public Edge2(int endNode, int capacity, int perCost, int revId) {
		super();
		this.endNode = endNode;
		this.capacity = capacity;
		this.perCost = perCost;
		this.revId = revId;
	}

	public int getEndNode() {
		return endNode;
	}

	public void setEndNode(int endNode) {
		this.endNode = endNode;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public void setPerCost(int perCost) {
		this.perCost = perCost;
	}

	public void setRevId(int revId) {
		this.revId = revId;
	}

	public int getCapacity() {
		return capacity;
	}

	public int getPerCost() {
		return perCost;
	}

	public int getRevId() {
		return revId;
	}

}
