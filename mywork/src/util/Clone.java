package util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import itemset.ItemSet;

public class Clone {

	public static HashMap<ItemSet, Integer> clone(HashMap<ItemSet, Integer> frequentItemsTable) {

		HashMap<ItemSet, Integer> newMap = new HashMap<>();
		Iterator<Entry<ItemSet, Integer>> it = frequentItemsTable.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<ItemSet, Integer> pair = (Map.Entry) it.next();
			newMap.put(pair.getKey().clone(), pair.getValue());

		}
		return newMap;

	}
}
