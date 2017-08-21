package com.cacheserverdeploy.deploy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class Deploy {

	public static String[] deployServer(String[] graphContent) {

		// System.out.println("Genetic Methods runing here: ");
		/*
		 * Test codes are lying here:
		 */
		final int IterTime = 50;
		final int populationSize = 100;
		int[] bestValues = new int[IterTime];
		long startTime = System.currentTimeMillis();
		long timeOut = 85000;
		/*
		 * 
		 */
		String[] content = new String[graphContent.length];
		Graph graph = new Graph(graphContent);
		MCFF mc = new MCFF(graph);
		int netSize = graph.getNetNodeNum();
		Population myPop = new Population(populationSize, netSize, true);
		GA.evolvePopulation(myPop, netSize, mc);
		for (int i = 0; i < IterTime && System.currentTimeMillis() - startTime < timeOut; i++) {
			// mc = new MCFF(new Graph(graphContent));
			myPop = GA.evolvePopulation(myPop, netSize, mc);
			bestValues[i] = (int) (1.0 / myPop.getFittest(mc).getFitness(mc));
			// System.out.println("Iteration:" + i + ": " + bestValues[i]);
			if (i > 3 && bestValues[i] == bestValues[i - 1] && bestValues[i - 1] == bestValues[i - 2]
					&& bestValues[i - 2] == bestValues[i - 3]) {
				break;
			}
		}

		// Get the best routes:
		ArrayList<Integer> al = new ArrayList<Integer>();
		for (int i = 0; i < netSize; i++) {
			if (GA.bestLocation.getGene(i) == 1)
				al.add(i);
		}
		System.out.println(al);
		// mc.getResult(al);

		content = mc.getContent(al);
		for (String str : content)
			System.out.println(str);
		return content;
	}

}

class ConsumeNode {
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

class Edge {
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

class Edge2 {
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

class FinalValues {
	public static final int MAX_NET_NODE_NUM = 1000;
	public static final int MAX_CONSUME_NODE_NUM = 500;
	public static final int MAX_LINK_NUM_PER_NODE = 20;
	public static final int MAX_EDGE_NUM = 2000 * 20;
	public static final int MIN_LINK_CAPACITY = 0;
	public static final int MAX_LINK_CAPACITY = 100;
	public static final int MIN_UNIT_CAPACITY_COST = 0;
	public static final int MAX_UNIT_CAPACITY_COST = 100;
	public static final int MIN_SERVEF_COST = 0;
	public static final int MAX_SERVEF_COST = 5000;
	public static final int MIN_BANDWIDTH_DEMAND = 0;
	public static final int MAX_BANDWIDTH_DEMAND = 5000;
	public static final int MAX_PATH_NUM = 50000;
	public static final int MAX_NODE_NUM_PER_PATH = 1000;
	public static final int MAX_RUN_TIME = 90;
	public static final int INF = 100000000;
}

class Flow {
	List<Integer> route;
	int consumeId;
	int bandwidth;

	public List<Integer> getRoute() {
		return route;
	}

	public void setRoute(List<Integer> route) {
		this.route = route;
	}

	public int getConsumeId() {
		return consumeId;
	}

	public void setConsumeId(int consumeId) {
		this.consumeId = consumeId;
	}

	public int getBandwidth() {
		return bandwidth;
	}

	public void setBandwidth(int bandwidth) {
		this.bandwidth = bandwidth;
	}

}

class Graph {
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

	public Graph(String[] graphContent) {
		parseInput(graphContent);
	}

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

}

class MCFF {
	private int vertexNum;
	private int distance[] = new int[FinalValues.MAX_NET_NODE_NUM];
	private int totalUnitCost[] = new int[FinalValues.MAX_NET_NODE_NUM + 2];
	private int preVertex[] = new int[FinalValues.MAX_NET_NODE_NUM];
	private int preEdge2[] = new int[FinalValues.MAX_NET_NODE_NUM];
	private int totalCost;
	private Graph graph;
	private ArrayList<Edge2>[] adjList;
	private List<Flow> bestPath = new ArrayList<Flow>();
	// private ArrayList<ArrayList<Integer>> pathList = new
	// ArrayList<ArrayList<Integer>>();

	public MCFF(Graph graph) {
		this.graph = graph;
		// graph.parseInput(graphContent);
		vertexNum = graph.getNetNodeNum() + 2; // Add 2 as super sink/source
		totalCost = 0;
	}

	//
	public void addEdge2(int from, int to, int cap, int cost) {
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
		bestPath = new ArrayList<Flow>();
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

	public String[] getContent(List<Integer> serverId) {
		String[] result = new String[bestPath.size() + 2];
		// int cost = getMinCostFlow(serverId);
		int pathCnt = bestPath.size();
		result[0] = (pathCnt + "");
		result[1] = "";
		for (int i = 0; i < bestPath.size(); i++) {
			StringBuilder sb = new StringBuilder();
			Flow flow = bestPath.get(i);
			for (int j = 0; j < flow.getRoute().size(); j++)
				sb.append(flow.getRoute().get(j) + " ");
			sb.append(flow.getConsumeId() + " " + flow.getBandwidth());
			result[i + 2] = sb.toString();
		}

		return result;
	}

}

class Pair implements Comparable {
	private int first;
	private int second;

