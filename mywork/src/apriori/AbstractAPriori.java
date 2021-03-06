package apriori;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import enums.Classification;
import itemset.ItemSet;
import itemset.ItemSetIF;
import transaction.Transaction;
import util.Reader;

public abstract class AbstractAPriori implements AprioriInterface {

	protected String fileName, folderData = "Datasets\\", folderResults = "Results\\";;
	protected int N, T, totalAccorgimento = 0, totalPruned = 0;;

	String s;
	protected int minimumSupport;// , fixedSupport;
	protected double minimumSupportDouble;

	// protected Printer printer;
	long timeStep = System.currentTimeMillis();

	protected List<Transaction> transactions;

	protected HashMap<Integer, Boolean> currentItems;

	protected HashMap<ItemSet, Integer> frequentItemsTable;

	protected HashMap<ItemSet, Integer> frequentItemset;
	protected StringBuilder sb;

	protected PrintWriter pw;
	protected long start;
	protected int k, reductionStep, maxItem, maxK;

	// protected HashSet<ItemSet> incrementedTuples = new HashSet<>();

	protected Classification classification;

	public AbstractAPriori(String fileName, double minimumSupport, int maxK, Classification classification)
			throws Exception {
		start = System.currentTimeMillis();
		this.fileName = fileName;
		this.classification = classification;

		if (maxK < 0)
			throw new IllegalArgumentException("MaxK must be an integer > 0");
		else
			this.maxK = maxK;

		// if (classification == Classification.USOCIAL)
		// CD = true;

		pw = new PrintWriter(new File(folderResults + fileName + "_" + minimumSupport + ".result"));

		sb = new StringBuilder();

		frequentItemsTable = new HashMap<>();
		transactions = new ArrayList<>();
		currentItems = new HashMap();
		frequentItemset = new HashMap<>();

		Reader.readTransations(this, classification, folderData);

		if (minimumSupport < 0)
			throw new IllegalArgumentException(
					"support must be expressed with a double value between 0 and 1 (included) or with an integer value indicating the occurrences");
		else {
			if (minimumSupport < 1) {
				this.minimumSupport = (int) (T * minimumSupport);
				this.minimumSupportDouble = minimumSupport;
			} else {
				this.minimumSupport = (int) minimumSupport;
				this.minimumSupportDouble = ((double) minimumSupport) / T;
				System.out.println(minimumSupport + " " + T + "aaaaaaaaaaaaaaaaa");
			}
			// fixedSupport = minimumSupport;
		}

		printInputSettings();

	}

	protected void printInputSettings() {
		sb.append("Folder:" + folderData + "\n" + "fileName:" + fileName + "\n\n");
		sb.append("Input configuration: \n" + N + " items, " + T + " transactions, ");
		sb.append("Min Sup = " + (minimumSupportDouble * 100) + "%(" + minimumSupport + " occurrences)\n");

		sb.append("classification = " + classification.toString() + "\n");
		// sb.append("Min Conf = " + (minimumConfidence * 100) + "%\n");
		pw.write(sb.toString() + "\n");
		System.out.println(sb.toString());
	}

	public PrintWriter getPrintWriter() {
		return pw;
	}

	public void setMaxItem(int i) {
		maxItem = i;
	}

	public void transactionsAdd(Transaction t) {
		transactions.add(t);
	}

	public void setN(int x) {
		N = x;
	}

	public void setT(int x) {
		T = x;
	}

	public double getMinimumSupport() {
		return minimumSupport;
	}

	protected double computeSup(int value) {

		double sup = ((double) value / T);

		return sup;
	}

	// public double getMinimumConfidence() {
	// return minimumConfidence;
	// }

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

	public String getFolderData() {
		return folderData;
	}

	public void currentItemsPut(int x, Boolean b) {
		currentItems.put(x, b);
	}

	public boolean fITContains(ItemSet unary) {
		return frequentItemsTable.containsKey(unary);
	}

	public void fITPut(ItemSet is, Integer i) {
		frequentItemsTable.put(is, i);
	}

	public int fITGet(ItemSet is) {
		return frequentItemsTable.get(is);
	}

	public HashMap<ItemSet, Integer> getItemsCountMap() {
		return frequentItemsTable;

	}

	public HashMap<ItemSet, Integer> getFrequentItemset() {
		return frequentItemset;
	}

	public StringBuilder getStringBuilder() {
		return sb;
	}

	// reset all the items at false
	protected void resetCurrentItems() {
		HashMap<Integer, Boolean> newCurrentItems = new HashMap<>();
		for (Integer i : currentItems.keySet())
			newCurrentItems.put(i, false);
		currentItems = newCurrentItems;

	}

	protected void countOccurrences() {
		s = "Counting all the occurrences...";
		System.out.println(s);
		sb.append(s + "\n");
		Iterator<ItemSet> it = frequentItemsTable.keySet().iterator();
		while (it.hasNext()) {
			ItemSet is = it.next();
			countOccurrences(is);
		}

	}

	// count the occurrences of an itemsets in all the transactions and eventually
	// insert the value in the map
	protected void countOccurrences(ItemSet is) {
		int value = frequentItemsTable.get(is);
		for (Transaction t : transactions)
			if (t.containsAll(is))
				value++;
		frequentItemsTable.put(is, value);

	}

