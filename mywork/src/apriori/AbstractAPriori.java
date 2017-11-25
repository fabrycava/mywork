package apriori;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import enums.Classification;
import itemset.ItemSet;
import itemset.ItemSetIF;
import transaction.Transaction;
import util.Printer;
import util.Reader;

public abstract class AbstractAPriori implements AprioriInterface {

	protected String fileName, folderData = "Datasets\\", folderResults = "Results\\";;
	protected int N, T;

	protected double minimumSupport;
	protected double minimumConfidence;
	// protected Printer printer;

	protected List<Transaction> transactions;

	protected HashMap<Integer, Boolean> currentItems;

	protected HashMap<ItemSet, Integer> frequentItemsTable;

	protected HashMap<ItemSet, Double> frequentItemset;
	protected StringBuilder sb;

	
	protected PrintWriter pw;
	protected long start;

	public AbstractAPriori(String fileName, double minimumSupport, double minimumConfidence,
			Classification classification) throws Exception {
		start = System.currentTimeMillis();
		this.fileName = fileName;
		// frequentItemset = new HashMap<>();

		if (minimumConfidence > 1 || minimumConfidence < 0)
			throw new IllegalArgumentException("confidence must be expressed between 0 and 1 (included)");
		else
			this.minimumConfidence = minimumConfidence;

		if (minimumSupport > 1 || minimumSupport < 0)
			throw new IllegalArgumentException(
					"support must be expressed with a double value between 0 and 1 (included)");
		else
			this.minimumSupport = minimumSupport;

		pw = new PrintWriter(new File(folderResults + fileName + ".result"));

		sb = new StringBuilder();

		frequentItemsTable = new HashMap<>();
		transactions = new ArrayList<>();
		currentItems = new HashMap();
		frequentItemset=new HashMap<>();

		
	}

	public PrintWriter getPrintWriter() {
		return pw;
	}

	public void transactionsAdd(Transaction t) {
		transactions.add(t);
	}

	public void setN(int x) {
		N = x;
	}

	public void setT(int x) {
		T = x;
	}

	public double getMinimumSupport() {
		return minimumSupport;
	}

	protected double computeSup(int value) {

		double sup = ((double) value / T);

		return sup;
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

	public String getFolderData() {
		return folderData;
	}

}
