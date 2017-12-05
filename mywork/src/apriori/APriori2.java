package apriori;

import java.util.HashMap;
import java.util.Iterator;

import associationRule.AssociationRuleGenerator;
import enums.Classification;
import itemset.ItemSet;
import itemset.ItemSetIF;
import util.Reader;
import util.Subset;
import util.SubsetIterator;

public class APriori2 extends AbstractAPriori {

	int total = 0;
	private int totalAccorgimento = 0;
	private int tuplesGenerated;

	public APriori2(String fileName, double minimumSupport, double minimumConfidence, Classification classification)
			throws Exception {
		super(fileName, minimumSupport, minimumConfidence, classification);
		frequentItemsTable = new HashMap<>();
		Reader.readTransations(this, classification, folderData);
		printInputSettings();
	}

	@Override
	public void compute() {
		super.compute();
		// System.out.println(frequentItemsTable.size());

		System.out.println(total + " risparmiate grazie all' accorgimento ;)");
		long elapsedTime = System.currentTimeMillis() - start;

		s = "Elapsed time(s)= " + (double) elapsedTime / 1000 + "\n";
		sb.append(s + "\n\n");
		System.out.println(s + "\n");

		pw.print(sb.toString());
		pw.close();

	}

	// generate the candidate tuples
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

				}
			}
			it.remove();
		}

		// remove from current items

		frequentItemsTable = newMap;

		int itemsRemoved = removeUnfrequentCurrentItems();

		if (CD && !frequentItemsTable.isEmpty())
			cleanFrequentItemset(k);
		resetCurrentItems();

		totalAccorgimento += t;
		frequentItemsTable = newMap;
		tuplesGenerated += frequentItemsTable.size();

		s = t + " avoided accorgimento in step #" + k;
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

	@Override
	public void results() {
		// TODO Auto-generated method stub

	}

	public static void main(String[] args) throws Exception {

		APriori ap = new APriori("kosarak.dat", (double) 0.02, 0.9, Classification.TRANSACTIONS);
		ap.compute();
		AssociationRuleGenerator arg = new AssociationRuleGenerator(ap.getFrequentItemset(), ap.getMinimumConfidence(),
				ap.getStringBuilder());
		arg.assocRules();

	}

	@Override
	protected void step(int k) {
		long timeStep = System.currentTimeMillis();

		generateCk(k);
		countOccurrences();
		prune(k);
		s = "Elapsed time for step #" + k + " = " + (System.currentTimeMillis() - timeStep) / 100;
		sb.append(s + "\n");
		System.out.println(s);

	}

}
