package apriori;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Transaction{

	private List<Integer> items;
	
	
	public Transaction(String s) {
		items=new LinkedList<>();
	}
	
	@Override
	public String toString() {
		return items.toString()+ "\n";
	}
	
	public void addItem(int x) {
		items.add(x);
	}
	
	public List<Integer> getItems(){
		return items;
	}

}
