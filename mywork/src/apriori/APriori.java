package apriori;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import associationRule.AssociationRule;
import associationRule.AssociationRuleGenerator;

import java.util.Set;
import java.util.StringTokenizer;

import enums.Classification;
import enums.Order;
import itemset.ItemSet;
import itemset.ItemSetIF;
import transaction.Transaction;
import transaction.TransactionSet;
import util.Printer;
import util.Reader;
import util.Subset;

public class APriori implements AprioriInterface {

	// private List<int[]> itemsets;
	private String fileName, outputFile, folderData = "Datasets\\", folderResults = "Results\\";;
	private int N, T;

	protected double minimumSupport;
	private double minimumConfidence;
	protected HashMap<ItemSet, Integer> frequentItemsTable;
	protected HashMap<Integer, Boolean> currentItems;

	protected HashMap<ItemSet, Double> frequentItemset;

	private List<Transaction> transactions;

	protected PrintWriter pw;

	StringBuilder sb = new StringBuilder();

	private Printer printer;

	protected long start = System.currentTimeMillis();

	public APriori(String fileName, double minimumSupport, double minimumConfidence, Classification classification)
			throws Exception {
		this.fileName = fileName;
		this.outputFile = outputFile;
		frequentItemset = new HashMap<>();

		if (minimumConfidence > 1 || minimumConfidence < 0)
			throw new IllegalArgumentException("confidence must be expressed between 0 and 1 (included)");
		else
			this.minimumConfidence = minimumConfidence;

		if (minimumSupport > 1 || minimumSupport < 0)
			throw new IllegalArgumentException(
					"support must be expressed with a double value between 0 and 1 (included)");
		else
			this.minimumSupport = minimumSupport;

		pw = new PrintWriter(new File(folderResults + fileName + ".result"));

		printer = new Printer(this);

		frequentItemsTable = new HashMap<>();
		transactions = new ArrayList<>();
		currentItems = new HashMap();

		Reader.readTransations(this, classification, folderData);

		// System.out.println(max);
		// if (T != transactions.size())
		// System.err.println("ERRRRRRRR" + T + " " + transactions.size());

		printer.printInputSettings();

		System.out.println(frequentItemsTable);
		System.out.println(transactions);
		System.out.println(currentItems);
	}

	@Override
	public void compute() {

		// System.out.println(frequentItemsTable);
		// sb.append("FIT size = " + frequentItemsTable.size() + "\n");
		// System.out.println("FIT size = " + frequentItemsTable.size());

		// System.out.println(frequentItemsTable);
		System.out.println("Pruning occurrencies of size 1");
		sb.append("Pruning occurrencies of size 1\n");
		prune(1);

		sb.append("Found " + frequentItemsTable.size() + " frequent itemsets of size 1" + " (with support "
				+ (minimumSupport * 100) + "%)\n\n");
		System.out.println("Found " + frequentItemsTable.size() + " frequent itemsets of size 1" + " (with support "
				+ (minimumSupport * 100) + "%)\n");
		// System.out.println(frequentItemsTable);
		for (int k = 2; frequentItemsTable.size() != 0; k++) {
			sb.append("generating all the tuples of size " + k + "\n");
			System.out.println("generating all the tuples of size " + k);
			generateCk();
			Iterator<ItemSet> it = frequentItemsTable.keySet().iterator();
			// System.out.println(frequentItemsTable);
			sb.append(frequentItemsTable.size() + " of size " + k + " have been generated\n");
			System.out.println(frequentItemsTable.size() + " of size " + k + " have been generated");
			while (it.hasNext()) {
				ItemSet is = it.next();
				countOccurrences(is);
			}

			// System.out.println(frequentItemsTable);

			prune(k);

			sb.append("currentItems size(" + k + ") = " + currentItems.size() + "\n\n");
			System.out.println("currentItems size(" + k + ") = " + currentItems.size() + "\n");

		}

		System.out.println(frequentItemset);	
		
		assocRules();

		long elapsedTime = System.currentTimeMillis() - start;

		sb.append("Elapsed time = " + elapsedTime + "\n");
		System.out.println("Elapsed time(s)= " + (double) elapsedTime / 1000);

		pw.print(sb.toString());
		pw.close();

	}

