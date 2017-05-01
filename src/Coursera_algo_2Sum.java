import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Coursera_algo_2Sum {

	static boolean _debug = true;

	// would be nice: implement own hashing function and compare performances
	// under chaining or open addresses approaches to solve collisions

	public static void main(String... arg) {
		String fileName = "/Users/VinZ/Dropbox/Work/JAVA/Training/src/2sum_Small.txt"; // _Small
		fileName = "/Users/VinZ/Dropbox/Work/JAVA/Training/src/2sum.txt";
		// read file into stream, try-with-resources
		List<String> aList = new ArrayList<>();
		try (Stream<String> stream = Files.lines(Paths.get(fileName))) {

			aList = stream.collect(Collectors.toList());

		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("init");
		init(aList);

		int nbOfMatchingTargets = 0;
		for (int i = -10000; i <= 10000; i++) {
			nbOfMatchingTargets += getNbOfPairs(i);
			if (i % 10 == 0) {
				debugln("done with " + i + " , currently : " + nbOfMatchingTargets);
			}
		}

		// found: 427
		debug("done: " + nbOfMatchingTargets);
	}

	static Set<Long> nums = new HashSet<>();

	static int getNbOfPairs(long total) {
		for (long num : nums) {
			long lookingFor = total - num;
			if (lookingFor != num && nums.contains(lookingFor)) {
				return 1;
			}
		}
		return 0;
	}

	static void init(List<String> str) {
		for (String a : str) {
			nums.add(Long.parseLong(a));
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
