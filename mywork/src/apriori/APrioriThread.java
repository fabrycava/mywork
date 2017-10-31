package apriori;

import java.util.Iterator;

public class APrioriThread extends APriori implements Runnable {

	public APrioriThread(String fileName, double minimumSupport, double minimumConfidence) throws Exception {
		super(fileName, minimumSupport, minimumConfidence);

	}

	@Override
	public void run() {
		// System.out.println(frequentItemsTable);
		// sb.append("FIT size = " + frequentItemsTable.size() + "\n");
		// System.out.println("FIT size = " + frequentItemsTable.size());

		prune(1);

		sb.append("Found " + frequentItemsTable.size() + " frequent itemsets of size 1" + " (with support "
				+ (minimumSupport * 100) + "%)\n");
		System.out.println("Found " + frequentItemsTable.size() + " frequent itemsets of size 1" + " (with support "
				+ (minimumSupport * 100) + "%)");
		System.out.println(frequentItemsTable);
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

		long elapsedTime = System.currentTimeMillis() - start;

		sb.append("Elapsed time = " + elapsedTime + "\n");
		System.out.println("Elapsed time = " + elapsedTime);

		pw.print(sb.toString());
		pw.close();

	}

}
