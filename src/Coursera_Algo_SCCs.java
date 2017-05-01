import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// yay, works with Duplicates too
public class Coursera_Algo_SCCs {

	static boolean _debug = true;

	// run with java -Xss12m -jar SCC.jar
	public static void main(String[] args) {

		// mac format (sorry, been lazy there)
		String fileName = "/Users/VinZ/Dropbox/Work/JAVA/Training/src/SCC_Small2.txt"; // _Small
		fileName = "/Users/VinZ/Dropbox/Work/JAVA/Training/src/SCC.txt";

		// String fileName = // WINDOWS
		// "C:\\Users\\vpingard\\Dropbox\\Work\\JAVA\\Training\\src\\SCC.txt";

		// read file into stream, try-with-resources
		List<String> aList = new ArrayList<>();
		try (Stream<String> stream = Files.lines(Paths.get(fileName))) {

			aList = stream.collect(Collectors.toList());

		} catch (IOException e) {
			e.printStackTrace();
		}

		// **** DEBUG ****
		// Collections.shuffle(aList);

		System.out.println("init");
		init(aList);

		// ************ CONSISTENCY CHECK(s) ****************
		for (int i = 1; i <= max; i++) {
			if (!nodesPerNum.containsKey(i)) {
				debugln("OUCH " + i);
				break;
			}
		}
		debugln("consistency check (1) OK");
		_debug = false; // ************* DISABLE DEBUG *************

		// ************ FIRST PASS ****************
		// DFS on Max node, then next leader (unvisited Node)
		// note: use reverse arcs for DFS Loop
		int nxtNodeNb = max;
		Node node = null;
		do {
			node = nodesPerNum.get(nxtNodeNb);
			// if (nxtNodeNb == 874931) // ********* DEBUG *** (stack overflow)
			// _debug = true;
			debugln("searching DFS1 node " + nxtNodeNb);
			try {
				search(node, RevType.REV);
			} catch (java.lang.StackOverflowError e) {
				System.err.println("Stack Overflow at " + nxtNodeNb + " with runtime " + runTime);
				// e.printStackTrace();
				System.exit(0);
			}
			// get nxtNodeNb (nxt unvisited)
			do {
				nxtNodeNb--;
				node = nodesPerNum.get(nxtNodeNb);
			} while (node != null && node.visited);
		} while (node != null && nxtNodeNb >= 1);

		// ************ SECOND PASS ****************
		debugln("************ SECOND PASS ****************");
		// reset visited to false (and check consistency of runTime existence!)
		for (int i = 1; i <= max; i++) {
			Node aNode = runTimePerNum.get(i); // none shall be null
			try {
				aNode.visited = false;
			} catch (java.lang.NullPointerException e) {
				System.err.println("Node " + i + " does not have a runtime");
				e.printStackTrace();
				System.exit(0);
			}
		}
		debugln("consistency check (2) OK");

		nxtNodeNb = max; // DFS loop again but against decreasing running times
		do {
			node = runTimePerNum.get(nxtNodeNb);
			debugln("searching DFS2 node " + nxtNodeNb);
			search(node);
			// get nxtNodeNb (nxt unvisited)
			do {
				nxtNodeNb--;
				node = runTimePerNum.get(nxtNodeNb);
			} while (node != null && node.visited);
			// debugln("next node for DFS2 : " + nxtNodeNb);
		} while (node != null && nxtNodeNb >= 1);

		_debug = true;
		int maxNbOfZones = 5;
		SCCSizes = sortByValue(SCCSizes);
		for (Entry<Integer, Integer> item : SCCSizes.entrySet()) {
			debugln("size of SCC(" + +item.getKey() + ") = " + item.getValue());
			if (--maxNbOfZones == 0) { // filter
				break;
			}
		}

	}

	static enum RevType {
		REG, REV;
	}

	static private void init(List<String> aList) {
		nodesPerNum = new HashMap<>();
		String[] nums;
		int from, to;
		for (String str : aList) {
			nums = str.split(" ");
			from = Integer.parseInt(nums[0]);
			to = Integer.parseInt(nums[1]);
			// System.out.println(from + "," + to);
			Node fromN = nodesPerNum.get(from);
			if (fromN == null) {
				fromN = new Node(from);
				nodesPerNum.put(from, fromN);
			}
			Node toN = nodesPerNum.get(to);
			if (toN == null) {
				toN = new Node(to);
				nodesPerNum.put(to, toN);
			}
			// double link them (to be able to reverse)
			fromN._outgoing.add(toN._data);
			toN._incoming.add(fromN._data);

			// keep track of max
			if (from > max) {
				max = from;
			}
		}

		debugln("graph created, max = " + max);
	}

	static private int runTime; // is there a better way?
	// YES there is: use a QUEUE and push Node reference
	static private int curSCCSize;

	static private RevType curRevType = RevType.REG;

	static private int max = -1;

	static private Map<Integer, Node> nodesPerNum = new HashMap<>();
	static private Map<Integer, Node> runTimePerNum = new HashMap<>();
	static private Map<Integer, Integer> SCCSizes = new HashMap<>();

	// DFS impl. Warning: Does runTime need to be reset?
	static int search(Node root, RevType revType) {
		curRevType = revType;
		searchDFS(root._data);
		// debugln("");
		return runTime; // make it look good (evil global...)
	}

	// DFS impl. using direction edges
	static void search(Node root) {
		curSCCSize = 0;
		curRevType = RevType.REG;
		searchDFS(root._data);
		SCCSizes.put(root._runTime, curSCCSize);
		// debugln("");
	}

	// DFS while counting runTime
	static void searchDFS(int nodeNum) { // Node root , RevType revType (opt)
		Node root = nodesPerNum.get(nodeNum); // avoids StackOverflow
		if (root == null)
			return;
		// debug(root._data + "(" + root._runTime + "); ");
		root.visited = true;
		// could go both ways
		Set<Integer> adjacent = (curRevType == RevType.REG) ? root._outgoing : root._incoming;
		Node node = null;
		for (int n : adjacent) {
			node = nodesPerNum.get(n);
			if (!node.visited) {
				searchDFS(n);
			}
		}
		// no more search to be done, we're at leaf - only relevant for first pass
		if (curRevType == RevType.REV) {
			root._runTime = ++runTime;
			runTimePerNum.put(runTime, root); // track for second pass
			// debugln("runTime of " + root._data + " = " + root._runTime);
		} else { // PASS #2 (yes, a bit of a hack
			curSCCSize++;
		}
	}

	static class Node {
		int _data;
		int _runTime = 0;
		boolean visited = false; // TO RESET!
		Set<Integer> _incoming = new HashSet<>();
		Set<Integer> _outgoing = new HashSet<>();

		Node(int data) {
			_data = data;
		}

		@Override
		public String toString() {
			return String.valueOf(_data);
		}
	}

	@SuppressWarnings("unused")
	private static void testSortMap() {
		// TEST SORT BY VALUE
		Map<Integer, Integer> test = new HashMap<>();
		test.put(1, 5);
		test.put(2, 1);
		test.put(3, 4);
		test.put(4, 3);
		test.put(5, 2);
		// sortByValue
		test = sortByValue(test);

		for (Entry<Integer, Integer> item : test.entrySet()) {
			debugln("key(" + +item.getKey() + ") => " + item.getValue());
		}
	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		return map.entrySet().stream().sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}

	private static void debug(Object obj) {
		if (_debug)
			System.err.print(obj.toString());
	}

	private static void debugln(Object obj) {
		if (_debug)
			System.err.println(obj.toString());
	}

}
