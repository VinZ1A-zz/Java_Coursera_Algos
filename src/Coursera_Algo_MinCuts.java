import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Coursera_Algo_MinCuts {

	static boolean showDetails = false;

	public static void main(String[] args) {
		// pc format (sorry, been lazy there)
		// String fileName =
		// "C:\\Users\\vpingard\\Dropbox\\Work\\JAVA\\Training\\src\\kargerMinCut_Small.txt"
		// String fileName =
		// "/Users/VinZ/Dropbox/Work/JAVA/Training/src/kargerMinCut_Small.txt";
		String fileName = "/Users/VinZ/Dropbox/Work/JAVA/Training/src/kargerMinCut.txt";

		// read file into stream, try-with-resources
		List<String> aList = new ArrayList<String>();
		try (Stream<String> stream = Files.lines(Paths.get(fileName))) {

			// aList = stream.map(Integer::parseInt).collect(Collectors.toList());
			aList = stream.collect(Collectors.toList());

		} catch (IOException e) {
			e.printStackTrace();
		}

		Map<Integer, List<Integer>> nodeToNodes = new HashMap<>();
		int n = 0;
		for (String content : aList) {
			// print(content);
			String[] ints = content.split("\t");
			int k = 0, curKey = 0;
			for (String i : ints) {
				int iVal = Integer.parseInt(i);
				if (k == 0) // new key
				{
					curKey = iVal;
					nodeToNodes.put(curKey, new ArrayList<Integer>());
				} else {
					nodeToNodes.get(curKey).add(iVal);
				}
				k++;
			}
			n++;
		}

		// deepCopy of Map for further re-use
		Map<Integer, List<Integer>> nodeToNodesCopy = deepCopy(nodeToNodes);

		int nbAttempts = (int) Math.pow(nodeToNodesCopy.keySet().size(), 2);
		if (showDetails)
			nbAttempts = 1; // DEBUG
		int bestMinCut = Integer.MAX_VALUE;
		for (int attemptNb = 1; attemptNb <= nbAttempts; attemptNb++) {
			if (showDetails)
				displayMap(nodeToNodes); // initial graph

			// remember what has been picked up to not pick it up again next
			// (table of n (shuffled), pop out two last ones)
			List<Integer> nodes = new ArrayList<>();
			nodes.addAll(nodeToNodes.keySet());
			Collections.shuffle(nodes);

			while (nodes.size() > 2) {
				int child = nodes.remove(nodes.size() - 1);
				// int parent = nodes.get(nodes.size() - 1); // OUPS. need to be
				// connected to child!

				// get one of the connected nodes
				List<Integer> parents = new ArrayList<>();
				parents.addAll(nodeToNodes.get(child));
				Collections.shuffle(parents);
				int parent = parents.get(0);

				Collections.shuffle(nodes); // don't want to take same parent again
				merging(nodeToNodes, parent, child);
			}
			// both should be the same anyway..? (desperate move)
			int minCut = Math.min(nodeToNodes.get(nodes.get(0)).size(), nodeToNodes.get(nodes.get(1)).size());
			println("minCut for attempt #" + attemptNb + "/" + nbAttempts + " : " + minCut + "\t\t(best so far : "
					+ bestMinCut + ")");

			if (minCut < bestMinCut) {
				bestMinCut = minCut;
			}

			// restore original Map
			nodeToNodes = deepCopy(nodeToNodesCopy);
		}

		// should find 17 with kargerMinCut.txt !!!
		println("probable best minCut " + bestMinCut);

	}

	static Map<Integer, List<Integer>> deepCopy(Map<Integer, List<Integer>> nodeToNodes) {
		Map<Integer, List<Integer>> nodeToNodesCopy = new HashMap<>();

		for (Entry<Integer, List<Integer>> aEntry : nodeToNodes.entrySet()) {
			List<Integer> aVal = new ArrayList<>(aEntry.getValue());
			nodeToNodesCopy.put(aEntry.getKey(), aVal);
		}

		return nodeToNodesCopy;
	}

	// pick manually Vertices #Parent and #Child and merge Child into Parent
	static void merging(Map<Integer, List<Integer>> nodeToNodes, int parent, int child) {
		// content of Child goes into Parent
		nodeToNodes.get(parent).addAll(nodeToNodes.get(child));
		// Child is removed from map
		nodeToNodes.remove(child);
		// replace child with parent in all references
		for (Entry<Integer, List<Integer>> aEntry : nodeToNodes.entrySet()) {
			List<Integer> newNodes = new ArrayList<>();
			for (Integer aNode : aEntry.getValue()) {
				// replace child by parent
				Integer toAdd;
				if (aNode.equals(child)) {
					toAdd = parent;
				} else {
					toAdd = aNode;
				}
				// remove self loops (child = parent)
				if (!toAdd.equals(aEntry.getKey())) {
					newNodes.add(toAdd);
				}
			}
			aEntry.setValue(newNodes);
		}

		if (showDetails) {
			println("merging " + child + " into " + parent);
			displayMap(nodeToNodes);
		}
	}

	static void displayMap(Map<Integer, List<Integer>> nodeToNodes) {
		for (Entry<Integer, List<Integer>> aEntry : nodeToNodes.entrySet()) {
			print(aEntry.getKey() + " : ");
			for (int aNode : aEntry.getValue()) {
				print(aNode + ", ");
			}
			println("");
		}
	}

	static void println(Object iStr) {
		System.out.println(iStr.toString());
	}

	static void print(Object iStr) {
		System.out.print(iStr.toString());
	}

}
