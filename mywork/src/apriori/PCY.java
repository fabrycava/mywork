package apriori;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.math3.util.CombinatoricsUtils;

import associationRule.AssociationRuleGenerator;
import enums.Classification;
import itemset.ItemSet;
import itemset.ItemSetIF;
import transaction.Transaction;
import util.Reader;

public class PCY extends AbstractAPriori {

	private int[] buckets;

	private BitSet bitmap;

	int tuplesAvoided = 0, tuplesGenerated, mod = 0;

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
		long timeStep;
		for (int k = 2; frequentItemsTable.size() != 0; k++) {
			timeStep = System.currentTimeMillis();
			pcyStep(k);
			generateCk(k);
			countOccurrences();
			prune(k);

			s = "Elapsed time(s) for step #" + k + " = " + (System.currentTimeMillis() - timeStep) / 1000+"\n";
			sb.append(s + "\n");
			System.out.println(s+"");
		}

		long elapsedTime = System.currentTimeMillis() - start;

		s = "A total of " + tuplesAvoided + " tuples generation over " + (tuplesGenerated + tuplesAvoided)
				+ " has been avoided thanks the PCY\n";

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

		if (ind < 0) {
			System.out.println(is);
			System.out.println(is.hashCode());
			System.out.println(ind);
		}

		// System.out.println(hashFunction(is));
		buckets[hashFunction(is)] += value;

	}

	private int hashFunction(ItemSet is) {
		int hash = is.hashCode() % (mod);
		if (hash < 0) {
//			System.out.println("cazzoooooooooooooooooooo");
//			if(is.hashCode()<0)
//				System.out.println("e perche?");
			Iterator<Integer> it = is.iterator();
			int newhash = 1;
			while (it.hasNext()) {
				newhash += it.next();
			}
			//newhash *= 157;
//			if (newhash >= 0)
//				System.out.println("se po fa " + hash + " " + newhash);
//			else
//				System.out.println("eche cavolo ancoraaaaaa" + hash + " " + newhash);
			hash = newhash;
			return hash % mod;

		}

		return hash;
	}

	private void pcyStep(int k) {
		s = "Starting the PCY # " + (k - 1) + "\n";
		sb.append(s + "\n");
		System.out.println(s);

		int coeff = (int) (CombinatoricsUtils.binomialCoefficient(currentItems.size(), k));
		mod = Math.max(((coeff * 3) / 4), 1);
		buckets = new int[mod];
		Iterator<ItemSet> it = frequentItemsTable.keySet().iterator();
		while (it.hasNext()) {
			ItemSetIF temp = it.next();
			Iterator<Integer> it1 = currentItems.keySet().iterator();
			while (it1.hasNext()) {
				int x = it1.next();
				if (x > temp.getMax()) {// ensure the monotonicity
					ItemSet previous = (ItemSet) temp.clone();
					previous.add(x);
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
		for (int i = 0; i < buckets.length; i++) {
			if (computeSup(buckets[i]) >= minimumSupport)
				bitmap.set(i);
		}
		// System.out.println("aaaaaaaaaaaaaaa");

	}

	protected void generateCk(int k) {
		s = "generating all the tuples of size " + k;
		sb.append(s + "\n");
		System.out.println(s);
		int tempAcc = 0;
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

					if (bitmap.get(hashFunction(previous)))
						newMap.put(previous, 0);

					else
						tempAcc++;

				}
			}
			it.remove();
		}
		frequentItemsTable = newMap;
		tuplesAvoided += tempAcc;
		tuplesGenerated += frequentItemsTable.size();

		s = tempAcc + " avoided in step #" + k;
		System.out.println(s);
		sb.append(s);

		s = frequentItemsTable.size() + " of size " + k + " have been generated from " + currentItems.size() + " items";
		sb.append(s + "\n");
		System.out.println(s);
	}

	public static void main(String[] args) throws Exception {

		PCY pcy = new PCY("kosarak.dat", (double) 0.001, 0.9, Classification.TRANSACTIONS);
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
}
