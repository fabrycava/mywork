package apriori;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import enums.Classification;
import itemset.ItemSet;
import itemset.ItemSetIF;
import transaction.Transaction;
import util.Reader;

public class APriori extends AbstractAPriori {

	protected HashMap<ItemSet, Integer> frequentItemsTable;
	
	


	public APriori(String fileName, double minimumSupport, double minimumConfidence, Classification classification)
			throws Exception {
		super(fileName, minimumSupport, minimumConfidence, classification);


		frequentItemsTable = new HashMap<>();

		Reader.readTransations(this, classification, folderData);
		StringBuilder sb = new StringBuilder();
		sb.append("Folder:" + folderData + "\n" + "fileName:" + fileName + "\n\n");
		sb.append("Input configuration: \n" + N + " items, " + T + " transactions, ");
		sb.append("Min Sup = " + (minimumSupport * 100) + "%\n");
		sb.append("Min Conf = " + (minimumConfidence * 100) + "%\n");

		pw.write(sb.toString() + "\n");

		System.out.println(sb.toString());

		// System.out.println(frequentItemsTable);
		//System.out.println(transactions);
		// System.out.println(currentItems);
	}

	@Override
	public void compute() {

		// System.out.println(frequentItemsTable);
		// sb.append("FIT size = " + frequentItemsTable.size() + "\n");
		// System.out.println("FIT size = " + frequentItemsTable.size());

		// System.out.println(frequentItemsTable);
		System.out.println("Pruning occurrencies of size 1");
		sb.append("Pruning occurrencies of size 1\n");
		minimumSupport=80;
		prune(1);

		sb.append("Found " + frequentItemsTable.size() + " frequent itemsets of size 1" + " (with support "
				+ (minimumSupport * 100) + "%)\n\n");
		System.out.println("Found " + frequentItemsTable.size() + " frequent itemsets of size 1" + " (with support "
				+ (minimumSupport * 100) + "%)\n");
		// System.out.println(frequentItemsTable);
		for (int k = 2; frequentItemsTable.size() != 0; k++) {
			minimumSupport=Math.max(minimumSupport, k);
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

		// System.out.println(frequentItemset);

		// assocRules();

		long elapsedTime = System.currentTimeMillis() - start;

		sb.append("Elapsed time = " + elapsedTime + "\n");
		System.out.println("Elapsed time(s)= " + (double) elapsedTime / 1000);

		pw.print(sb.toString());
		pw.close();

	}

	// private void assocRules() {
	// AssociationRuleGenerator arg=new AssociationRuleGenerator(frequentItemset,
	// minimumConfidence, sb);
	// arg.assocRules();
	// }

	protected void prune(int k) {
		Iterator<Entry<ItemSet, Integer>> it = frequentItemsTable.entrySet().iterator();
		LinkedList<ItemSet> toDelete = new LinkedList<>();

		System.out.println("Current Items.size= " + currentItems.size());

		// compute the support for all the itemsets in Ck
		while (it.hasNext()) {
			Map.Entry<ItemSet, Integer> pair = (Map.Entry) it.next();
			//double sup = computeSup(pair.getValue());
			// System.out.println("sup of " + pair.getKey() + " = " + sup);
			if (pair.getValue() < minimumSupport)
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

		APriori ap = new APriori("kosarak.dat", (double) 0.02, 0.1, Classification.TRANSACTIONS);
		ap.compute();

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

}