	private void assocRules() {
		AssociationRuleGenerator arg=new AssociationRuleGenerator(frequentItemset, minimumConfidence, sb);
		arg.assocRules();		
	}

	protected void prune(int k) {
		Iterator<Entry<ItemSet, Integer>> it = frequentItemsTable.entrySet().iterator();
		LinkedList<ItemSet> toDelete = new LinkedList<>();

		System.out.println("Current Items.size= " + currentItems.size());

		// compute the support for all the itemsets in Ck
		while (it.hasNext()) {
			Map.Entry<ItemSet, Integer> pair = (Map.Entry) it.next();
			double sup = computeSup(pair.getValue());
			// System.out.println("sup of " + pair.getKey() + " = " + sup);
			if (sup < minimumSupport)
				toDelete.add(pair.getKey());
			else {// itemset is frequen
				for (Integer i : pair.getKey()) {// if an itemset is frequent then all the items in it are frequent
					currentItems.put(i, true);
				}
				// add the frequent itemset to the frequent so i can count the confidence later.
				if (pair.getKey().size() > 0)
					frequentItemset.put(pair.getKey(), sup);
			}
		}
		int numRemoved = toDelete.size();
		int c = 0;
		// System.out.println(frequentItemsTable.size());
		// System.out.println(toDelete.size());

		for (ItemSetIF x : toDelete) {
			frequentItemsTable.remove(x);
			// System.out.println(frequentItemsTable.size());

			// remove from current items
			Iterator<Map.Entry<Integer, Boolean>> itCurr = currentItems.entrySet().iterator();
			while (itCurr.hasNext()) {
				Map.Entry<Integer, Boolean> curr = itCurr.next();

				if (!curr.getValue()) {
					c++;
					itCurr.remove();
					// sb.append(curr.getKey() + " removed\n");
					// System.out.println(curr.getKey() + " removed");
				}
			}
			// System.out.println(toDelete.size());

		}

		resetCurrentItems();

		sb.append("pruned " + numRemoved + " itemsets of size " + k + " and " + c + " elements\n");

		System.out.println("pruned " + numRemoved + " itemsetsof size " + k + " and " + c + " elements");

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

	// count the occurrences of an itemsets in all the transactions and eventually
	// insert the value in the map
	protected void countOccurrences(ItemSet is) {
		int value = frequentItemsTable.get(is);
		for (Transaction t : transactions)
			if (t.containsAll(is))
				value++;
		frequentItemsTable.put(is, value);

	}

	// generate the candidate tuples
	protected void generateCk() {
		HashMap<ItemSet, Integer> newMap = new HashMap<>();
		Iterator<ItemSet> it = frequentItemsTable.keySet().iterator();
		while (it.hasNext()) {
			ItemSetIF temp = it.next();
			Iterator<Integer> it1 = currentItems.keySet().iterator();
			while (it1.hasNext()) {
				int x = it1.next();
				if (x > temp.getMax()) {// ensure the monotonicity
					ItemSet previous = (ItemSet) temp.clone();
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
		// APriori ap = new APriori("books.dat", (double) 0.020, 0.2,
		// Classification.BIPARTITEbi);
		// ap.compute();
		//
		// APriori ap1 = new APriori("conversion", (double) 0.02, 0.2,
		// Classification.TRANSACTIONS);
		//
		// ap1.compute();

		APriori ap = new APriori("USocial.dat", (double) 0.2, 0.1, Classification.USOCIAL);
		ap.compute();

	}

	public void currentItemsPut(int x, Boolean b) {
		currentItems.put(x, b);
	}

	public String getFolderData() {
		return folderData;
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

	public PrintWriter getPrintWriter() {
		return pw;
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
	
	public HashMap<ItemSet,Double> getFrequentItemset(){
		return frequentItemset;
	}
	public StringBuilder getStringBuilder() {
		return sb;
	}

}
