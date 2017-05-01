import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// yay, works with Duplicates too
public class Coursera_Algo_NbOfInversions {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("hey");

		// mac format (sorry, been lazy there)
		// String fileName =
		// "/Users/VinZ/Dropbox/Work/JAVA/Training/src/IntegerArray.txt";
		String fileName = "C:\\Users\\vpingard\\Dropbox\\Work\\JAVA\\Training\\src\\IntegerArrayDuplicate4.txt";

		// read file into stream, try-with-resources
		List<Integer> aList = new ArrayList<Integer>();
		try (Stream<String> stream = Files.lines(Paths.get(fileName))) {

			aList = stream.map(Integer::parseInt).collect(Collectors.toList());

		} catch (IOException e) {
			e.printStackTrace();
		}

		// Sort and count inversions
		// int prev = Integer.MIN_VALUE;
		// for (int i : aList) {
		// if (prev == Integer.MIN_VALUE) {
		// prev = i;
		// } else {
		// if (prev == i) {
		// System.out.println("duplicate " + i);
		// }
		// }
		// prev = i;
		// // System.out.println(i);
		// }

		// debug
		// aList.clear();
		// for (int i = 1; i < 100000; i++) {
		// aList.add(i);
		// }
		// Collections.shuffle(aList);

		// like a mutable Integer.
		long nbOfInversions = countInvAndSort(aList, 0, aList.size() - 1);

		System.out.println("done : " + nbOfInversions);
		for (int i : aList) {
			System.out.println(i);
		}

	}

	static long countInvAndSort(List<Integer> ioList, int begPoint, int endPoint) {
		long[] nbOfInversions = new long[1];
		nbOfInversions[0] = 0;
		countInvAndSort(ioList, begPoint, endPoint, nbOfInversions);
		return nbOfInversions[0];
	}

	static void countInvAndSort(List<Integer> ioList, int begPoint, int endPoint, long[] nbOfInversions) {
		if (endPoint - begPoint < 1) // 1 or none elem
			return;
		int aMidPoint = begPoint + ((endPoint - begPoint) / 2);
		countInvAndSort(ioList, begPoint, aMidPoint, nbOfInversions);
		countInvAndSort(ioList, aMidPoint + 1, endPoint, nbOfInversions);
		mergeTwoHalves(ioList, begPoint, aMidPoint, endPoint, nbOfInversions);
	}

	private static void mergeTwoHalves(List<Integer> ioList, int iBegPoint, int iMidPoint, int iEndPoint,
			long[] nbOfInversions) {

		int n = iEndPoint - iBegPoint;
		List<Integer> aTemp = new ArrayList<Integer>();
		int i = iBegPoint, j = iMidPoint + 1;
		int iNum = -1, jNum = -1;
		for (int k = 0; k <= n; k++) {
			iNum = ioList.get(i);
			jNum = ioList.get(j);
			if (iNum < jNum) {
				aTemp.add(iNum);
				if (i < iMidPoint)
					i++;
				else if (j <= iEndPoint) {
					for (int k0 = j; k0 <= iEndPoint; k0++) {
						aTemp.add(ioList.get(k0));
					}
					break;
				}

			} else if (iNum > jNum) {
				// also add all the previous ones from
				// the second half
				nbOfInversions[0] = nbOfInversions[0] + iMidPoint + 1 - i;
				aTemp.add(jNum);
				if (j < iEndPoint)
					j++;
				else if (i <= iMidPoint) {
					for (int k0 = i; k0 <= iMidPoint; k0++) {
						aTemp.add(ioList.get(k0));
					}
					break;
				}

			} else { // same numbers

				aTemp.add(jNum);
				for (int k0 = i; k0 <= iMidPoint; k0++) {
					if (ioList.get(k0) > jNum) { // there must be a better way.
						nbOfInversions[0]++;
					}
				}
				if (j < iEndPoint) {
					j++;
				} else if (i <= iMidPoint) {
					for (int k0 = i; k0 <= iMidPoint; k0++) {
						aTemp.add(ioList.get(k0));
					}
					break;
				}
			}
		}
		i = 0;
		for (int k = iBegPoint; k <= iEndPoint; k++) {
			ioList.set(k, aTemp.get(i));
			i++;
		}

	}
}
