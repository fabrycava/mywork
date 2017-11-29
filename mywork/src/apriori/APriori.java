package apriori;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import associationRule.AssociationRuleGenerator;
import enums.Classification;
import itemset.ItemSet;
import itemset.ItemSetIF;
import transaction.Transaction;
import util.Reader;

public class APriori extends AbstractAPriori {

	// protected HashMap<ItemSet, Integer> frequentItemsTable;

	public APriori(String fileName, double minimumSupport, double minimumConfidence, Classification classification)
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
		for (int k = 2; frequentItemsTable.size() != 0; k++) {
			long timeStep = System.currentTimeMillis();

			generateCk(k);
			countOccurrences();
			prune(k);
			s = "Elapsed time for step #" + k + " = " + (System.currentTimeMillis() - timeStep) / 100;
			sb.append(s + "\n");
			System.out.println(s);

		}

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
					//if (!newMap.containsKey(previous))
						newMap.put(previous, 0);
				}
			}
			it.remove();
		}
		frequentItemsTable = newMap;

		s = frequentItemsTable.size() + " of size " + k + " have been generated from " + currentItems.size() + " items";
		sb.append(s + "\n");
		System.out.println(s);
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

}
