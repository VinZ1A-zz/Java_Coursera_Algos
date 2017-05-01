import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Coursera_Algo_Dijkstra {

	static boolean _debug = true;
	static boolean _giveAlphaResults = false;
	static int _nbOfSteps = -1; // -1 : unlimited

	// to do - implement later using Min Heap (MinIntHeap - add deletions and
	// mapping between vertices and their positions in the heap)

	// read file dijkstraData (Node ToNode,weight ect..) and get shortest paths
	// from 1 to {7,37,59,82,99,115,133,165,188,197} in order
	// no possible path = arbitrary length of 1000000

	// Need a priority queue: Without heap (ie. an array): time impl is O(n.m)
	// with heap : ((n+m).log(n))

	public static void main(String[] args) {
		// mac format (sorry, been lazy there)
		String fileName = "/Users/VinZ/Dropbox/Work/JAVA/Training/src/dijktraData_Small.txt"; // _Small
		fileName = "/Users/VinZ/Dropbox/Work/JAVA/Training/src/dijkstraData.txt"; // real
																																							// stuff!!
		// read file into stream, try-with-resources
		List<String> aList = new ArrayList<>();
		try (Stream<String> stream = Files.lines(Paths.get(fileName))) {

			aList = stream.collect(Collectors.toList());

		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("init");
		init(aList);

		// consistency checks:
		// no negative val --- other checks?
		// skipped...

		// algo:
		// get node in remaining with lowest value in pathWeightPerNode (say, N)
		do {
			int minNodeNum = popRemaining();
			Node minNode = nodesPerNum.get(minNodeNum);
			// debugln("popped " + getNodeDisp(minNodeNum));

			// for each adjacent node x (_nodeNb in each element of _outgoing) :
			for (NodeWeight adj : minNode._outgoing) {
				// debugln("adj : " + getNodeDisp(adj._nodeNb));

				// compare pathWeightPerNode(x) with pathWeightPerNode(n) + edge_Weight
				// from n to x
				int curAdjPathWeight = pathWeightPerNode.get(adj._nodeNb);
				int newWeightFromN = pathWeightPerNode.get(minNodeNum) + adj._pathWeigth;
				// debugln("comparing " + curAdjPathWeight + " and " + newWeightFromN);
				// if shortest path found:
				if (newWeightFromN < curAdjPathWeight) {
					// - update pathWeightPerNode for N
					pathWeightPerNode.put(adj._nodeNb, newWeightFromN);
					// - update _previous of adj (to N)
					nodesPerNum.get(adj._nodeNb)._previousNodeId = minNodeNum;
				}

				// remove N from remaining (DONE when popping)
			}
			// do this until remaining is empty
			_nbOfSteps--; // DEBUG
		} while (_nbOfSteps != 0 && !remaining.isEmpty());

		// when done:
		// pathWeightPerNode stores shortest path values (do a sort per value)

		// use previous to get shortest path
		// eg. from a to i : a -> c -> d -> g -> i (=8)
		// *********** DEBUG ***************
		displayResults();

		debugln("");
		// should get 2599,2610,2947,2052,2367,2399,2029,2442,2505,3068
		int[] getShortestLengthFor = { 7, 37, 59, 82, 99, 115, 133, 165, 188, 197 };
		for (int toNode : getShortestLengthFor) {
			Integer length = pathWeightPerNode.get(toNode);
			if (length != null) {
				debug(length + ",");
			} else {
				debug("NULL,");
			}
		}

	}

	static void displayResults() {
		LinkedHashMap<Integer, Integer> newPathWeightPerNode = (LinkedHashMap) sortByValue(pathWeightPerNode);
		for (Entry<Integer, Integer> entry : newPathWeightPerNode.entrySet()) {
			debugln("path to " + getNodeDisp(entry.getKey()) + " has length " + entry.getValue());
			Node prevNode = nodesPerNum.get(entry.getKey());
			int prevNodeId = prevNode._previousNodeId;
			StringBuilder pathStr = new StringBuilder();
			while (prevNodeId != -1) {
				pathStr.append(getNodeDisp(prevNodeId)).append(" -> ");
				prevNode = nodesPerNum.get(prevNodeId);
				prevNodeId = prevNode._previousNodeId;
			}
			debugln("    through " + pathStr);
		}

	}

	static private Map<Integer, Node> nodesPerNum = new HashMap<>();
	// to know the edge_Weight from n to x :
	// nodesPerNum.get(n)._outgoing.get(x)._pathWeight
	static private Map<Integer, Integer> pathWeightPerNode = new HashMap(); // path_weight
	// all nodes in path, will remove the nodes with lowest path_weight first
	// (sorting pathWeightPerNode per value)
	// TODO - better implementation (min Heap)
	static private Set<Integer> remaining = new HashSet<>();
	// Java-built-in heap or create one (how to insert in a minHeap (keep
	// balanced)?)
	// static private PriorityQueue<NodeWeight> remaining2 = new
	// PriorityQueue<>();

	static final private int INFINITY = 1000000;

	public static void init(List<String> aList) {

		for (String line : aList) {
			String[] contents = line.split("\t");

			int i = 0;
			Node node = null;
			// create each Node and all weights to other nodes
			for (String item : contents) {
				item = item.trim().toLowerCase();
				if (i == 0) {
					// !!! transform chars to numbers whenever they are found
					// (convenience)
					int nodeNum = getNodeNum(item);
					node = new Node(nodeNum);
					// map each node in nodesPerNum
					nodesPerNum.put(nodeNum, node);
				} else // path weigh given
				{
					String nodeAndWeight[] = item.split(",");
					NodeWeight nodeWeight = new NodeWeight(getNodeNum(nodeAndWeight[0]), Integer.parseInt(nodeAndWeight[1]));
					node._outgoing.add(nodeWeight);
				}
				i++;
			}

		}

		// once all nodes created, initialize pathWeightPerNode
		// to 0 for node #1, INFINITY for the others
		for (Entry<Integer, Node> item : nodesPerNum.entrySet()) {
			if (item.getKey() == 1) {
				pathWeightPerNode.put(1, 0);
			} else {
				pathWeightPerNode.put(item.getKey(), INFINITY);
			}
			// initialize remaining with all numbers
			remaining.add(item.getKey());

		}

		// get helper function to get the current minimum from remaining (sort map
		// per value) & TEST IT
		// testSortingPerValue(); // DEBUG !!!
	}

	// get node in remaining with lowest value in pathWeightPerNode
	static int popRemaining() {
		LinkedHashMap<Integer, Integer> newPathWeightPerNode = (LinkedHashMap) sortByValue(pathWeightPerNode);
		int elemRemoved;
		Iterator<Entry<Integer, Integer>> iter = newPathWeightPerNode.entrySet().iterator();
		// remove first existing element
		do {
			elemRemoved = iter.next().getKey();
		} while (!remaining.remove(new Integer(elemRemoved)));
		return elemRemoved;
	}

	// DEBUG METHOD
	static void testSortingPerValue() {
		pathWeightPerNode.clear();
		remaining.clear();

		pathWeightPerNode.put(1, 5);
		pathWeightPerNode.put(2, 3);
		pathWeightPerNode.put(3, 7);
		pathWeightPerNode.put(4, 2);
		remaining.add(1);
		remaining.add(2);
		remaining.add(3);
		remaining.add(4);

		// pathWeightPerNode = sortByValue(pathWeightPerNode);

		for (Entry<Integer, Integer> item : pathWeightPerNode.entrySet()) {
			debugln("key " + item.getKey() + " ; value " + item.getValue());
		}

		debugln(" will remove key " + popRemaining());

		for (int item : remaining) {
			debugln("in remaining " + item);
		}

		debugln(" will remove key " + popRemaining());

		int a = 1;
	}

	// TODO - create a priority queue of NodeWeight
	// need to add a comparator to compare by weight
	static class NodeWeight {
		int _nodeNb;
		int _pathWeigth = 0;

		public NodeWeight(int nodeNb, int pathWeight) {
			_nodeNb = nodeNb;
			_pathWeigth = pathWeight;
		}
	}

	static class Node {
		public Node(int iId) {
			_nodeId = iId;
		}

		int _nodeId;
		// SomeObj _data; // could carry other information
		// boolean visited = false; // TO RESET!
		List<NodeWeight> _outgoing = new ArrayList<>();
		int _previousNodeId = -1; // prev node in current shortest path
	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		return map.entrySet().stream()
				.sorted(Map.Entry.comparingByValue(/* Collections.reverseOrder() */))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}

	static int getNodeNum(String item) {
		char nodeName = item.toCharArray()[0];
		if (nodeName >= 'a' && (nodeName <= 'z')) {
			// !!! remember chars were used to give results back as chars (cool)
			_giveAlphaResults = true;
			return (nodeName - 'a' + 1); // 'a' is 1
		} else {
			return (Integer.parseInt(item));
		}
		// debugln(nodeName + " nodeNum " + nodeNum);
	}

	static String getNodeDisp(int num) {
		if (_giveAlphaResults) {
			Character disp = (char) ('a' + num - 1);
			return Character.toString(disp);
		} else {
			return String.valueOf(num);
		}
	}

	private static void debug(Object obj) {
		if (_debug) {
			System.err.print(obj.toString());
		}
	}

	private static void debugln(Object obj) {
		if (_debug) {
			System.err.println(obj.toString());
		}
	}

}
