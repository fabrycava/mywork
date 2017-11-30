package itemset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import associationRule.AssociationRule;
import enums.Order;

public class ItemSet extends HashSet<Integer> implements ItemSetIF {

	private int max = -1;

	public ItemSet() {
		super();
	}

	public ItemSet(AssociationRule ar) {
		super();
		super.addAll((Collection<? extends Integer>) ar.getX());
		super.addAll((Collection<? extends Integer>) ar.getY());

	}

	public ItemSet(Set<Integer> set) {
		super(set);
	}

	@Override
	public boolean add(Integer x) {
		if (isEmpty())
			max = x;
		else if (x > max)
			max = x;
		return super.add(x);
	}

	// public void addItemset(ItemSet is) {
	//
	// elements.addAll(is.elements);
	// }

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;

		if (o == this)
			return true;

		if (o instanceof ItemSet) {
			return super.equals(o);
		}
		return false;

	}

	@Override
	public int getMax() {

		return max;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		Iterator<Integer> it = iterator();
		while (it.hasNext()) {
			sb.append(it.next());
			if (it.hasNext())
				sb.append(", ");

		}
		sb.append(")");
		return sb.toString();
	}

//	@Override
//	public int hashCode() {
//		int x = 1;
//		for (Integer integer : this) {
//			x *= integer;
//		}
//		return x ;
//	}

	@Override
	public ItemSet clone() {
		ItemSet is = new ItemSet();
		for (Integer i : this)
			is.add(i);
		return is;
	}

	@Override
	public boolean nullIntersection(ItemSetIF is) {
		Iterator<Integer> it = ((ItemSet) is).iterator();
		while (it.hasNext()) {
			if (this.contains(it.next()))
				return false;
		}
		return true;
	}

	@Override
	public LinkedList<Integer> getSortedElements() {
		LinkedList<Integer> linkedList = new LinkedList<>(this);
		System.out.println(linkedList);
		Collections.sort(linkedList);
		System.out.println(linkedList);
		return linkedList;
	}

	public static Comparator<ItemSet> getComparator(Order order) {
		return order == Order.ASCENDING ? new AscendingItemSetComparator() : new DescendingItemSetComparator();
	}

	private static class DescendingItemSetComparator implements Comparator<ItemSet> {

		@Override
		public int compare(ItemSet i1, ItemSet i2) {
			if (i1.size() > i2.size())
				return -1;
			else if (i1.size() < i2.size())
				return 1;

			return 0;
		};

	}

	private static class AscendingItemSetComparator implements Comparator<ItemSet> {

		@Override
		public int compare(ItemSet i1, ItemSet i2) {
			if (i1.size() < i2.size())
				return -1;
			else if (i1.size() > i2.size())
				return 1;

			return 0;
		};

	}

	public void searchMax() {
		for (Integer x : this) 
			if (x > max) 
				max=x;
			
	}

	public static void main(String[] args) {

		ItemSetIF is = new ItemSet();

		int[] a = { 1, 2, 3, 4, 5, 6, 7, 8 };
		for (int i = a.length - 1; i >= 0; i--)
			is.add(a[i]);

		LinkedList<Integer> l = is.getSortedElements();

		ArrayList<Boolean> al = new ArrayList<Boolean>();
		System.out.println(al);
		al.add(true);
		al.add(false);
		System.out.println(al);
		ItemSet i = new ItemSet();
		System.out.println(i.getMax());
		i.add(1);
		System.out.println(i.getMax());

		ItemSet i2 = new ItemSet();
		i2.add(2);
		i2.add(3);
		System.out.println(i2.getMax());

		AssociationRule ar = new AssociationRule(i, i2);
		System.out.println(ar);
		i = null;
		i2 = null;
		ItemSet s1 = (ItemSet) ar.getX();
		ItemSet s2 = (ItemSet) ar.getY();
		System.out.println(s2.getMax());

	}

}
