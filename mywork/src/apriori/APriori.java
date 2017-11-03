package apriori;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import util.Printer;
import util.Subset;

public class APriori implements AprioriInterface, Cloneable {

	// private List<int[]> itemsets;
	private String fileName, outputFile;
	private int N, T;

	private int max;

	protected double minimumSupport;
	private double minimumConfidence;
	protected HashMap<ItemSet, Integer> frequentItemsTable;
	protected HashMap<Integer, Boolean> currentItems;

	protected HashMap<ItemSet, Double> frequent;
	protected HashSet<AssociationRule> assoc;

	private List<Transaction> transactions;

	protected PrintWriter pw;

	StringBuilder sb = new StringBuilder();

	private Printer printer;

	protected long start = System.currentTimeMillis();

	public APriori(String fileName, double minimumSupport, double minimumConfidence) throws Exception {
		this.fileName = fileName;
		this.outputFile = outputFile;
		frequent = new HashMap<>();

		if (minimumConfidence > 1 || minimumConfidence < 0)
			throw new IllegalArgumentException("confidence must be expressed between 0 and 1 (included)");
		else
			this.minimumConfidence = minimumConfidence;

		if (minimumSupport > 1 || minimumSupport < 0)
			throw new IllegalArgumentException(
					"support must be expressed with a double value between 0 and 1 (included)");
		else
			this.minimumSupport = minimumSupport;

		pw = new PrintWriter(new File(fileName + ".result"));

		printer = new Printer(this);

		readTransactions();

		printer.printInputSettings();

	}

	public PrintWriter getPrintWriter() {
		return pw;
	}

	public double getMinimumSupport() {
		return minimumSupport;
	}

