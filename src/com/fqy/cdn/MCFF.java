package com.fqy.cdn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

//Minumum cost feasible flow
public class MCFF {
	private int vertexNum;
	private int distance[] = new int[FinalValues.MAX_NET_NODE_NUM + 2];
	private int totalUnitCost[] = new int[FinalValues.MAX_NET_NODE_NUM + 2];
	private int preVertex[] = new int[FinalValues.MAX_NET_NODE_NUM + 2];
	private int preEdge2[] = new int[FinalValues.MAX_NET_NODE_NUM + 2];
	private int totalCost;
	private Graph graph;
	private ArrayList<Edge2>[] adjList;
	private List<Flow> bestPath = new ArrayList<Flow>();
	// private ArrayList<ArrayList<Integer>> pathList = new
	// ArrayList<ArrayList<Integer>>();

	public MCFF(Graph graph) {
		this.graph = graph;
		vertexNum = graph.getNetNodeNum() + 2; // Add 2 as super sink/source
		totalCost = 0;
	}

	//
	public void addEdge2(int from, int to, int cap, int cost) {
		// for (int i = 0; i < graph.getNetNodeNum(); i++)
		// adjList[i] = new ArrayList<>();
		// // for(int i=0; i<graph.getNetNodeNum(); i++)
		// adjList = new ArrayList[graph.getNetNodeNum()];

		adjList[from].add(new Edge2(to, cap, cost, adjList[to].size() - 1));
		adjList[to].add(new Edge2(from, cap, cost, adjList[from].size() - 1));
	}

	public void initAdjList(int superSource, int superSink, List<Integer> source, List<ConsumeNode> consumeNodeList) {
		// Copy net nodes' disjoint list
		adjList = new ArrayList[graph.getNetNodeNum() + graph.getConsumeNodeList().size()];
		for (int i = 0; i < graph.getNetNodeNum() + graph.getConsumeNodeList().size(); i++)
			adjList[i] = new ArrayList<>();
		ArrayList<Edge> al = (ArrayList) graph.getEdgeList();
		for (int i = 0; i < al.size(); i++) {
			int startNode = al.get(i).getStartNode();
			int endNode = al.get(i).getEndNode();
			int capacity = al.get(i).getCapacity();
			int perCost = al.get(i).getPerCost();
			addEdge2(startNode, endNode, capacity, perCost);
		}
		// Construct an edge that links the super source, add it to the disjoint
		// list
		for (int i = 0; i < source.size(); i++) {

			addEdge2(superSource, source.get(i), FinalValues.INF, 0);
		}
		// Construct an edge that links to the super sink, add it to the
		// disjoint list
		for (int i = 0; i < consumeNodeList.size(); i++) {
			addEdge2(consumeNodeList.get(i).getNetNodeId(), superSink, consumeNodeList.get(i).getBandwidthDemand(), 0);
		}
	}

	public int mcffCore(int superSource, int superSink, int needFlow) {
		int cost = 0;
		// Initial all the other vertexes to be zero
		Arrays.fill(totalUnitCost, 0);
		// Total bandwidth need greater than zero
		while (needFlow > 0) {
			PriorityQueue<Pair> pQueue = new PriorityQueue<Pair>();
			// Initialize all vertexes to source to be Infinite
			Arrays.fill(distance, FinalValues.INF);
			// distance of superSource to superSouce to be 0
			distance[superSource] = 0;
			// Add super source to the PriorityQueue
			pQueue.add(new Pair(0, superSource));

			while (!pQueue.isEmpty()) {
				Pair p = pQueue.poll(); // Retrieve and remove the first element
				int vet = p.getSecond(); // The vertex associated withe the
											// distance
				if (distance[vet] < p.getFirst())
					continue;
				for (int i = 0; i < adjList[vet].size(); i++) {
					Edge2 edge2 = adjList[vet].get(i);
					if (edge2.getCapacity() > 0 && distance[edge2.getEndNode()] > distance[vet] + edge2.getPerCost()
							+ totalUnitCost[vet] - totalUnitCost[edge2.getEndNode()]) {
						distance[edge2.getEndNode()] = distance[vet] + edge2.getPerCost() + totalUnitCost[vet]
								- totalUnitCost[edge2.getEndNode()];
						preVertex[edge2.getEndNode()] = vet;
						preEdge2[edge2.getEndNode()] = i;
						pQueue.add(new Pair(distance[edge2.getEndNode()], edge2.getEndNode()));
					}
				}
			}

			// No more opr
			if (distance[superSink] == FinalValues.INF)
				return -1;
			for (int j = 0; j < vertexNum; j++)
				totalUnitCost[j] += distance[j];

			int realFlow = needFlow;
			for (int i = superSink; i != superSource; i = preVertex[i])
				realFlow = Math.min(realFlow, adjList[preVertex[i]].get(preEdge2[i]).getCapacity());
			needFlow -= realFlow;
			cost += realFlow * totalUnitCost[superSink];

			// Store the flow:
			Flow flow = new Flow();
			flow.setRoute(new ArrayList<Integer>());
			for (int i = superSink; preVertex[i] != superSource; i = preVertex[i])
				flow.getRoute().add(preVertex[i]);
			Collections.reverse(flow.getRoute());
			int consumeId = graph.getConsumeNodeId(flow.getRoute().get(flow.getRoute().size() - 1));
			flow.setConsumeId(consumeId);
			flow.setBandwidth(realFlow);
			bestPath.add(flow);

			for (int i = superSink; i != superSource; i = preVertex[i]) {
				Edge2 e = adjList[preVertex[i]].get(preEdge2[i]);
				int cap = e.getCapacity();
				cap -= realFlow;
				e.setCapacity(cap);
			}
		}
		return cost;
	}

	public int getMinCostFlow(List<Integer> serverId) {
		int cost = 0;
		initAdjList(vertexNum - 2, vertexNum - 1, serverId, graph.getConsumeNodeList());
		int linkCost = mcffCore(vertexNum - 2, vertexNum - 1, graph.getTotalNeededFlow());
		int serverCost = graph.getPerServerCost() * serverId.size();
		cost = linkCost + serverCost;
		if (linkCost == -1)
			return -1;
		else
			return cost;
	}

	public List<Flow> getBestPath() {
		return bestPath;
	}

	public int getResult(List<Integer> serverId) {
		int cost = getMinCostFlow(serverId);
		System.out.println(cost);
		System.out.println();
		System.out.println(bestPath.size());
		System.out.println();
		for (int i = 0; i < bestPath.size(); i++) {
			Flow f = bestPath.get(i);
			for (int j = 0; j < f.getRoute().size(); j++)
				System.out.print(f.getRoute().get(j) + " ");
			System.out.print(f.getConsumeId() + " " + f.getBandwidth());
			System.out.println();
		}
		return cost;
	}
}
