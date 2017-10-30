package apriori;

import java.util.HashMap;

public class FrequentItemsTable implements Cloneable{
	
	private HashMap<ItemSet, Integer> table;
	
	public FrequentItemsTable(){
		table=new HashMap<>();
	}
	
	public HashMap<ItemSet, Integer> getTable(){
		return table;
	}
	
	public int size(){
		return table.size();		
	}
	
	


}
