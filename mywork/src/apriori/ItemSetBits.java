package apriori;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;

public class ItemSetBits extends BitSet implements ItemSetIF {

	public int max = 0;

	public ItemSetBits() {
		super();
	}

	public ItemSetBits(AssociationRule ar) {
		super();
		addAll(ar.getX());
		addAll(ar.getY());

	}

	private void addAll(ItemSetIF x) {
		ItemSetBits is = (ItemSetBits) x;
		or(is);
	}

	@Override
	public boolean add(Integer x) {
		if (isEmpty())
			max = x;
		else if (x > max)
			max = x;
		set(x);
		return true;
	}

	public ItemSetBits(ItemSetBits is) {
		super();
		is.clone();
	}

	@Override
	public int getMax() {
		return max;
	}

	@Override
	public ItemSetBits clone() {
		return (ItemSetBits) super.clone();
	}

	@Override
	public boolean nullIntersection(ItemSetIF is) {
		return !containsAll(is);
	}

	@Override
	public LinkedList<Integer> getSortedElements() {
		LinkedList<Integer> linkedList = new LinkedList<>();
		for (int i = 0; i < size(); i++)
			if (get(i))
				linkedList.add(i);
		System.out.println(linkedList);
		Collections.sort(linkedList);
		System.out.println(linkedList);
		return linkedList;
	}

	@Override
	public boolean containsAll(ItemSetIF is) {
		BitSet temp = (ItemSetBits) is.clone();
		temp.xor(this);
		return temp.isEmpty();
	}

}
