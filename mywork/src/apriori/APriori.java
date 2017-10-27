package apriori;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

public class APriori implements AprioriInterface {

	// private List<int[]> itemsets;
	private String fileName;
	private int N, T;

	private int max;

	private double minimumSupport, minimumConfidence;
	private HashMap<ItemSet, Integer> itemsCountMap;

	private List<Transaction> transactions;

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

		readTransactions();

		printInputSettings();

	}

	private void readTransactions() throws Exception {
		N = 0;
		T = 0;
		itemsCountMap = new HashMap<>();
		BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
		transactions = new ArrayList<>();
		while (br.ready()) {
			String s = br.readLine();
			T++;
			Transaction t = new Transaction(s);
			StringTokenizer st = new StringTokenizer(s, " ");
			while (st.hasMoreTokens()) {
				int x = Integer.parseInt(st.nextToken());
				ItemSet unary = new ItemSet(1);
				unary.add(x);
				t.addItem(x);
				if (x > max)
					max = x;
				if (!itemsCountMap.containsKey(unary)) {
					itemsCountMap.put(unary, 1);
					N++;
				} else {
					int old = itemsCountMap.get(unary);
					itemsCountMap.put(unary, ++old);
					// System.out.println("OKKKKKKKKKKKKKKK");

				}

			}
			transactions.add(t);
		}
		br.close();
	}

	private void printInputSettings() {
		System.out.println("Transactions:\n"+ transactions);

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
		Iterator<Entry<ItemSet, Integer>> it = itemsCountMap.entrySet().iterator();
		LinkedList<ItemSet> toDelete = new LinkedList<>();
		while (it.hasNext()) {
			Map.Entry<ItemSet, Integer> pair = (Map.Entry) it.next();
			double sup = computeSup(pair.getValue());
			System.out.println("sup of " + pair.getKey() + " = " + sup);
			if (sup < minimumSupport)
				toDelete.add(pair.getKey());
		}
		for (ItemSet x : toDelete) {
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

	public static void main(String[] args) throws Exception {
		APriori ap = new APriori("chess.dat", 0.5, 0.8);
		ap.compute();


	}

	public HashMap<ItemSet, Integer> getItemsCountMap() {
		return itemsCountMap;

	}

}
