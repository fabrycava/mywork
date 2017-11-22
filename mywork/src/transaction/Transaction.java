package transaction;

import itemset.ItemSetIF;

public interface Transaction {

	@Override
	String toString();

	boolean add(Integer x);

	boolean containsAll(ItemSetIF is);

	public int getId();

	@Override
	
	public boolean equals(Object o);

	@Override
	public int hashCode();

}
