import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Coursera_algo_Median {

	static boolean _debug = true;

	public static void main(String... arg) {
		String fileName = "/Users/VinZ/Dropbox/Work/JAVA/Training/src/Median_Small.txt"; // _Small
		fileName = "/Users/VinZ/Dropbox/Work/JAVA/Training/src/Median.txt";
		// read file into stream, try-with-resources
		List<String> aList = new ArrayList<>();
		try (Stream<String> stream = Files.lines(Paths.get(fileName))) {

			aList = stream.collect(Collectors.toList());

		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("init");
		init(aList);

		debug("done: ");
	}

	// static List<Integer> nums = new ArrayList<>();
	// max prioQ on left
	static PriorityQueue<Integer> lows = new PriorityQueue<>(Collections.reverseOrder());
	// min prioQ on right
	static PriorityQueue<Integer> highs = new PriorityQueue<>();

	static void init(List<String> str) {
		int count = 0;
		long sum = 0L;
		for (String a : str) {
			int num = Integer.parseInt(a);
			if (count++ == 0) {
				lows.add(num);
				// highs.add(num);
			} else // queues are initialized
			{
				if (num < lows.peek()) {
					// add in left
					lows.add(num);
				} else {
					highs.add(num);
				}

				// rebalance ( when ODD: left is larger by 1)
				if (lows.size() == highs.size() + 2) {
					// move from L to R
					int max = lows.poll();
					highs.add(max);
				} else if (highs.size() == lows.size() + 1) {
					// move from R to L
					int min = highs.poll();
					lows.add(min);
				}

			}

			// debugln("lows : " + lows);
			// debugln("highs : " + highs);
			// debugln(" cur median " + lows.peek());
			sum += lows.peek();
		}

		debugln("sum of medians " + sum % 10_000);
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
