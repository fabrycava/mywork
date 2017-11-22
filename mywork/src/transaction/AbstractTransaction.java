package transaction;

import itemset.ItemSetIF;

public abstract class AbstractTransaction implements Transaction {
	
	protected int id;

	public abstract boolean add(Integer x) ;
	

	public abstract boolean containsAll(ItemSetIF is) ;

	@Override
	public int getId() {
		return id;
	}

	
	@Override
	public boolean equals(Object o) {
		if(o==null)
			return false;
		if(o==this) return true;
		if(! (o instanceof AbstractTransaction))
			return false;
		AbstractTransaction at=(AbstractTransaction) o;
		return at.id==this.id;
	} 
	
	@Override
	public int hashCode() {		
		return id*157;
	}

}
