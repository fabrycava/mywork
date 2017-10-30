package apriori;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import util.Printer;

public class APriori implements AprioriInterface, Cloneable {

	// private List<int[]> itemsets;
	private String fileName, outputFile;
	private int N, T;

	private int max;

	private double minimumSupport, minimumConfidence;
	private HashMap<ItemSet, Integer> frequentItemsTable;
	private HashSet<Integer> currentItems;

	private List<Transaction> transactions;

	private Printer printer;

	private long start = System.currentTimeMillis();

	public APriori(String fileName, double minimumSupport, double minimumConfidence, String outputFile)
			throws Exception {
		this.fileName = fileName;
		this.outputFile = outputFile;

		if (minimumConfidence > 1 || minimumConfidence < 0)
			throw new IllegalArgumentException("confidence must be expressed between 0 and 1 (included)");
		else
			this.minimumConfidence = minimumConfidence;

		if (minimumSupport > 1 || minimumSupport < 0)
			throw new IllegalArgumentException(
					"support must be expressed with a double value between 0 and 1 (included)");
		else
			this.minimumSupport = minimumSupport;

		printer = new Printer(this, outputFile);

		readTransactions();

		printer.printInputSettings();

	}

	public double getMinimumSupport() {
		return minimumSupport;
	}

	public double getMinimumConfidence() {
		return minimumConfidence;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public String getFileName() {
		return fileName;
	}

	public int getN() {
		return N;
	}

	public int getT() {
		return T;
	}

	
	
	private void readTransactions() throws Exception {
		N = 0;
		T = 0;
		frequentItemsTable = new HashMap<>();
		BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
		transactions = new ArrayList<>();
		currentItems=new HashSet<>();
		while (br.ready()) {
			String s = br.readLine();
			T++;
			Transaction t = new Transaction(s);
			StringTokenizer st = new StringTokenizer(s, " ");
			while (st.hasMoreTokens()) {
				int x = Integer.parseInt(st.nextToken());
				ItemSet unary = new ItemSet();
				unary.add(x);
				t.addItem(x);
				currentItems.add(x);
				if (x > max)
					max = x;
				if (!frequentItemsTable.containsKey(unary)) {
					frequentItemsTable.put(unary, 1);
					N++;
				} else {
					int old = frequentItemsTable.get(unary);
					frequentItemsTable.put(unary, ++old);
				}
			}
			transactions.add(t);
		}
		br.close();
	}

	@Override
	public void compute() {

		sizeOne();
		System.out.println("Found " + frequentItemsTable.size() + " frequent itemsets of size 1" + " (with support "
				+ (minimumSupport * 100) + "%)");

		// decommentare dopo aver creato generateC()
		// iterazione sui Ck Lk
		int currentItemset = 1;
		int nbFrequentSets = 0;
		
		
		generateCk();
		
		
		
		System.out.println(frequentItemsTable);
		System.out.println(frequentItemsTable.size());

//		for (int k = 2; frequentItemsTable.size() != 0; k++) {
//			generateCk();
//			Iterator<ItemSet> it = frequentItemsTable.keySet().iterator();
//			while (it.hasNext()) {
//				ItemSet is = it.next();
//				int value = frequentItemsTable.get(is);
//				for (Transaction t : transactions)
//					if (t.containsItemset(is))
//						value++;
//				frequentItemsTable.put(is, value);
//			}
//
//			// prune();
//
//		}

		long elapsedTime = System.currentTimeMillis() - start;

		System.out.println("Elapsed time = " + elapsedTime);

	}

	private void generateCk() {
		HashMap<ItemSet, Integer> newMap=new HashMap<>();
		Iterator<ItemSet> it = frequentItemsTable.keySet().iterator();
		while (it.hasNext()) {
			ItemSet temp = it.next();
			Iterator<Integer> it1 = currentItems.iterator();
			while (it1.hasNext()) {
				int x = it1.next();
				if (!temp.contains(x)) {
					ItemSet previous = temp.clone();
					previous.add(x);
					if (!newMap.containsKey(previous))
						newMap.put(previous, 0);
				}
			}
			it.remove();
		}
		frequentItemsTable=newMap;
	}

	private void sizeOne() {
		Iterator<Entry<ItemSet, Integer>> it = frequentItemsTable.entrySet().iterator();
		LinkedList<ItemSet> toDelete = new LinkedList<>();
		while (it.hasNext()) {
			Map.Entry<ItemSet, Integer> pair = (Map.Entry) it.next();
			double sup = computeSup(pair.getValue());
			// System.out.println("sup of " + pair.getKey() + " = " + sup);
			if (sup < minimumSupport)
				toDelete.add(pair.getKey());
		}
		int numRemoved = toDelete.size();
		for (ItemSet x : toDelete) {
			frequentItemsTable.remove(x);
		}
		System.out.println("pruned " + numRemoved + " elements from itemsets of size 1");

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
		APriori ap = new APriori("chess.dat", 0.5, 0.8, "result.txt");

		ap.compute();
		
		HashSet<Integer> prova=new HashSet<>();
		prova.add(1);
		prova.add(1);
		prova.add(1);
		prova.remove(1);
		System.out.println(prova);

	}

	public HashMap<ItemSet, Integer> getItemsCountMap() {
		return frequentItemsTable;

	}

}