	@Override
	public void compute() {
		prune(1);

		try {
			for (k = 2; frequentItemsTable.size() != 0 && k <= maxK; k++) {
				int worstCase = frequentItemset.size() * (currentItems.size() - k - 1);
				s = "STEP " + k + "\nNo more than " + worstCase + " will be computed in this step";
				sb.append(s + "\n");
				System.out.println(s);
				step(k);

			}
		} catch (OutOfMemoryError e) {
			catchOutOfMemory();
		}

	}

	protected void catchOutOfMemory() {

		System.out.println();
		long elapsedTime = System.currentTimeMillis() - start;

		s = "Found " + frequentItemsTable.size() + " frequent itemsets of size " + k + " (with support "
				+ (minimumSupport * 100) + "%)";
		sb.append(s + "\n\n");
		System.out.println(s + "\n");
		s = "APriori crashed due to the OutOfMemory!!!!!!!!\nThe result may be highly incorrect\n\n\n"
				+ totalAccorgimento + " risparmiate grazie all' accorgimento ;)\nElapsed time(s)= "
				+ (double) elapsedTime / 1000 + "\n";
		sb.append(s + "\n\n");
		System.out.println(s + "\n");

		pw.print(sb.toString());
		results();
		pw.close();
	}

	// after the counting of the candidate tuples Ck,
	// it prunes all the unfrequent tuples
	protected void prune(int k) {
		s = "Pruning occurrencies of size " + k;
		System.out.println(s);
		sb.append(s + "\n");

		Iterator<Entry<ItemSet, Integer>> it = frequentItemsTable.entrySet().iterator();
		LinkedList<ItemSet> toDelete = new LinkedList<>();

		// compute the support for all the itemsets in Ck
		while (it.hasNext()) {
			Map.Entry<ItemSet, Integer> pair = (Map.Entry) it.next();
			int sup = pair.getValue();
			if (sup < minimumSupport)
				toDelete.add(pair.getKey());
			else {// itemset is frequen
				for (Integer i : pair.getKey()) {// if an itemset is frequent then all the items in it are frequent
					currentItems.put(i, true);
				}
				// add the frequent itemset to the frequent so i can count the confidence later.
				if (pair.getKey().size() > 0)
					frequentItemset.put(pair.getKey(), pair.getValue());
			}
		}
		int numRemoved = toDelete.size();
		int c = 0;
		for (ItemSetIF x : toDelete) {
			frequentItemsTable.remove(x);
			// remove from current items
			Iterator<Map.Entry<Integer, Boolean>> itCurr = currentItems.entrySet().iterator();
			while (itCurr.hasNext()) {
				Map.Entry<Integer, Boolean> curr = itCurr.next();
				if (!curr.getValue()) {
					c++;
					itCurr.remove();
				}
			}
		}

		resetCurrentItems();
		s = "pruned " + numRemoved + " itemsetsof size " + k + " and " + c + " elements";
		sb.append(s + "\n");
		System.out.println(s);

		s = "Found " + frequentItemsTable.size() + " frequent itemsets of size " + k + " (with support "
				+ (minimumSupportDouble * 100) + "%\t(" + minimumSupport + " occurrences)\n";
		sb.append(s + "\n");
		System.out.println(s);
		s = "Items currently frequent= " + currentItems.size() + "\n";
		System.out.println(s);
		sb.append(s + "\n");

	}

	public HashMap<ItemSet, Integer> getFIT() {
		return frequentItemsTable;
	}

	public int getK() {
		return k;
	}

	protected void cleanFrequentItemset(int k) {

	}

	protected abstract void step(int k);

	protected boolean prune(int k, ItemSet is, HashMap<ItemSet, Integer> newMap) {

		int sup = newcountOccurrences(is);

		// System.out.println("occ of " + is+ " = " + sup);
		if ((classification == Classification.USOCIALZ && sup >= k)
				|| (classification == Classification.USOCIALR && sup >= minimumSupport)) {// tuple is frequent
			frequentItemset.put(is, sup);
			newMap.put(is, sup);
			for (Integer i : is) {// if an itemset is frequent then all the items in it are frequent
				currentItems.put(i, true);
			}
			return false;
		}
		return true;

	}

	protected int newcountOccurrences(ItemSet is) {

		int value = 0;
		for (Transaction t : transactions)
			if (t.containsAll(is))
				value++;
		return value;

	}

	protected int removeUnfrequentCurrentItems() {
		// remove from current items
		int c = 0;
		Iterator<Map.Entry<Integer, Boolean>> itCurr = currentItems.entrySet().iterator();
		while (itCurr.hasNext()) {
			Map.Entry<Integer, Boolean> curr = itCurr.next();
			if (!curr.getValue()) {
				c++;
				itCurr.remove();
			}
		}
		return c;
	}

	// public Set<ItemSet> getIngrementedTuple() {
	// return incrementedTuples;
	// }

	@Override
	public void results() {

		for (ItemSet is : frequentItemset.keySet())
			pw.write(is.toString() + "\n");
	}

	protected abstract void generateCk(int k) throws OutOfMemoryError;

}