	public double getMinimumConfidence() {
		return minimumConfidence;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public String getFileName() {
		return fileName;
	}

	public int getN() {
		return N;
	}

	public int getT() {

		return T;
	}

	public HashMap<ItemSet, Integer> getItemsCountMap() {
		return frequentItemsTable;

	}

	private void readTransactions() throws Exception {
		N = 0;
		T = 0;
		frequentItemsTable = new HashMap<>();
		BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
		transactions = new ArrayList<>();
		currentItems = new HashMap();
		while (br.ready()) {
			String s = br.readLine();
			T++;
			Transaction t = new Transaction(s);
			StringTokenizer st = new StringTokenizer(s, " ");
			while (st.hasMoreTokens()) {
				int x = Integer.parseInt(st.nextToken());
				ItemSet unary = new ItemSet();
				unary.add(x);
				t.addItem(x);
				currentItems.put(x, false);
				if (x > max)
					max = x;
				if (!frequentItemsTable.containsKey(unary)) {
					frequentItemsTable.put(unary, 1);
					N++;
				} else {
					int old = frequentItemsTable.get(unary);
					frequentItemsTable.put(unary, ++old);
				}
			}
			transactions.add(t);
		}
		br.close();
	}

	@Override
	public void compute() {

		// System.out.println(frequentItemsTable);
		// sb.append("FIT size = " + frequentItemsTable.size() + "\n");
		// System.out.println("FIT size = " + frequentItemsTable.size());

		// System.out.println(frequentItemsTable);
		prune(1);

		sb.append("Found " + frequentItemsTable.size() + " frequent itemsets of size 1" + " (with support "
				+ (minimumSupport * 100) + "%)\n");
		System.out.println("Found " + frequentItemsTable.size() + " frequent itemsets of size 1" + " (with support "
				+ (minimumSupport * 100) + "%)");
		// System.out.println(frequentItemsTable);
		for (int k = 2; frequentItemsTable.size() != 0; k++) {
			sb.append("generating all the tuples of size " + k + "\n");
			System.out.println("generating all the tuples of size " + k);
			generateCk();
			Iterator<ItemSet> it = frequentItemsTable.keySet().iterator();
			// System.out.println(frequentItemsTable);
			sb.append(frequentItemsTable.size() + " of size " + k + " have been generated\n");
			System.out.println(frequentItemsTable.size() + " of size " + k + " have been generated");
			while (it.hasNext()) {
				ItemSet is = it.next();
				countOccurrences(is);
			}

			// System.out.println(frequentItemsTable);

			prune(k);

			sb.append("currentItems size(" + k + ") = " + currentItems.size() + "\n");
			System.out.println("currentItems size(" + k + ") = " + currentItems.size());

		}

		assocRules();

		long elapsedTime = System.currentTimeMillis() - start;

		sb.append("Elapsed time = " + elapsedTime + "\n");
		System.out.println("Elapsed time = " + elapsedTime);

		pw.print(sb.toString());
		pw.close();

	}

	private void assocRules() {
		generateAssocRules();
		// System.out.println(assoc);
		computeAssocRules2();
		printAssocRules();

	}

	private void printAssocRules() {
		sb.append("Found " + assoc.size() + " Association rules" + " (with confidence " + (minimumConfidence * 100)
				+ "%)\n");
		System.out.println("Found " + assoc.size() + " Association rules" + " (with confidence "
				+ (minimumConfidence * 100) + "%)\n");
		// sb.append(assoc);
		// System.out.println(assoc);

	}

	private void computeAssocRules() {
		Iterator<AssociationRule> it = assoc.iterator();
		HashSet<AssociationRule> toDelete = new HashSet();
		while (it.hasNext()) {
			AssociationRule ar = it.next();
			double conf = computeConfidence(ar);
			if (conf < minimumConfidence)
				it.remove();
			else {
				Iterator<AssociationRule> it1 = assoc.iterator();
				while (it1.hasNext()) {
					AssociationRule ar1 = it1.next();
					if (!(toDelete.contains(ar) || toDelete.contains(ar1)))
						if (ar.contains(ar1)) {
							sb.append(ar + "" + ar1);
							System.out.println(ar + "" + ar1);
							toDelete.add(ar1);
						} else if (ar1.contains(ar)) {
							sb.append(ar1 + "" + ar);
							System.out.println(ar1 + "" + ar);
							toDelete.add(ar);
						}
				}
				ar.setConfidence(conf);
			}
		}
		it = toDelete.iterator();
		while (it.hasNext()) {
			AssociationRule ar = it.next();
			assoc.remove(ar);
			System.out.println("removed " + ar);
			sb.append("removed " + ar);
		}
	}

	private void computeAssocRules2() {
		Iterator<AssociationRule> it = assoc.iterator();
		System.out.println("#assoc before computing confidence" + " = " + assoc.size());
		HashSet<AssociationRule> toDelete = new HashSet();
		while (it.hasNext()) {
			AssociationRule ar = it.next();
			double conf = computeConfidence(ar);
			if (conf < minimumConfidence)
				it.remove();
			else {
				ar.setConfidence(conf);
			}
		}

		System.out.println("#assoc after computing confidence" + " = " + assoc.size());

		double start=System.currentTimeMillis();
		LinkedList<AssociationRule> ass = new LinkedList<>(assoc);
		ass.sort(AssociationRule.getComparator());
		System.out.println("tempo trascorso per ordinamento" + (System.currentTimeMillis()-start));


	}

	private double computeConfidence(AssociationRule ar) {
		ItemSet XY = new ItemSet(ar);
		double suppXY = frequent.get(XY);
		double suppX = frequent.get(ar.getI());
		return (double) suppXY / suppX;
	}

	private void generateAssocRules() {
		assoc = new HashSet();
		Iterator<ItemSet> it = frequent.keySet().iterator();
		while (it.hasNext()) {
			ItemSet curr = it.next();
			ItemSet[] subsets = generateSubsets(curr);
			for (int i = 0; i < subsets.length; i++) {
				for (int j = 0; j < subsets.length; j++) {
					if (i != j && subsets[i].cardinality() != 0 && subsets[j].cardinality() != 0
							&& subsets[i].nullIntersection(subsets[j])) {
						AssociationRule ar = new AssociationRule(subsets[i], subsets[j]);
						assoc.add(ar);
					}
				}
			}
		}
	}

	private static ItemSet[] generateSubsets(ItemSet curr) {
		Set<Set<Integer>> subsets = Subset.powerSet(curr.getElements());
		// System.out.println(subsets);
		ItemSet[] subsetsArray = new ItemSet[subsets.size()];
		Iterator<Set<Integer>> it = subsets.iterator();
		int i = 0;
		while (it.hasNext())
			subsetsArray[i++] = new ItemSet(it.next());
		return subsetsArray;

	}

	// reset all the items at false
	protected void resetCurrentItems() {
		HashMap<Integer, Boolean> newCurrentItems = new HashMap<>();
		for (Integer i : currentItems.keySet())
			newCurrentItems.put(i, false);
		currentItems = newCurrentItems;

	}

	// after the counting of the candidate tuples Ck,
	// it prunes all the unfrequent tuples
	protected void prune(int k) {
		Iterator<Entry<ItemSet, Integer>> it = frequentItemsTable.entrySet().iterator();
		LinkedList<ItemSet> toDelete = new LinkedList<>();

		// System.out.println(currentItems);

		// compute the support for all the itemsets in Ck
		while (it.hasNext()) {
			Map.Entry<ItemSet, Integer> pair = (Map.Entry) it.next();
			double sup = computeSup(pair.getValue());
			// System.out.println("sup of " + pair.getKey() + " = " + sup);
			if (sup < minimumSupport)
				toDelete.add(pair.getKey());
			else {// itemset is frequen
				for (Integer i : pair.getKey()) {// if an itemset is frequent then all the items in it are frequent
					currentItems.put(i, true);
				}
				// add the frequent itemset to the frequent so i can count the confidence later.
				if (pair.getKey().cardinality() > 0)
					frequent.put(pair.getKey(), sup);
			}
		}
		int numRemoved = toDelete.size();
		int c = 0;
		for (ItemSet x : toDelete) {
			frequentItemsTable.remove(x);

			// remove from current items
			Iterator<Map.Entry<Integer, Boolean>> itCurr = currentItems.entrySet().iterator();
			while (itCurr.hasNext()) {
				Map.Entry<Integer, Boolean> curr = itCurr.next();
				if (!curr.getValue()) {
					c++;
					itCurr.remove();
					// sb.append(curr.getKey() + " removed\n");
					// System.out.println(curr.getKey() + " removed");
				}
			}
		}

		resetCurrentItems();

		sb.append("pruned " + numRemoved + " itemsets from itemsets of size " + k + " and " + c + " elements\n");

		System.out.println("pruned " + numRemoved + " itemsets from itemsets of size " + k + " and " + c + " elements");

	}

	// count the occurrences of an itemsets in all the transactions and eventually
	// insert the value in the map
	protected void countOccurrences(ItemSet is) {
		int value = frequentItemsTable.get(is);
		for (Transaction t : transactions)
			if (t.containsItemset(is))
				value++;
		frequentItemsTable.put(is, value);

	}

	// generate the candidate tuples
	protected void generateCk() {
		HashMap<ItemSet, Integer> newMap = new HashMap<>();
		Iterator<ItemSet> it = frequentItemsTable.keySet().iterator();
		while (it.hasNext()) {
			ItemSet temp = it.next();
			Iterator<Integer> it1 = currentItems.keySet().iterator();
			while (it1.hasNext()) {
				int x = it1.next();
				if (x > temp.getMax()) {// ensure the monotonicity
					ItemSet previous = temp.clone();
					previous.add(x);
					if (!newMap.containsKey(previous))
						newMap.put(previous, 0);
				}
			}
			it.remove();
		}
		frequentItemsTable = newMap;
	}

	private double computeSup(int value) {

		double sup = ((double) value / T);

		return sup;
	}

	@Override
	public void results() {
		// TODO Auto-generated method stub

	}

	public static void main(String[] args) throws Exception {
		APriori ap = new APriori("chess6.dat", (double) 0.7, 0.8);

		ap.compute();

//		 ItemSet i = new ItemSet();
//		 i.add(1);
//		 i.add(2);
//		 ItemSet s = new ItemSet();
//		 s.add(3);
//		
//		 ItemSet i1 = new ItemSet();
//		 i1.add(1);
//		 ItemSet s1 = new ItemSet();
//		 s1.add(3);
//		 AssociationRule ar = new AssociationRule(i, s);
//		 AssociationRule ar1 = new AssociationRule(i1, s1);
////		 System.out.println(ar);
////		 System.out.println(ar1);
////		 System.out.println(ar1.compareTo(ar));
//		 
//		 ItemSet i3=new ItemSet();
//		 i3.add(1);
//		 i3.add(2);
//		 i3.add(4);
//		 AssociationRule ar2=new AssociationRule(i3, s1);
//		 
//		 LinkedList<AssociationRule> ass=new LinkedList<>();
//		 
//		 ass.add(ar);
//		 ass.add(ar1);
//		 ass.add(ar2);
//		 
//		 System.out.println(ass);
//
//		 ass.sort(AssociationRule.getComparator());
//		 
//		 System.out.println(ass);
		 
		 


	}

}
