package apriori;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import associationRule.AssociationRuleGenerator;
import enums.Classification;
import itemset.ItemSet;
import itemset.ItemSetIF;
import transaction.Transaction;
import transaction.TransactionSet;
import util.Reader;
import util.Subset;
import util.SubsetIterator;

public class PCYTransaction extends AbstractAPriori {

	private HashMap<Integer, Integer> buckets;

	private BitSet bitmap;

	boolean flag = false;

	int count = 0;
	int tuplesAvoided = 0, tuplesGenerated, mod = 0;

	int totalAccorgimento = 0;

	public PCYTransaction(String fileName, double minimumSupport, double minimumConfidence, Classification classification)
			throws Exception {
		super(fileName, minimumSupport, minimumConfidence, classification);
		frequentItemsTable = new HashMap<>();
		Reader.readTransations(this, classification, folderData);
		printInputSettings();

	}

	@Override
	public void compute() {
		super.compute();
		long timeStep;
		int previousSize = currentItems.size();
		for (k = 2; frequentItemsTable.size() != 0; k++) {

			// System.out.println(transactions.size());
			// double delta = minimumSupport / 10;
			// if (currentItems.size() > 120) {
			// minimumSupport += delta;
			// System.out.println("new minimum support = " + minimumSupport * 100);
			// } else if (currentItems.size() < 100 && k > 5) {
			// if (flag)
			// minimumSupport -= delta;
			// else
			// minimumSupport += delta;
			// System.out.println("new minimum support = " + minimumSupport * 100);
			// }
			timeStep = System.currentTimeMillis();
			pcyStep(k);
			generateCk(k);
			System.out.println(frequentItemsTable);
			// countOccurrences();
			prune(k);
			if (previousSize > currentItems.size()) {// the number of items is decreasing
				flag = true;
			} else// number of items is not decreasing
				flag = false;
			previousSize = currentItems.size();

			s = "Elapsed time(s) for step #" + k + " = " + (System.currentTimeMillis() - timeStep) / 1000 + "\n";
			sb.append(s + "\n");
			System.out.println(s);
		}

		long elapsedTime = System.currentTimeMillis() - start;

		s = "A total of " + tuplesAvoided + " tuples generation over " + (tuplesGenerated + tuplesAvoided)
				+ " has been avoided thanks the PCY\n";

		System.out.println(totalAccorgimento + " risparmiate grazie all' accorgimento ;)");

		sb.append(s);
		System.out.println(s + "\n");
		s = "Elapsed time(s)= " + (double) elapsedTime / 1000 + "\n";
		sb.append(s + "\n\n");
		System.out.println(s + "\n");
		pw.print(sb.toString());
		pw.close();

	}

	private void countOccurrencesPCY(ItemSet is) {

		int ind = hashFunction(is);
		if (!buckets.containsKey(ind))
			buckets.put(ind, 1);
		else {
			int old = buckets.get(ind);
			old += 1;
			buckets.put(ind, old);
		}
	}

	private int hashFunction(ItemSet is) {
		int hash = is.hashCode() % (mod);
		if (hash < 0) {
			Iterator<Integer> it = is.iterator();
			int newhash = 1;
			while (it.hasNext()) {
				newhash += it.next();
			}
			return newhash % mod;
		}
		return hash;
	}

	private void pcyStep(int k) {
		s = "Starting the PCY # " + (k - 1) + "\n";
		sb.append(s + "\n");
		System.out.println(s);
		mod = N;
		// buckets = new int[mod];
		buckets = new HashMap<>();
		Iterator<Transaction> it = transactions.iterator();

		// if (!flag || subset.size() == 1 || (subset.size() == maxSubsetSize &&
		// frequentItemset.containsKey(subset)))
		int c = 0;
		while (it.hasNext()) {
			System.out.println("workin on t #" + c++);
			TransactionSet t = (TransactionSet) it.next();
			t.getTransactions().retainAll(currentItems.keySet());
			if (k <= t.getTransactions().size()) {
				SubsetIterator<Integer> sit = new SubsetIterator<>(t.getTransactions(), k);
				while (sit.hasNext()) {
					ItemSet is = (ItemSet) sit.next();
					SubsetIterator<Integer> sit2 = new SubsetIterator<>(is, k - 1);
					while (sit2.hasNext())
						if (k == 2 || frequentItemset.containsKey(sit2.next()))
							countOccurrencesPCY(is);
				}

			}
		}

		calculateBitmap();
		// System.out.println(bitmap);
	}

	private void calculateBitmap() {
		bitmap = new BitSet();
		for (Map.Entry<Integer, Integer> pair : buckets.entrySet())
			if (computeSup(pair.getValue()) >= minimumSupport)
				bitmap.set(pair.getKey());
		buckets = null;
	}

	protected void generateCk(int k) {
		s = "generating all the tuples of size " + k;
		sb.append(s + "\n");
		System.out.println(s);
		int tempAcc = 0, c = 0;
		HashMap<ItemSet, Integer> newMap = new HashMap<>();
		Iterator<Transaction> it = transactions.iterator();
		while (it.hasNext()) {
			TransactionSet t = (TransactionSet) it.next();
			t.getTransactions().retainAll(currentItems.keySet());
			if (k - 1 <= t.getTransactions().size()) {
				SubsetIterator<Integer> sit = new SubsetIterator<>(t.getTransactions(), k);
				while (sit.hasNext()) {
					ItemSet is = (ItemSet) sit.next();
					boolean flag = true;
					if (bitmap.get(hashFunction(is))) {
						SubsetIterator<Integer> sit2 = new SubsetIterator<>(is, k - 1);
						while (sit2.hasNext()) {
							if (!frequentItemset.containsKey(sit2.next())) {
								flag = false;
								c++;
								break;
							}
						}
						if (flag)
							countOccurrencesMod(is, newMap);
					} else
						tempAcc++;

				}

			}
			s = c + " avoided accorgimento in step #" + k;
			System.out.println(s);
			sb.append(s);

			totalAccorgimento += c;
			frequentItemsTable = newMap;
			tuplesAvoided += tempAcc;
			tuplesGenerated += frequentItemsTable.size();

			s = tempAcc + " avoided PCY in step #" + k;
			System.out.println(s);
			sb.append(s);

			s = frequentItemsTable.size() + " of size " + k + " have been generated from " + currentItems.size()
					+ " items";
			sb.append(s + "\n");
			System.out.println(s);
		}
	}

	private void countOccurrencesMod(ItemSet is, HashMap<ItemSet, Integer> newMap) {

		if (!frequentItemsTable.containsKey(is))
			newMap.put(is, 1);
		else {
			int old = newMap.get(is);
			old += 1;
			newMap.put(is, old);
		}

	}

	public static void main(String[] args) throws Exception {

		PCY pcy = new PCY("kosarak.dat", (double) 0.0005, 0.9, Classification.TRANSACTIONS);
		pcy.compute();

		AssociationRuleGenerator arg = new AssociationRuleGenerator(pcy.getFrequentItemset(),
				pcy.getMinimumConfidence(), pcy.getStringBuilder());
		arg.assocRules();

		APriori ap = new APriori("kosarak.dat", (double) 0.02, 0.9, Classification.TRANSACTIONS);
		ap.compute();
		AssociationRuleGenerator arg1 = new AssociationRuleGenerator(ap.getFrequentItemset(), ap.getMinimumConfidence(),
				ap.getStringBuilder());
		arg1.assocRules();

		System.out.println(arg.getAssociationRules().containsAll(arg1.getAssociationRules()));

	}

	@Override
	public void results() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void step(int k) {
		// TODO Auto-generated method stub
		
	}
}
