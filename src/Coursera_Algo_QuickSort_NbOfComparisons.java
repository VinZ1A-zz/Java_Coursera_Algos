import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// yay, works with Duplicates too
public class Coursera_Algo_QuickSort_NbOfComparisons {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("hey");

		// mac format (sorry, been lazy there)
		String fileName = "/Users/VinZ/Dropbox/Work/JAVA/Training/src/QuickSort.txt"; // _Small
		// String fileName =
		// "C:\\Users\\vpingard\\Dropbox\\Work\\JAVA\\Training\\src\\QuickSort_Small.txt";

		// read file into stream, try-with-resources
		List<Integer> aList = new ArrayList<Integer>();
		try (Stream<String> stream = Files.lines(Paths.get(fileName))) {

			aList = stream.map(Integer::parseInt).collect(Collectors.toList());

		} catch (IOException e) {
			e.printStackTrace();
		}

		// Sort and count inversions

		// debug
		// aList.clear();
		// for (int i = 1; i <= 100; i++) {
		// aList.add(i);
		// }
		// Collections.shuffle(aList);

		// aList.add(5);
		// aList.add(4);
		// aList.add(3);
		// aList.add(6);
		// aList.add(2);
		// aList.add(1);
		// System.out.println("init ");
		// for (int i : aList) {
		// System.out.println(i);
		// }

		// like a mutable Integer.
		long nbOfComparisons = countCompAndSort(aList, 0, aList.size() - 1);

		System.out.println("done : " + nbOfComparisons);
		for (int i : aList) {
			System.out.println(i);
		}

	}

	static long countCompAndSort(List<Integer> ioList, int begPoint, int endPoint) {
		long[] nbOfComparisons = new long[1];
		nbOfComparisons[0] = 0;
		countCompAndSort(ioList, begPoint, endPoint, nbOfComparisons);
		return nbOfComparisons[0];
	}

	static void countCompAndSort(List<Integer> ioList, int begPoint, int endPoint, long[] nbOfComparisons) {
		int sizeArray = endPoint - begPoint + 1;
		int temp;
		if (sizeArray <= 1)
			return;
		// sort array
		// when pivot is last element, swap first with first element
		// temp = ioList.get(endPoint);
		// ioList.set(endPoint, ioList.get(begPoint));
		// ioList.set(begPoint, temp);

		// compare first, last and mid elem and pick the middle one
		// key is the value of the value-th element
		TreeMap<Integer, Integer> triplette = new TreeMap<Integer, Integer>();
		triplette.put(ioList.get(begPoint), begPoint);
		triplette.put(ioList.get(endPoint), endPoint);
		if (sizeArray % 2 == 0) {
			triplette.put(ioList.get(begPoint + (sizeArray / 2) - 1), begPoint + (sizeArray / 2) - 1);
		} else {
			triplette.put(ioList.get(begPoint + sizeArray / 2), begPoint + sizeArray / 2);
		}

		// Part1: 162085
		// Part2: 164123
		// Part3: 138382

		// here pivot is chosen as the median elem of the three, swap first with
		// first element
		int medianPos = (int) triplette.values().toArray()[1];
		temp = ioList.get(medianPos);
		ioList.set(medianPos, ioList.get(begPoint));
		ioList.set(begPoint, temp);

		int piv = begPoint;
		int valPiv = ioList.get(piv);
		int i = piv;
		for (int j = piv + 1; j <= endPoint; j++) {
			if (ioList.get(j) < valPiv) {
				i++;
				if (j > i) {
					temp = ioList.get(i);
					ioList.set(i, ioList.get(j));
					ioList.set(j, temp);
				}
			}
		}
		if (i != piv) {
			ioList.set(piv, ioList.get(i));
			ioList.set(i, valPiv);
		}

		nbOfComparisons[0] += sizeArray - 1;
		// recursive call for two sub arrays
		countCompAndSort(ioList, begPoint, i - 1, nbOfComparisons);
		countCompAndSort(ioList, i + 1, endPoint, nbOfComparisons);

	}

}
