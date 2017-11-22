package transaction;

import java.util.BitSet;

import itemset.ItemSetBits;
import itemset.ItemSetIF;

public class TransactionBits extends AbstractTransaction {
	
	BitSet transaction;

	public TransactionBits() {
		super();
		transaction=new BitSet();
	}

	@Override
	public boolean containsAll(ItemSetIF is) {
		BitSet temp=(ItemSetBits)is.clone();
		temp.xor(transaction);
		return temp.isEmpty();		
	}

	@Override
	public String toString() {
		return super.toString() + "\n";
	}

	@Override
	public boolean add(Integer x) {
		transaction.set(x+1);
		return true;
	}

	public static void main(String[] args) {
		BitSet b = new BitSet();
		BitSet b1 = new BitSet();

		b.set(0);
		b.set(1);
		b.set(2);
		b.set(2569);

		b1.set(2569);

		BitSet b2 = (BitSet) b.clone();
		
		
		b.xor(b2);

		
		

		System.out.println(b);
		System.out.println(b1);
		System.out.println(b2);

		b.and(b1);
		System.out.println(b);
		System.out.println(b1);
		System.out.println(b2+"\n");
		
		b.xor(b2);

		b.or(b2);
		System.out.println(b);
		System.out.println(b1);
		System.out.println(b2+"\n");
		
		System.out.println(b.intersects(b2));

		
		
		
	}

}
