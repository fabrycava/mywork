package communityDetection;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import apriori.APriori;
import apriori.AprioriInterface;
import apriori.PCY;
import associationRule.AssociationRule;
import associationRule.AssociationRuleGenerator;
import enums.ARParameter;
import enums.Classification;
import enums.Order;
import itemset.ItemSet;

public class CommunityDetection {

	// private List<ItemSet> frequentItemset;

	private HashMap<ItemSet, Double> frequentItemset;
	AssociationRuleGenerator arg;
	private HashMap<Integer, Integer> elementCommunity;
	private HashMap<Integer, ItemSet> communities;

	public CommunityDetection(HashMap<ItemSet, Double> frequentItemset) {
		// this.frequentItemset = new LinkedList<>(frequentItemset.keySet());
		// this.frequentItemset.sort(ItemSet.getComparator(Order.DESCENDING));

		this.frequentItemset = frequentItemset;
		elementCommunity = new HashMap<>();
		// arg = new AssociationRuleGenerator(frequentItemset, 0.1, new
		// StringBuilder());

	}

	public static void main(String[] args) throws Exception {
		// APriori ap = new APriori("facebook.dat", (double) 0.01, 0.99,
		// Classification.USOCIAL);
		// ap.compute();
		PCY pcy = new PCY("facebook.dat", (double) 0.03, 0.9, Classification.USOCIAL);
		pcy.compute();
		CommunityDetection cm = new CommunityDetection(pcy.getFrequentItemset());
		System.out.println("k = " + pcy.getK());
		// cm.cleanFrequentItemset(pcy.getK());
		System.out.println("Found " + cm.getFrequentItemset().size() + " of size " + (pcy.getK() - 2));
		System.out.println(cm.getFrequentItemset());
		// cm.findCommunities();
		//
		// APriori ap = new APriori("facebook.dat", (double) 0.02, 0.9,
		// Classification.USOCIAL);
		// ap.compute();
		// cm = new CommunityDetection(ap.getFrequentItemset());
		// cm.cleanFrequentItemset(ap.getK());
		// System.out.println("Found " + cm.getFrequentItemset().size() + " of size " +
		// (pcy.getK() - 2));
		// System.out.println(cm.getFrequentItemset());

		// ap = null;
		// cm.findCommunities();

	}

	public HashMap<ItemSet, Double> getFrequentItemset() {
		return frequentItemset;
	}

	// Clean the frequent Items, leaving only the last iteration, the biggest
	private void cleanFrequentItemset(int k) {
		int max = 0;
		Iterator<ItemSet> it = frequentItemset.keySet().iterator();
		while (it.hasNext()) {

			ItemSet is = it.next();
			int size = is.size();
			max = Math.max(max, size);
			if (!(size == k - 2))
				it.remove();
		}
		System.out.println("max = " + max);
	}

	public void findCommunities() {

		// System.out.println(frequentItemset + "\n");

		List<AssociationRule> list = new LinkedList<>(arg.getAssociationRules());
		list.sort(AssociationRule.getComparator(Order.DESCENDING, ARParameter.CONFIDENCE));
		// System.out.println(list);
		Iterator<AssociationRule> it = list.listIterator();
		while (it.hasNext()) {
			AssociationRule ar = it.next();
			ItemSet x = (ItemSet) ar.getX();
			ItemSet y = (ItemSet) ar.getY();
			int max = y.getMax();
			for (Integer i : x) {
				if (!elementCommunity.containsKey(i)) {
					// System.out.println(ar);
					elementCommunity.put(i, max);
					for (Integer j : y)
						if (!elementCommunity.containsKey(j))
							elementCommunity.put(j, max);

				}
			}
		}

		// System.out.println(elementCommunity);
		printCommunities();

	}

	private void printCommunities() {
		Iterator<Map.Entry<Integer, Integer>> it = elementCommunity.entrySet().iterator();
		communities = new HashMap<>();
		while (it.hasNext()) {
			Map.Entry<Integer, Integer> pair = it.next();
			if (!communities.containsKey(pair.getValue())) {
				communities.put(pair.getValue(), new ItemSet());
			}
			communities.get(pair.getValue()).add(pair.getKey());

		}

		System.out.println("Found " + communities.size() + " communities");
		for (ItemSet i : communities.values()) {
			System.out.println(i);
		}
	}

	private class ItemSetDoubleComparator implements Comparator<Map.Entry<ItemSet, Double>> {

		@Override
		public int compare(Map.Entry<ItemSet, Double> o1, Map.Entry<ItemSet, Double> o2) {
			return o1.getValue() < o2.getValue() ? 1 : o1.getValue() > o2.getValue() ? (-1) : 0;
		}
	}

}
