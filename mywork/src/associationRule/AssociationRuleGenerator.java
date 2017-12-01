package associationRule;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.apache.commons.math3.util.CombinatoricsUtils;

import apriori.APriori;
import apriori.PCY;
import enums.ARParameter;
import enums.Classification;
import enums.Order;
import itemset.ItemSet;
import util.Subset;
import util.SubsetIterator;

public class AssociationRuleGenerator {

	private HashMap<ItemSet, Double> frequentItemset;
	private double minimumConfidence;
	private HashSet<AssociationRule> assoc, assocOLD;

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
		//generateAssocRulesOld();
		generateAssocRules();
		// System.out.println(assoc);
		computeAssocRules2();		
		 printAssocRules();
		System.out.println("Elapsed time for AR " + (System.currentTimeMillis() - start + "\n"));
		sb.append("Elapsed time for AR " + (System.currentTimeMillis() - start + "\n\n"));

	}

	private void printAssocRules() {
		String s = "Found " + assoc.size() + " Association rules" + " (with confidence " + (minimumConfidence * 100)
				+ "%)\n";
		sb.append(s + "\n");
		System.out.println(s);
		LinkedList<AssociationRule> ass = new LinkedList<>(assoc);
		ass.sort(AssociationRule.getComparator(Order.ASCENDING, ARParameter.SIZE));
		s = ass + "\n";
		System.out.println(s);
		sb.append(s + "\n");
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
		String s = "Generating the Associaton Rules from " + frequentItemset.size() + " itemsets";
		System.out.println(s);
		sb.append(s + "\n");
		assoc = new HashSet();
		Iterator<ItemSet> it = frequentItemset.keySet().iterator();
		while (it.hasNext()) {
			ItemSet curr = it.next();
			int dim = 0;
			for (int i = 1; i <= curr.size(); i++)
				dim += CombinatoricsUtils.binomialCoefficient(curr.size(), i);
			SubsetIterator<Integer> sit = new SubsetIterator<>(curr,1, curr.size());
			ItemSet[] subsets = new ItemSet[dim];
			int c = 0;
			while (sit.hasNext()) {
				subsets[c++] = sit.next();
				//System.out.println(Arrays.toString(subsets));
			}
			//System.out.println(Arrays.toString(subsets));
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

	private void generateAssocRulesOld() {
		String s = "Generating the Associaton Rules from " + frequentItemset.size() + " itemsets";
		System.out.println(s);
		sb.append(s + "\n");
		assoc = new HashSet();
		Iterator<ItemSet> it = frequentItemset.keySet().iterator();
		while (it.hasNext()) {
			ItemSet curr = it.next();
			// int dim = 0;
			// for (int i = 1; i <= curr.size(); i++)
			// dim += CombinatoricsUtils.binomialCoefficient(curr.size(), i);
			// SubsetIterator<Integer> sit = new SubsetIterator<>(curr, curr.size());
			ItemSet[] subsets = Subset.generateSubsets(curr);
			// int c = 0;
			// while (sit.hasNext())
			// subsets[c++] = sit.next();
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

	public static void main(String[] args) throws Exception {
		PCY pcy = new PCY("retail.dat", 0.005, 0.5, Classification.TRANSACTIONS);
		pcy.compute();
//		AssociationRuleGenerator arg = new AssociationRuleGenerator(pcy.getFrequentItemset(),
//				pcy.getMinimumConfidence(), pcy.getStringBuilder());
//		arg.assocRules();

	}

}
