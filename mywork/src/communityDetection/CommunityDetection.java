package communityDetection;

import java.security.KeyStore.Entry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import associationRule.AssociationRule;
import associationRule.AssociationRuleGenerator;
import enums.ARParameter;
import enums.Order;
import itemset.ItemSet;

public class CommunityDetection {

	// private List<ItemSet> frequentItemset;

	private HashMap<ItemSet, Double> frequentItemset;
	AssociationRuleGenerator arg;

	
	
	

	public CommunityDetection(HashMap<ItemSet, Double> frequentItemset) {
		// this.frequentItemset = new LinkedList<>(frequentItemset.keySet());
		// this.frequentItemset.sort(ItemSet.getComparator(Order.DESCENDING));

		this.frequentItemset = frequentItemset;
		
		arg=new AssociationRuleGenerator(frequentItemset, 0.1, new StringBuilder());
		
		findCommunities();
	}

	public void findCommunities() {
		// List<Map.Entry<ItemSet, Double>> list = new
		// LinkedList<>(frequentItemset.entrySet());
		// list.sort(new ItemSetDoubleComparator());
		// System.out.println(list);

		List<AssociationRule> list = new LinkedList<>(arg.getAssociationRules());
		list.sort(AssociationRule.getComparator(Order.DESCENDING, ARParameter.CONFIDENCE));
		System.out.println(list);

	}

	private class ItemSetDoubleComparator implements Comparator<Map.Entry<ItemSet, Double>> {

		@Override
		public int compare(Map.Entry<ItemSet, Double> o1, Map.Entry<ItemSet, Double> o2) {
			return o1.getValue() < o2.getValue() ? 1 : o1.getValue() > o2.getValue() ? (-1) : 0;
		}
	}

}
