package apriori;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Transaction{

	private HashSet<Integer> items;
	
	
	public Transaction(String s) {
		items=new HashSet<>();
	}
	
	@Override
	public String toString() {
		return items.toString()+ "\n";
	}
	
	public void addItem(int x) {
		items.add(x);
	}
	
	public HashSet<Integer> getItems(){
		return items;
	}
	
	
	
	public boolean containsItemset(ItemSet is){
		return items.containsAll(is.getElements());
	}

}
