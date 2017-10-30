package apriori;

import java.util.HashSet;
import java.util.Iterator;

public class ItemSet implements Iterable<Integer>, Cloneable {
	
	private HashSet<Integer> elements;

	public ItemSet() {
		
		elements = new HashSet<>();
	}

	@Override
	public Iterator<Integer> iterator() {
		return elements.iterator();
	}

	public void add(int x) {
		elements.add(x);
	}

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

	public int cardinality() {
		return elements.size();
	}

	public HashSet<Integer> getElements() {
		return elements;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		Iterator<Integer> it = iterator();
		while (it.hasNext()) {
			sb.append(it.next());
			if (it.hasNext())
				sb.append(", ");

		}
		sb.append("}");
		return sb.toString();
	}

	@Override
	public int hashCode() {
		//System.out.println("invoking hashcode ");
		int x = 1;
		for (Integer integer : elements) {
			x *= integer;
		}
		return x * 157;
	}
	
	
	public ItemSet clone(){
		ItemSet is=new ItemSet();		
		for(Integer i:this.elements)
			is.elements.add(i);
		return is;		
	}
	
	public boolean contains(Integer x){
		return elements.contains(x);
	}
	
}
