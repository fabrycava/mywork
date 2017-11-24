package associationRule;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import apriori.APriori;
import enums.ARParameter;
import enums.Order;
import itemset.ItemSet;
import util.Subset;

public class AssociationRuleGenerator {

	private HashMap<ItemSet, Double> frequentItemset;
	private double minimumConfidence;
	private HashSet<AssociationRule> assoc;
	private StringBuilder sb;

	public AssociationRuleGenerator(HashMap<ItemSet, Double> frequentItemset, double minimumConfidence,
			StringBuilder sb) {
		this.frequentItemset = frequentItemset;
		this.minimumConfidence = minimumConfidence;
		this.sb = sb;
	}

	public HashSet<AssociationRule> getAssociationRules() {
		assocRules();
		return assoc;
	}

	public void assocRules() {
		double start = System.currentTimeMillis();
		generateAssocRules();
		// System.out.println(assoc);
		computeAssocRules2();
		//printAssocRules();
		System.out.println("Elapsed time for AR " + (System.currentTimeMillis() - start));
		sb.append("Elapsed time for AR " + (System.currentTimeMillis() - start + "\n"));

	}

	private void printAssocRules() {

		sb.append("Found " + assoc.size() + " Association rules" + " (with confidence " + (minimumConfidence * 100)
				+ "%)\n");
		System.out.println("Found " + assoc.size() + " Association rules" + " (with confidence "
				+ (minimumConfidence * 100) + "%)\n");
		LinkedList<AssociationRule> ass = new LinkedList<>(assoc);
		ass.sort(AssociationRule.getComparator(Order.ASCENDING, ARParameter.SIZE));
		System.out.println(ass + "\n");
		sb.append(ass + "\n\n");

		// sb.append(assoc + "\n\n");
		// System.out.println(assoc + "\n");

	}

	private void computeAssocRules() {
		Iterator<AssociationRule> it = assoc.iterator();
		HashSet<AssociationRule> toDelete = new HashSet();
		while (it.hasNext()) {
			AssociationRule ar = it.next();
			double conf = computeConfidence(ar);
			if (conf < minimumConfidence)
				it.remove();
			else {
				Iterator<AssociationRule> it1 = assoc.iterator();
				while (it1.hasNext()) {
					AssociationRule ar1 = it1.next();
					if (!(toDelete.contains(ar) || toDelete.contains(ar1)))
						if (ar.contains(ar1)) {
							sb.append(ar + "" + ar1);
							System.out.println(ar + "" + ar1);
							toDelete.add(ar1);
						} else if (ar1.contains(ar)) {
							sb.append(ar1 + "" + ar);
							System.out.println(ar1 + "" + ar);
							toDelete.add(ar);
						}
				}
				ar.setConfidence(conf);
			}
		}
		it = toDelete.iterator();
		while (it.hasNext()) {
			AssociationRule ar = it.next();
			assoc.remove(ar);
			// System.out.println("removed " + ar);
			// sb.append("removed " + ar);
		}
	}

	private void computeAssocRules2() {
		Iterator<AssociationRule> it = assoc.iterator();
		System.out.println("#assoc before computing confidence" + " = " + assoc.size());
		HashSet<AssociationRule> toDelete = new HashSet();
		while (it.hasNext()) {
			AssociationRule ar = it.next();
			double conf = computeConfidence(ar);
			ar.setConfidence(conf);
			if (conf < minimumConfidence) {
				// System.out.println("removed " + ar);
				// sb.append("removed " + ar);
				it.remove();
			}
		}

		System.out.println("#assoc after computing confidence" + " = " + assoc.size());

		it = toDelete.iterator();
		while (it.hasNext()) {
			AssociationRule ar = it.next();
			assoc.remove(ar);
			// System.out.println("removed " + ar);
			// sb.append("removed " + ar);
		}

		double start = System.currentTimeMillis();
		LinkedList<AssociationRule> ass = new LinkedList<>(assoc);
		ass.sort(AssociationRule.getComparator(Order.DESCENDING, ARParameter.SIZE));
		// System.out.println(ass);
		System.out.println("tempo trascorso per ordinamento " + (System.currentTimeMillis() - start));

		while (ass.size() != 0) {
			AssociationRule ar = ass.getFirst();
			it = assoc.iterator();
			while (it.hasNext()) {
				AssociationRule curr = it.next();
				if (ar.contains(curr))
					it.remove();
			}
			ass.removeFirst();
		}

	}

	private double computeConfidence(AssociationRule ar) {
		ItemSet XY = new ItemSet(ar);
		double suppXY = frequentItemset.get(XY);
		double suppX = frequentItemset.get(ar.getX());
		return (double) suppXY / suppX;
	}

	private void generateAssocRules() {
		System.out.println("Generating the Associaton Rules from " + frequentItemset.size() + " itemsets");
		sb.append("Generating the Associaton Rules from " + frequentItemset.size() + " itemsets\n");
		assoc = new HashSet();
		Iterator<ItemSet> it = frequentItemset.keySet().iterator();
		while (it.hasNext()) {
			ItemSet curr = it.next();
			ItemSet[] subsets = generateSubsets(curr);
			for (int i = 0; i < subsets.length; i++) {
				for (int j = 0; j < subsets.length; j++) {
					if (i != j && subsets[i].size() != 0 && subsets[j].size() != 0
							&& subsets[i].nullIntersection(subsets[j])) {
						subsets[i].searchMax();
						subsets[j].searchMax();

						AssociationRule ar = new AssociationRule(subsets[i], subsets[j]);
						ar.getX().searchMax();
						ar.getY().searchMax();

						assoc.add(ar);
					}
				}
			}
		}
		System.out.println(assoc.size() + " candidate AR have been generated");
		sb.append(assoc.size() + " candidate AR have been generated\n");
	}

	private ItemSet[] generateSubsets(ItemSet curr) {
		Set<Set<Integer>> subsets = Subset.powerSet(curr);
		// System.out.println(subsets);
		ItemSet[] subsetsArray = new ItemSet[subsets.size()];
		Iterator<Set<Integer>> it = subsets.iterator();
		int i = 0;
		while (it.hasNext())
			subsetsArray[i++] = new ItemSet(it.next());
		return subsetsArray;

	}
}
