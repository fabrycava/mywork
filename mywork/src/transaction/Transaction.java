package transaction;

import itemset.ItemSetIF;

public interface Transaction {	
	
	@Override
	String toString();
	public boolean add(Integer x);
	boolean containsAll(ItemSetIF is);
	


}
