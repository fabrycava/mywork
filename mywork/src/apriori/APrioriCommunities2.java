package apriori;

import java.util.HashMap;
import java.util.Iterator;

import associationRule.AssociationRuleGenerator;
import enums.Classification;
import itemset.ItemSet;
import itemset.ItemSetIF;
import util.SubsetIterator;

public class APrioriCommunities2 extends AbstractAPrioriCommunities {

	public APrioriCommunities2(String fileName, double minimumSupport, int maxK, Classification classification,
			int maxComputableItems) throws Exception {
		super(fileName, minimumSupport, maxK, classification, maxComputableItems);
		// TODO Auto-generated constructor stub
	}

	public APrioriCommunities2(String fileName, double minimumSupport, int maxK, Classification classification)
			throws Exception {
		super(fileName, minimumSupport, maxK, classification);
		// TODO Auto-generated constructor stub
	}

	int total = 0;

	private int tuplesGenerated;

	@Override
	public void compute() {
		super.compute();
		// System.out.println(frequentItemsTable.size());

		// System.out.println(totalAccorgimento + " risparmiate grazie all' accorgimento
		// ;)");
		long elapsedTime = System.currentTimeMillis() - start;

		s = "Elapsed time(s)= " + (double) elapsedTime / 1000
				+ ("\n\nFound " + frequentItemset.size() + " of size " + (k - 2));
		sb.append(s + "\n\n");
		System.out.println(s + "\n");

		pw.print(sb.toString());
		pw.close();
	}

	// generate the candidate tuples
	protected void generateCk(int k) {
		int checked = 0, tuplesPruned = 0, unchecked = 0, checkedPruned = 0;

		s = "generating all the tuples of size " + k;
		sb.append(s + "\n");
		System.out.println(s);
		int t = 0;
		HashMap<ItemSet, Integer> newMap = new HashMap<>();
		Iterator<ItemSet> it = frequentItemsTable.keySet().iterator();
		while (it.hasNext()) {
			ItemSetIF temp = it.next();
			Iterator<Integer> it1 = currentItems.keySet().iterator();
			while (it1.hasNext()) {
				int x = it1.next();
				if (x > temp.getMax()) {// ensure the monotonicity
					checked++;
					ItemSet previous = (ItemSet) temp.clone();
					previous.add(x);

					boolean flag = true;

				
					checkedPruned++;
					if (prune(k, previous, newMap)) {
						tuplesPruned++;
					} // if it's check Z then this is enough
					
					else {// otherwise we have to check also the others
						if (classification == Classification.USOCIALR) {//generate all the subset
							//System.out.println("eccoloooooooooooooo");
							SubsetIterator<Integer> sit = new SubsetIterator<>(previous, k - 1);
							while (sit.hasNext()) {
								if (!frequentItemset.containsKey(sit.next())) {

									flag = false;
									t++;
									break;
								}
							}
							if (flag) {//if all the subsets are ok then count the whole file
								if (pruneR(checkedPruned, previous, newMap))
									tuplesPruned++;
							}
						}
					}
				} else
					unchecked++;
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
		tuplesGenerated += frequentItemsTable.size();
		totalPruned += tuplesPruned;

		// System.out.println("c = " +c+ "\tc1 = "+c1);

		// s = "checked " + checked + " itemsetsof size " + k;
		// sb.append(s + "\n");
		// System.out.println(s);
		//
		// s = "unchecked " + unchecked + " itemsetsof size " + k;
		// sb.append(s + "\n");
		// System.out.println(s);
		//
		// s = t + " avoided accorgimento in step #" + k;
		// System.out.println(s);
		// sb.append(s + "\n");
		//
		// s = "checkedPruned " + checkedPruned + " itemsetsof size " + k;
		// sb.append(s + "\n");
		// System.out.println(s);

		s = "pruned " + tuplesPruned + " itemsetsof size " + k + " and " + itemsRemoved + " elements";
		sb.append(s + "\n");
		System.out.println(s);

		// s = frequentItemsTable.size() + " of size " + k + " have been generated from
		// " + currentItems.size() + " items";
		// sb.append(s + "\n");
		// System.out.println(s);

		s = "Found " + frequentItemsTable.size() + " cliques of size " + k;
//		if (!iterative)
//			s += " (with support " + (minimumSupportDouble * 100) + "%\t(" + minimumSupport + " occurrences)";
//		sb.append(s + "\n");
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

		APriori ap = new APriori("kosarak.dat", (double) 0.02, 0, Classification.TRANSACTIONS);
		ap.compute();
		AssociationRuleGenerator arg = new AssociationRuleGenerator(ap.getFrequentItemset(), 0.9,
				ap.getStringBuilder());
		arg.assocRules();

	}

	@Override
	protected void step(int k) {
		long timeStep = System.currentTimeMillis();
		try {
			generateCk(k);
		} catch (OutOfMemoryError e) {

			System.out.println();
			long elapsedTime = System.currentTimeMillis() - start;

			s = "APriori crashed due to the OutOfMemory!!!!!!!!\nThe result may be highly incorrect\n\n\n"
					+ totalAccorgimento + " risparmiate grazie all' accorgimento ;)\nElapsed time(s)= "
					+ (double) elapsedTime / 1000 + "\n";
			sb.append(s + "\n\n");
			System.out.println(s + "\n");

			s = "Found " + frequentItemset.size() + " frequent itemsets of size " + k + " (with support "
					+ (minimumSupport * 100) + "%)";
			sb.append(s + "\n\n");
			System.out.println(s + "\n");

			pw.print(sb.toString());
			results();
			pw.close();
		}

		s = "Elapsed time for step #" + k + " = " + (System.currentTimeMillis() - timeStep) / 1000 + "\n";
		sb.append(s + "\n");
		System.out.println(s);

	}

}
