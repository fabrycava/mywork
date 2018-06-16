package apriori;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import enums.Classification;
import itemset.ItemSet;
import transaction.Transaction;

public abstract class AbstractAPrioriCommunities extends AbstractAPriori {

	protected HashMap<Integer, Transaction> transactions;
	protected int maxComputableItems = 0;

	public AbstractAPrioriCommunities(String fileName, double minimumSupport, int maxK, Classification classification,
			int maxComputableItems) throws Exception {
		super(fileName, minimumSupport, maxK, classification);
		transactions = new HashMap<>();
		for (Transaction t : super.transactions)
			transactions.put(t.getId(), t);
		this.maxComputableItems = maxComputableItems;

		String s = "Max computable Items = " + maxComputableItems;
		sb.append(s + "\n");
		System.out.println(s);

		// System.out.println(transactions);
		// TODO Auto-generated constructor stub
	}

	public AbstractAPrioriCommunities(String fileName, double minimumSupport, int maxK, Classification classification)
			throws Exception {
		super(fileName, minimumSupport, maxK, classification);
		transactions = new HashMap<>();
		for (Transaction t : super.transactions)
			transactions.put(t.getId(), t);
		// System.out.println(transactions);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void compute() {
		prune(1);

		if (frequentItemsTable.size() > maxComputableItems) {
			LinkedList<Entry<ItemSet, Integer>> l = new LinkedList<>(frequentItemsTable.entrySet());
			Collections.sort(l, new OccurrencesComparator());
			while (l.size() > maxComputableItems) {
				int i = l.getFirst().getKey().getMax();
				frequentItemsTable.remove(l.getFirst().getKey());
				currentItems.remove(i);
				frequentItemset.remove(l.removeFirst().getKey());
			}
			System.out.println("items after forced pruning = " + frequentItemsTable.size() + "\n");
		}
		try {
			for (k = 2; frequentItemsTable.size() != 0 && k <= maxK; k++) {
				// System.out.println("current = " + currentItems.size() + " FIT = " +
				// frequentItemsTable.size() + " FI = "
				// + frequentItemset.size() + "\n");
				int worstCase = frequentItemset.size() * (currentItems.size() - (k - 1));
				// long worstCase=CombinatoricsUtils.binomialCoefficient(currentItems.size(),
				// k);
				s = "STEP " + k + "\nNo more than " + worstCase + " will be computed in this step";
				// sb.append(s + "\n");
				// System.out.println(s);
				step(k);
			}
		} catch (OutOfMemoryError e) {
			catchOutOfMemory();
		}

	}

	@Override
	protected int newcountOccurrences(ItemSet is) {

		int value = 0;
		for (Integer i : is)
			if (transactions.get(i).containsAll(is)) {
				value++;
				// if (value > minimumSupport) {
				// return value;
				// }
			}
		return value;

	}

	private class OccurrencesComparator implements Comparator<Map.Entry<ItemSet, Integer>> {

		@Override
		public int compare(Entry<ItemSet, Integer> o1, Entry<ItemSet, Integer> o2) {
			int x = o1.getValue(), y = o2.getValue();
			return x > y ? 1 : x == y ? 0 : -1;
		}

	}

	@Override
	protected boolean prune(int k, ItemSet is, HashMap<ItemSet, Integer> newMap) {
		// TODO Auto-generated method stub

		int sup = newcountOccurrences(is);
		// System.out.println("occ of " + is+ " = " + sup);
		if ((sup >= is.size())) {
			if (classification == Classification.USOCIALZ) {
				frequentItemset.put(is, sup);
				newMap.put(is, sup);

				for (Integer i : is) {// if an itemset is frequent then all the items in it are frequent
					currentItems.put(i, true);
				}
			}
			return false;
		}
		return true;
	}

	protected boolean pruneR(int k, ItemSet is, HashMap<ItemSet, Integer> newMap) {

		int sup = 0;
		for (Transaction t : transactions.values())
			if (t.containsAll(is))
				sup++;

		//System.out.println("occ of " + is + " = " + sup);
		if (sup >= minimumSupport) {
			//System.out.println("nopasa");
			frequentItemset.put(is, sup);
			newMap.put(is, sup);
			for (Integer i : is) {// if an itemset is frequent then all the items in it are frequent

				//System.out.println("i");
				currentItems.put(i, true);
			}
			return false;
		}
		return true;

	}

	@Override
	protected void cleanFrequentItemset(int k) {
		int max = 0;
		Iterator<ItemSet> it = frequentItemset.keySet().iterator();
		while (it.hasNext()) {

			ItemSet is = it.next();
			int size = is.size();
			max = Math.max(max, size);
			if (!(size == k))
				it.remove();
		}
		// System.out.println("max = " + max + "\tk =" + k);
	}

}
