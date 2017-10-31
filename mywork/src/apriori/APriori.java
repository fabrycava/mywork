package apriori;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
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

	protected double minimumSupport;
	private double minimumConfidence;
	protected HashMap<ItemSet, Integer> frequentItemsTable;
	protected HashMap<Integer, Boolean> currentItems;

	private List<Transaction> transactions;

	protected PrintWriter pw;

	StringBuilder sb = new StringBuilder();

	private Printer printer;

	protected long start = System.currentTimeMillis();

	public APriori(String fileName, double minimumSupport, double minimumConfidence) throws Exception {
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

		pw = new PrintWriter(new File(fileName + ".result"));

		printer = new Printer(this);

		readTransactions();

		printer.printInputSettings();

	}

	public PrintWriter getPrintWriter() {
		return pw;
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
	public HashMap<ItemSet, Integer> getItemsCountMap() {
		return frequentItemsTable;

	}
	private void readTransactions() throws Exception {
		N = 0;
		T = 0;
		frequentItemsTable = new HashMap<>();
		BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
		transactions = new ArrayList<>();
		currentItems = new HashMap();
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
				currentItems.put(x, false);
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

				

//		System.out.println(frequentItemsTable);
//		sb.append("FIT size = " + frequentItemsTable.size() + "\n");
//		System.out.println("FIT size = " + frequentItemsTable.size());

		prune(1);
		
		sb.append("Found " + frequentItemsTable.size() + " frequent itemsets of size 1" + " (with support "
				+ (minimumSupport * 100) + "%)\n");
		System.out.println("Found " + frequentItemsTable.size() + " frequent itemsets of size 1"
				+ " (with support " + (minimumSupport * 100) + "%)");
		System.out.println(frequentItemsTable);
		for (int k = 2; frequentItemsTable.size() != 0; k++) {
			sb.append("generating all the tuples of size " + k + "\n");
			System.out.println("generating all the tuples of size " + k);
			generateCk();
			Iterator<ItemSet> it = frequentItemsTable.keySet().iterator();
			//System.out.println(frequentItemsTable);
			sb.append(frequentItemsTable.size() + " of size " + k + " have been generated\n");
			System.out.println(frequentItemsTable.size() + " of size " + k + " have been generated");
			while (it.hasNext()) {
				ItemSet is = it.next();
				countOccurrences(is);
			}

			// System.out.println(frequentItemsTable);

			prune(k);

			sb.append("currentItems size(" + k + ") = " + currentItems.size() + "\n");
			System.out.println("currentItems size(" + k + ") = " + currentItems.size());

		}

		long elapsedTime = System.currentTimeMillis() - start;

		sb.append("Elapsed time = " + elapsedTime + "\n");
		System.out.println("Elapsed time = " + elapsedTime);
		
		pw.print(sb.toString());
		pw.close();

	}

	

	// reset all the items at false
	protected void resetCurrentItems() {
		HashMap<Integer, Boolean> newCurrentItems = new HashMap<>();
		for (Integer i : currentItems.keySet())
			newCurrentItems.put(i, false);
		currentItems = newCurrentItems;

	}

	// after the counting of the candidate tuples Ck,
	// it prunes all the unfrequent tuples
	protected void prune(int k) {

		Iterator<Entry<ItemSet, Integer>> it = frequentItemsTable.entrySet().iterator();
		LinkedList<ItemSet> toDelete = new LinkedList<>();

		// System.out.println(currentItems);

		// compute the support for all the itemsets in Ck
		while (it.hasNext()) {
			Map.Entry<ItemSet, Integer> pair = (Map.Entry) it.next();
			double sup = computeSup(pair.getValue());
			// System.out.println("sup of " + pair.getKey() + " = " + sup);
			if (sup < minimumSupport)
				toDelete.add(pair.getKey());
			else // if an itemset is frequent then all the items in it are frequent
				for (Integer i : pair.getKey()) {
					// System.out.println(i +" is frequen");
					currentItems.put(i, true);
				}
		}
		int numRemoved = toDelete.size();
		int c = 0;
		for (ItemSet x : toDelete) {
			frequentItemsTable.remove(x);

			// remove from current items
			Iterator<Map.Entry<Integer, Boolean>> itCurr = currentItems.entrySet().iterator();
			while (itCurr.hasNext()) {
				Map.Entry<Integer, Boolean> curr = itCurr.next();
				if (!curr.getValue()) {
					c++;
					itCurr.remove();
					sb.append(curr.getKey() + " removed\n");
					System.out.println(curr.getKey() + " removed");
				}
			}
		}

		resetCurrentItems();

		sb.append("pruned " + numRemoved + " itemsets from itemsets of size " + k + " and " + c + " elements\n");

		System.out.println("pruned " + numRemoved + " itemsets from itemsets of size " + k + " and " + c + " elements");

	}

	// count the occurrences of an itemsets in all the transactions and eventually
	// insert the value in the map
	protected void countOccurrences(ItemSet is) {
		int value = frequentItemsTable.get(is);
		for (Transaction t : transactions)
			if (t.containsItemset(is))
				value++;
		frequentItemsTable.put(is, value);

	}

	// generate the candidate tuples
	protected void generateCk() {
		HashMap<ItemSet, Integer> newMap = new HashMap<>();
		Iterator<ItemSet> it = frequentItemsTable.keySet().iterator();
		while (it.hasNext()) {
			ItemSet temp = it.next();
			Iterator<Integer> it1 = currentItems.keySet().iterator();
			while (it1.hasNext()) {
				int x = it1.next();
				if (x > temp.getMax()) {// ensure the monotonicity
					ItemSet previous = temp.clone();
					previous.add(x);
					if (!newMap.containsKey(previous))
						newMap.put(previous, 0);
				}
			}
			it.remove();
		}
		frequentItemsTable = newMap;
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
		APriori ap = new APriori("kosarak.dat.txt", 0.01, 0.8);

		 ap.compute();


	}

	

}
