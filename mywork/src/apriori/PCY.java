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
import util.Reader;
import util.Subset;
import util.SubsetIterator;

public class PCY extends AbstractAPriori {

	private HashMap<Integer, Integer> buckets;

	private BitSet bitmap;

	boolean flag = false;

	int count = 0;
	int tuplesAvoided = 0, tuplesGenerated, mod = 0,previousSize;

	int totalAccorgimento = 0;

	public PCY(String fileName, double minimumSupport, double minimumConfidence, Classification classification)
			throws Exception {
		super(fileName, minimumSupport, minimumConfidence, classification);
		frequentItemsTable = new HashMap<>();
		Reader.readTransations(this, classification, folderData);
		printInputSettings();

	}

	@Override
	public void compute() {
		super.compute();
		
		int previousSize = currentItems.size();
		
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
		mod = 3*N;
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
							newMap.put(previous, 0);
					} else
						tempAcc++;

				}
			}
			it.remove();
		}
		s = t + " avoided accorgimento in step #" + k;
		System.out.println(s);
		sb.append(s);

		totalAccorgimento += t;
		frequentItemsTable = newMap;
		tuplesAvoided += tempAcc;
		tuplesGenerated += frequentItemsTable.size();

		s = tempAcc + " avoided PCY in step #" + k;
		System.out.println(s);
		sb.append(s);

		s = frequentItemsTable.size() + " of size " + k + " have been generated from " + currentItems.size() + " items";
		sb.append(s + "\n");
		System.out.println(s);
	}

	public static void main(String[] args) throws Exception {

		PCY pcy = new PCY("kosarak.dat", (double) 0.02, 0.9, Classification.TRANSACTIONS);
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
		double delta = minimumSupport / 10;
		if (currentItems.size() > 120) {
			minimumSupport += delta;
			System.out.println("new minimum support = " + minimumSupport * 100);
		} else if (currentItems.size() < 100 && k > 5) {
			if (flag)
				minimumSupport -= delta;
			else
				minimumSupport += delta;
			System.out.println("new minimum support = " + minimumSupport * 100);
		}
		timeStep=System.currentTimeMillis();
		pcyStep(k);
		generateCk(k);
		countOccurrences();
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
}
