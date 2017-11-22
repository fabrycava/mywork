package transaction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.views.AbstractView;

import itemset.ItemSet;
import itemset.ItemSetIF;

public class TransactionSet extends AbstractTransaction {

	private HashSet<Integer> transaction;

	public TransactionSet(int id) {
		super();
		transaction = new HashSet<>();
		this.id=id;
	}

	public boolean containsAll(ItemSetIF is) {
		return transaction.containsAll((ItemSet) is);
	}

	@Override
	public boolean add(Integer x) {
		return transaction.add(x);
	}
	
	
	@Override
	public String toString() {
		return id+" "+transaction.toString()+"\n";
	}

	
	public static void main(String[] args) {
		TransactionSet t=new TransactionSet(2);
		t.add(1);
		ArrayList<Transaction> al=new ArrayList<>();
		al.add(t);
		System.out.println(al.contains(new TransactionSet(2)));
	}
	
}
