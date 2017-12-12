package apriori;

import java.util.HashMap;
import java.util.HashSet;

import associationRule.AssociationRuleGenerator;
import enums.Classification;
import itemset.ItemSet;

public class APrioriCliques extends AbstractAPriori {
	
	protected HashSet<Integer> Items, itemsDeleted;

	protected HashMap<ItemSet, Integer> frequentItemset;
	
	protected int firstSupport;
			
	private int currentSupport;
	
	private boolean recovery=false;

	public APrioriCliques(String fileName, double minimumSupport, int maxK, Classification classification)
			throws Exception {
		super(fileName, minimumSupport, maxK, classification);
		this.Items=(HashSet<Integer>)currentItems.keySet();
		firstSupport=this.minimumSupport*5;
		
	}	
	public APrioriCliques(APrioriCliques ap)
			throws Exception {
		super(ap.fileName,ap.minimumSupport, ap.maxK,ap.classification);
		this.currentItems=ap.currentItems;
		this.frequentItemset=ap.frequentItemset;		
		this.frequentItemsTable=ap.frequentItemset;
		this.transactions=ap.transactions;
	}
	
	@Override
	public void compute() {		
		prune(1);
		
		minimumSupport /= 10;

		try {
			for (k = 2; frequentItemsTable.size() != 0 && k <= maxK; k++) {
				step(k);
			}
		} catch (OutOfMemoryError e) {

			 catchOutOfMemory();
		}
	}
	

	@Override
	protected void step(int k) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void generateCk(int k) throws OutOfMemoryError {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	@Override
	protected int removeUnfrequentCurrentItems() {
		// TODO Auto-generated method stub
		return super.removeUnfrequentCurrentItems();
	}
	
	public static void main(String[] args) throws Exception {
		APrioriCliques ap = new APrioriCliques("kosarak.dat", (double) 0.02, 0, Classification.USOCIAL);
		ap.computeFirst();
		ap.compute();

		
	}

	
}
