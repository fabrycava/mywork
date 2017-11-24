package itemset;

import java.util.Collection;
import java.util.LinkedList;

public interface ItemSetIF {

	boolean add(Integer x);

	boolean equals(Object o);

	int getMax();

	String toString();

	int hashCode();

	ItemSetIF clone();

	boolean nullIntersection(ItemSetIF is);

	LinkedList<Integer> getSortedElements();
	
	int size();

	//boolean containsAll(ItemSetIF y);

}