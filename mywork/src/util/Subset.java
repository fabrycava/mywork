package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import apriori.AssociationRule;
import apriori.ItemSet;

public class Subset {


	
	
	public static <T> Set<Set<T>> powerSet(Set<T> originalSet) {
	    Set<Set<T>> sets = new HashSet<Set<T>>();
	    if (originalSet.isEmpty()) {
	        sets.add(new HashSet<T>());
	        return sets;
	    }
	    List<T> list = new ArrayList<T>(originalSet);
	    T head = list.get(0);
	    Set<T> rest = new HashSet<T>(list.subList(1, list.size())); 
	    for (Set<T> set : powerSet(rest)) {
	        Set<T> newSet = new HashSet<T>();
	        newSet.add(head);
	        newSet.addAll(set);
	        sets.add(newSet);
	        sets.add(set);
	    }       
	    return sets;
	}  
	
	
	
	

	public static void main(String[] args) {
		ItemSet i = new ItemSet();
		i.add(1);
		i.add(2);
		ItemSet s = new ItemSet();
		s.add(3);

		AssociationRule ar = new AssociationRule(i, s);

		HashMap<AssociationRule, Double> ass = new HashMap<>();
		ass.put(ar, 0.0);

		System.out.println(ass);

		Iterator<AssociationRule> it = ass.keySet().iterator();
		while (it.hasNext()) {
			AssociationRule a = it.next();
			ass.put(a, 1.1);
		}

		System.out.println(ass);

	//	System.out.println(Arrays.toString(generateSubsets(new ItemSet(ar))));
	}
}