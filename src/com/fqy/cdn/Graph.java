package com.fqy.cdn;

import java.util.ArrayList;
import java.util.List;

public class Graph {
	private int netNodeNum;
	private int consumeNodeNum;
	private int linkNum;
	private int perServerCost;
	private List<Edge> edgeList = new ArrayList();
	private List<ConsumeNode> consumeNodeList = new ArrayList();
	private int[][] edgeCapacity = new int[FinalValues.MAX_NET_NODE_NUM][FinalValues.MAX_NET_NODE_NUM];
	private int[][] edgeCost = new int[FinalValues.MAX_NET_NODE_NUM][FinalValues.MAX_NET_NODE_NUM];
	private List<Integer> netNodeFlow;
	private int totalNeededFlow;

	// private List<Edge2> adjList[] = new
	// ArrayList[FinalValues.MAX_NET_NODE_NUM + 2];

	// Used in MCFF class
	// public void addEdge2(int from, int to, int cap, int cost) {
	// adjList[from].add(new Edge2(to, cap, cost, adjList[to].size() - 1));
	// adjList[to].add(new Edge2(from, cap, cost, adjList[from].size() - 1));
	// }

	public void parseInput(String[] graphContent) {
		int nulLLine = 0;
		int linkCnt = 0;
		for (int i = 0; i < graphContent.length; i++) {
			if (graphContent[i].trim().equals("")) {
				// i++;
				nulLLine++;
				continue;
			}
			if (nulLLine == 0) {
				String[] str = graphContent[i].split(" ");
				netNodeNum = Integer.parseInt(str[0]);
				linkNum = Integer.parseInt(str[1]);
				consumeNodeNum = Integer.parseInt(str[2]);
				// System.out.println(netNodeNum + " " + linkNum + " " +
				// consumeNodeNum);
				edgeCapacity = new int[netNodeNum][netNodeNum];
				edgeCost = new int[netNodeNum][netNodeNum];

			}
			if (nulLLine == 1) {
				perServerCost = Integer.parseInt(graphContent[i]);
			}
			if (nulLLine == 2) {
				String[] str = graphContent[i].split(" ");
				int startNode = Integer.parseInt(str[0]);
				int endNode = Integer.parseInt(str[1]);
				int cap = Integer.parseInt(str[2]);
				int perCost = Integer.parseInt(str[3]);
				// Set the edges of the graph, linkCnt is a local variable.
				edgeList.add(new Edge(linkCnt++, startNode, endNode, cap, perCost));
				// addEdge2(startNode, endNode, cap, perCost);
				// Set the capacity and cost matrix of the graph using array
				edgeCapacity[startNode][endNode] = cap;
				edgeCapacity[endNode][startNode] = cap;
				edgeCost[startNode][endNode] = perCost;
				edgeCost[endNode][startNode] = perCost;
			}
			if (nulLLine == 3) {
				String[] str = graphContent[i].split(" ");
				int id = Integer.parseInt(str[0]);
				int netNodeId = Integer.parseInt(str[1]);
				int bandwidthDemand = Integer.parseInt(str[2]);
				consumeNodeList.add(new ConsumeNode(id, netNodeId, bandwidthDemand));
				// consumeNodeNum++;
			}
		}
		setTotalNeedFlow();
	}

	// Get edgeCapcity and edgeCost matrix
	public int[][] getEdgeCapacity() {
		return edgeCapacity;
	}

	public int[][] getEdgeCost() {
		return edgeCost;
	}

	// Get net node count
	public int getNetNodeNum() {
		return netNodeNum;
	}

	// Get per sever cost
	public int getPerServerCost() {
		return perServerCost;
	}

	// Get Consume Node List
	public List<ConsumeNode> getConsumeNodeList() {
		return consumeNodeList;
	}

	// Get the consumeNodeId if connected
	public int getConsumeNodeId(int netNodeId) {
		int k = -1;
		for (int i = 0; i < consumeNodeList.size(); i++) {
			if (consumeNodeList.get(i).getNetNodeId() == netNodeId) {
				k = i;
				break;
			}
		}

		return k;
	}

	/*
	 * Updated
	 */
	public List<Integer> getNetNodeFlow() {
		return netNodeFlow;
	}

	// Get the max input/output flow account allowed
	public void setNetNodeFlow() {
		for (int i = 0; i < netNodeNum; i++) {
			int netFlow = 0;
			for (int j = 0; j < netNodeNum; j++) {
				netFlow += edgeCapacity[i][j];
			}
			netNodeFlow.add(netFlow);
		}
	}

	// Set the total flow needed
	public void setTotalNeedFlow() {
		for (int i = 0; i < consumeNodeList.size(); i++)
			totalNeededFlow += consumeNodeList.get(i).getBandwidthDemand();
	}

	// Get the total flow needed of the graph
	public int getTotalNeededFlow() {
		return totalNeededFlow;
	}

	// get the output flow of given network node
	public int getNetNodeFLow(int netNodeId) {
		return netNodeFlow.get(netNodeId);
	}

	public List<Edge> getEdgeList() {
		return edgeList;
	}

	// Copy one array to another.
	// public void copyAdjList(ArrayList<Edge2> distAdjList[]) {
	// System.arraycopy(this.adjList, 0, distAdjList, 0,
	// FinalValues.MAX_NET_NODE_NUM + 2);
	// }
}