	public Pair(int first, int second) {
		super();
		this.first = first;
		this.second = second;
	}

	@Override
	public int compareTo(Object obj) {
		Pair pair = (Pair) obj;
		return this.getFirst() < pair.getFirst() ? 1 : (this.getFirst() == pair.getFirst() ? 0 : -1);
	}

	public int getFirst() {
		return first;
	}

	public void setFirst(int first) {
		this.first = first;
	}

	public int getSecond() {
		return second;
	}

	public void setSecond(int second) {
		this.second = second;
	}

}

class GA {
	private static final double uniformRate = 0.5;
	private static final double mutationRate = 0.015;
	private static final int tournamentSize = 5;
	private static final boolean elitism = true;
	public static Location bestLocation;

	// Evolve a populatin
	public static Population evolvePopulation(Population pop, int netSize, MCFF mc) {
		Population newPopulation = new Population(pop.size(), netSize, false);
		// Keep our best individual
		int elitismOffset = 0;
		if (elitism) {
			newPopulation.saveLocation(0, pop.getFittest(mc));
			bestLocation = pop.getFittest(mc);
			elitismOffset = 1;
		}

		/*
		 * CrossOver population: Loop over popution's size and create
		 * individuals from current population
		 */
		for (int i = elitismOffset; i < newPopulation.size(); i++) {
			// Select parents:
			Location parent1 = tournamentSelection(pop, netSize, mc);
			Location parent2 = tournamentSelection(pop, netSize, mc);
			// Crossover parents
			Location child = crossover(parent1, parent2, netSize);
			// Add child to the new population
			newPopulation.saveLocation(i, child);
		}
		// Mutate population
		for (int i = elitismOffset; i < newPopulation.size(); i++) {
			mutate(newPopulation.getLocation(i));
		}

		return newPopulation;
	}

	// Applies crossover to a set of parents and creates offspring
	public static Location crossover(Location parent1, Location parent2, int netSize) {
		// Create new child location
		Location child = new Location(netSize);

		// Loop through genes:
		for (int i = 0; i < parent1.size(); i++) {
			// Crossover
			if (Math.random() <= uniformRate) {
				child.setGene(i, parent1.getGene(i));
			} else
				child.setGene(i, parent2.getGene(i));
		}
		return child;
	}

	// Mutate an location
	private static void mutate(Location loc) {
		// Loop through genes:
		for (int i = 0; i < loc.size(); i++) {
			if (Math.random() <= mutationRate) {
				// Create random gene
				byte gene = (byte) Math.round(Math.random());
				loc.setGene(i, gene);
			}
		}
	}

	// Select candidates location for crossover
	private static Location tournamentSelection(Population pop, int netSize, MCFF mc) {
		// Create a tournament population
		Population tournament = new Population(tournamentSize, netSize, false);
		// For each place in the tournament get a random candidate location and
		// add it
		for (int i = 0; i < tournamentSize; i++) {
			int randomId = (int) (Math.random() * pop.size());
			tournament.saveLocation(i, pop.getLocation(randomId));
		}
		Location bestLocation = tournament.getFittest(mc);
		return bestLocation;
	}
}

class Location {
	// Holds locations of servers
	// private int count;
	private byte[] genes;
	private double fitness;

	public void generateLocation(int count) {
		// genes = new byte[count];
		for (int i = 0; i < count; i++) {
			byte gene = (byte) Math.round(Math.random());
			genes[i] = gene;
		}
	}

	public Location(int netNodeNum) {
		genes = new byte[netNodeNum];
	}

	public double getFitness(MCFF m) {
		ArrayList<Integer> al = new ArrayList<Integer>();
		for (int i = 0; i < size(); i++) {
			if (genes[i] == 1)
				al.add(i);
		}
		int curVal = m.getMinCostFlow(al);
		if (curVal == -1)
			curVal = FinalValues.INF;
		fitness = 1.0 / curVal;
		return fitness;
	}

	public int size() {
		return genes.length;
	}

	public void setGene(int index, byte value) {
		genes[index] = value;
		fitness = 0;
	}

	public byte getGene(int index) {
		return genes[index];
	}
}

class Population {
	Location[] locations;

	public Population(int populationSize, int netSize, boolean initialise) {
		locations = new Location[populationSize];
		// Initialize
		if (initialise) {
			// Loop and create new individuals
			for (int i = 0; i < locations.length; i++) {
				Location newLocation = new Location(netSize);
				newLocation.generateLocation(netSize);
				saveLocation(i, newLocation);
			}
		}
	}

	public Location getFittest(MCFF mc) {
		Location bestLoc = locations[0];
		for (int i = 1; i < locations.length; i++) {
			if (bestLoc.getFitness(mc) <= locations[i].getFitness(mc))
				bestLoc = locations[i];
		}
		return bestLoc;
	}

	public void saveLocation(int index, Location location) {
		locations[index] = location;
	}

	public int size() {
		return locations.length;
	}

	public Location getLocation(int index) {
		return locations[index];
	}
}
