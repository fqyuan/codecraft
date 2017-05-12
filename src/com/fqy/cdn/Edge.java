package com.fqy.cdn;

public class Edge {
	private int id;
	private int startNode;
	private int endNode;
	private int capacity;
	private int perCost;

	public Edge(int id, int startNode, int endNode, int capacity, int perCost) {
		super();
		this.id = id;
		this.startNode = startNode;
		this.endNode = endNode;
		this.capacity = capacity;
		this.perCost = perCost;
	}

	public int getId() {
		return id;
	}

	public int getStartNode() {
		return startNode;
	}

	public int getEndNode() {
		return endNode;
	}

	public int getCapacity() {
		return capacity;
	}

	public int getPerCost() {
		return perCost;
	}

}
