package apriori;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.views.AbstractView;

public class TransactionSet extends HashSet<Integer> implements Transaction {

	public TransactionSet() {
		super();
	}

	public boolean containsAll(ItemSetIF is) {
		return super.containsAll((ItemSet) is);
	}

	@Override
	public String toString() {
		return super.toString() + "\n";
	}

}
