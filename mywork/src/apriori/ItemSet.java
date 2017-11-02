package apriori;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ItemSet implements Iterable<Integer>, Cloneable {

	private HashSet<Integer> elements;

	private int max = 0;

	public ItemSet() {

		elements = new HashSet<>();
	}

	public ItemSet(AssociationRule ar) {
		elements = new HashSet<>();
		elements.addAll(ar.getI().elements);
		elements.addAll(ar.getS().elements);
	}

	public ItemSet(Set<Integer> set) {
		elements = (HashSet<Integer>) set;
	}

	@Override
	public Iterator<Integer> iterator() {
		return elements.iterator();
	}

	public void add(int x) {
		if (elements.isEmpty())
			max = x;
		else if (x > max)
			max = x;
		elements.add(x);
	}

	public void addItemset(ItemSet is) {

		elements.addAll(is.elements);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;

		if (o == this)
			return true;

		if (o instanceof ItemSet) {
			ItemSet other = (ItemSet) o;
			return this.elements.equals(other.getElements());
		}
		return false;

	}

	public int getMax() {
		return max;
	}

	public int cardinality() {
		return elements.size();
	}

	public HashSet<Integer> getElements() {
		return elements;
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

	@Override
	public int hashCode() {
		// System.out.println("invoking hashcode ");
		int x = 1;
		for (Integer integer : elements) {
			x *= integer;
		}
		return x * 157;
	}

	public ItemSet clone() {
		ItemSet is = new ItemSet();
		for (Integer i : this.elements)
			is.elements.add(i);
		return is;
	}

	public boolean contains(Integer x) {
		return elements.contains(x);
	}

	public boolean nullIntersection(ItemSet is) {
		Iterator<Integer> it = is.elements.iterator();
		while (it.hasNext()) {
			if (elements.contains(it.next()))				return false;

		}
		return true;

	}
}
