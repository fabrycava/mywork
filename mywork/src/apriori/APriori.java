package apriori;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

public class APriori implements AprioriInterface {

	private List<int[]> itemsets;
	private String fileName;
	private int N, T;

	private int max;

	private double minimumSupport, minimumConfidence;
	private HashMap<Integer, Integer> itemsCountMap;

	private List<String> tuples;

	private long start = System.currentTimeMillis();

	public APriori(String fileName, double minimumSupport, double minimumConfidence) throws Exception {
		this.fileName = fileName;

		if (minimumConfidence > 1 || minimumConfidence < 0)
			throw new IllegalArgumentException("confidence must be expressed between 0 and 1 (included)");
		else
			this.minimumConfidence = minimumConfidence;

		if (minimumSupport > 1 || minimumSupport < 0)
			throw new IllegalArgumentException(
					"support must be expressed with a double value between 0 and 1 (included)");
		else
			this.minimumSupport = minimumSupport;

		readLines();
		printInputSettings();

	}

	private void readLines() throws Exception {

		N = 0;
		T = 0;
		itemsCountMap = new HashMap<>();
		BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
		while (br.ready()) {
			String s = br.readLine();
			T++;
			StringTokenizer st = new StringTokenizer(s, " ");
			while (st.hasMoreTokens()) {
				int x = Integer.parseInt(st.nextToken());
				if (x > max)
					max = x;
				if (!itemsCountMap.containsKey(x)) {
					itemsCountMap.put(x, 1);
					N++;
				} else {
					int old = itemsCountMap.get(x);

					itemsCountMap.put(x, ++old);
				}

			}
		}
	}

	private void printInputSettings() {

		System.out.println("Input configuration: \n" + N + " items, " + T + " transactions, ");
		System.out.println("Max =" + max);
		System.out.println("Min Sup = " + minimumSupport + "%");
		System.out.println("Min Conf = " + minimumConfidence + "%");
		System.out.println("itemsCount" + itemsCountMap);

	}

	@Override
	public void compute() {

		sizeOne();
		System.out.println("Found " + itemsCountMap.size() + " frequent itemsets of size 1" + " (with support "
				+ (minimumSupport * 100) + "%)");
		;
		int currentItemset = 1;
		int nbFrequentSets = 0;

		// while (itemsets.size() != 0) {
		//
		// }

		long elapsedTime = System.currentTimeMillis() - start;

		System.out.println("Elapsed time = " + elapsedTime);

	}

	private void sizeOne() {
		Iterator<Entry<Integer, Integer>> it = itemsCountMap.entrySet().iterator();
		LinkedList<Integer> toDelete = new LinkedList<>();
		while (it.hasNext()) {
			Map.Entry<Integer, Integer> pair = (Map.Entry) it.next();

			double sup = computeSup(pair.getValue());
			System.out.println("sup of " + pair.getKey() + " = " + sup);
			if (sup < minimumSupport)
				toDelete.add(pair.getKey());

		}
		for (int x : toDelete) {
			itemsCountMap.remove(x);
		}

	}

	private double computeSup(int value) {

		double sup = ((double) value / T);

		return sup;
	}

	@Override
	public void results() {
		// TODO Auto-generated method stub

	}

	private String showItemsets() {
		StringBuilder sb = new StringBuilder("{");

		for (int[] i : itemsets) {
			sb.append(Arrays.toString(i) + ", ");

		}
		sb.append("}");
		return sb.toString();
	}

	public static void main(String[] args) throws Exception {
		APriori ap = new APriori("chess.dat", 0.5, 0.8);
		ap.compute();

		Object[] o = ap.getItemsCountMap().keySet().toArray();
		int [] a=new int[o.length];
		for(int i=0;i<o.length;i++) {
			a[i]=(int)o[i];			
		}
		permute(a, 2);
		
		

	}

	public HashMap<Integer, Integer> getItemsCountMap() {
		return itemsCountMap;

	}

	static void permute(int[]a, int k) {
		
		
		if (k == a.length) {
			for (int i = 0; i < a.length; i++) {
				System.out.print(" [" + a[i] + "] ");
			}
			System.out.println();
		} else {
			for (int i = k; i < a.length; i++) {
				int temp = a[k];
				a[k] = a[i];
				a[i] = temp;

				permute(a, k + 1);

				temp = a[k];
				a[k] = a[i];
				a[i] = temp;
			}
		}
	}

}
