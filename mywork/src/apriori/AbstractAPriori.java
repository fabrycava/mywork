package apriori;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import enums.Classification;
import itemset.ItemSet;
import itemset.ItemSetIF;
import transaction.Transaction;

public abstract class AbstractAPriori implements AprioriInterface {

	protected String fileName, folderData = "Datasets\\", folderResults = "Results\\";;
	protected int N, T;

	String s;
	protected double minimumSupport, fixedSupport;
	protected double minimumConfidence;
	// protected Printer printer;

	protected List<Transaction> transactions;

	protected HashMap<Integer, Boolean> currentItems;

	protected HashMap<ItemSet, Integer> frequentItemsTable;

	protected HashMap<ItemSet, Double> frequentItemset;
	protected StringBuilder sb;

	protected PrintWriter pw;
	protected long start;
	protected int k, reductionStep, maxItem;
	protected boolean CD = false;

	public AbstractAPriori(String fileName, double minimumSupport, double minimumConfidence,
			Classification classification) throws Exception {
		start = System.currentTimeMillis();
		this.fileName = fileName;
		// frequentItemset = new HashMap<>();

		if (minimumConfidence > 1 || minimumConfidence < 0)
			throw new IllegalArgumentException("confidence must be expressed between 0 and 1 (included)");
		else
			this.minimumConfidence = minimumConfidence;

		if (minimumSupport > 1 || minimumSupport < 0)
			throw new IllegalArgumentException(
					"support must be expressed with a double value between 0 and 1 (included)");
		else {
			this.minimumSupport = minimumSupport;
			fixedSupport = minimumSupport;
		}

		pw = new PrintWriter(new File(folderResults + fileName + ".result"));

		sb = new StringBuilder();

		frequentItemsTable = new HashMap<>();
		transactions = new ArrayList<>();
		currentItems = new HashMap();
		frequentItemset = new HashMap<>();

	}

	protected void contractSupport() {
		minimumSupport = (minimumSupport * k) / 10;
	}

	protected void printInputSettings() {
		sb.append("Folder:" + folderData + "\n" + "fileName:" + fileName + "\n\n");
		sb.append("Input configuration: \n" + N + " items, " + T + " transactions, ");
		sb.append("Min Sup = " + (minimumSupport * 100) + "%\n");
		sb.append("Min Conf = " + (minimumConfidence * 100) + "%\n");
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

	public HashMap<ItemSet, Double> getFrequentItemset() {
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
		reductionStep = 0;

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
			double sup = computeSup(pair.getValue());
			if (sup < minimumSupport)
				toDelete.add(pair.getKey());
			else {// itemset is frequen
				for (Integer i : pair.getKey()) {// if an itemset is frequent then all the items in it are frequent
					currentItems.put(i, true);
				}
				// add the frequent itemset to the frequent so i can count the confidence later.
				if (pair.getKey().size() > 0)
					frequentItemset.put(pair.getKey(), Double.valueOf(pair.getValue()));
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

		if (CD && !frequentItemsTable.isEmpty())
			cleanFrequentItemset(k);
		resetCurrentItems();
		s = "pruned " + numRemoved + " itemsetsof size " + k + " and " + c + " elements";
		sb.append(s + "\n");
		System.out.println(s);

		s = "Found " + frequentItemsTable.size() + " frequent itemsets of size " + k + " (with support "
				+ (minimumSupport * 100) + "%)";
		sb.append(s + "\n");
		System.out.println(s);
		s = "Items currently frequent= " + currentItems.size();
		System.out.println(s);
		sb.append(s + "\n");

	}

	public HashMap<ItemSet, Integer> getFIT() {
		return frequentItemsTable;
	}

	public int getK() {
		return k;
	}

	private void cleanFrequentItemset(int k) {
		int max = 0;
		Iterator<ItemSet> it = frequentItemset.keySet().iterator();
		while (it.hasNext()) {

			ItemSet is = it.next();
			int size = is.size();
			max = Math.max(max, size);
			if (!(size == k))
				it.remove();
		}
		System.out.println("max = " + max + "\tk =" + k);
	}

	public void setCD() {
		CD = true;
	}

}
