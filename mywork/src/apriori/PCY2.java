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
import util.SubsetIterator;

public class PCY2 extends AbstractAPriori {

	private HashMap<Integer, Integer> buckets;

	private BitSet bitmap;	

	private int  tuplesAvoided = 0, tuplesGenerated, mod = 0;	

	private long timeStep;

	public PCY2(String fileName, double minimumSupport, int maxK, Classification classification) throws Exception {
		super(fileName, minimumSupport, maxK, classification);
	}

	@Override
	protected void step(int k) {
		
		tuplesRemoved = 0;
		timeStep = System.currentTimeMillis();
		pcyStep(k);
		try {
			generateCk(k);

		} catch (OutOfMemoryError e) {

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

		

		s = "Elapsed time(s) for step #" + k + " = " + (System.currentTimeMillis() - timeStep) / 1000 + "\n";
		sb.append(s + "\n");
		System.out.println(s);

	}

	@Override
	public void compute() {
		super.compute();

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
		int value = 0;
		for (Transaction t : transactions)
			if (t.containsAll(is))
				value++;
		int ind = hashFunction(is);
		if (!buckets.containsKey(ind))
			buckets.put(ind, value);
		else {
			int old = buckets.get(ind);
			old += value;
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
		mod = 3 * N;
		// buckets = new int[mod];
		buckets = new HashMap<>();
		Iterator<ItemSet> it = frequentItemsTable.keySet().iterator();
		while (it.hasNext()) {
			ItemSetIF temp = it.next();
			Iterator<Integer> it1 = currentItems.keySet().iterator();
			while (it1.hasNext()) {
				int x = it1.next();
				if (x > temp.getMax()) {// ensure the monotonicity
					ItemSet previous = (ItemSet) temp.clone();
					previous.add(x);
					boolean flag = true;
					SubsetIterator<Integer> sit = new SubsetIterator<>(previous, k - 1);
					while (sit.hasNext()) {
						if (!frequentItemset.containsKey(sit.next())) {
							flag = false;
							break;
						}
					}

					if (flag)
						countOccurrencesPCY(previous);
				}
			}
		}
		// System.out.println(Arrays.toString(buckets));
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
		int tempAcc = 0, t = 0;
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

					boolean flag = true;
					if (bitmap.get(hashFunction(previous))) {

						SubsetIterator<Integer> sit = new SubsetIterator<>(previous, k - 1);
						while (sit.hasNext()) {
							if (!frequentItemset.containsKey(sit.next())) {
								flag = false;
								t++;
								break;
							}
						}
						if (flag)
							// newMap.put(previous, 0);
							if (prune(k, previous, newMap))
								tuplesRemoved++;

					} else
						tempAcc++;

				}
			}
			it.remove();
		}

		// remove from current items

		frequentItemsTable = newMap;

		int itemsRemoved = removeUnfrequentCurrentItems();

		if (!frequentItemsTable.isEmpty())
			cleanFrequentItemset(k);
		resetCurrentItems();

		totalAccorgimento += t;
		frequentItemsTable = newMap;
		tuplesAvoided += tempAcc;
		tuplesGenerated += frequentItemsTable.size();

		s = t + " avoided accorgimento in step #" + k;
		System.out.println(s);
		sb.append(s);

		s = tempAcc + " avoided PCY in step #" + k;
		System.out.println(s);
		sb.append(s);

		s = frequentItemsTable.size() + " of size " + k + " have been generated from " + currentItems.size() + " items";
		sb.append(s + "\n");
		System.out.println(s);

		s = "pruned " + tuplesRemoved + " itemsetsof size " + k + " and " + itemsRemoved + " elements";
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

	public static void main(String[] args) throws Exception {

		PCY pcy = new PCY("kosarak.dat", (double) 0.0005, 0, Classification.TRANSACTIONS);
		pcy.compute();

		AssociationRuleGenerator arg = new AssociationRuleGenerator(pcy.getFrequentItemset(), 0.9,
				pcy.getStringBuilder());
		arg.assocRules();

		APriori ap = new APriori("kosarak.dat", (double) 0.02, 0, Classification.TRANSACTIONS);
		ap.compute();
		AssociationRuleGenerator arg1 = new AssociationRuleGenerator(ap.getFrequentItemset(), 0.9,
				ap.getStringBuilder());
		arg1.assocRules();

		System.out.println(arg.getAssociationRules().containsAll(arg1.getAssociationRules()));

	}

	@Override
	public void results() {
		// TODO Auto-generated method stub

	}

}
