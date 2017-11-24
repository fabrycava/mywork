package apriori;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import enums.Classification;
import itemset.ItemSetIF;
import transaction.Transaction;
import util.Printer;
import util.Reader;

public abstract class AbstractAPriori implements AprioriInterface{

	protected String fileName, outputFile, folderData = "Datasets\\", folderResults = "Results\\";;
	protected int N, T;

	protected double minimumSupport;
	protected double minimumConfidence;
//	protected Printer printer;
	
	protected List<Transaction> transactions;

	protected HashMap<ItemSetIF, Integer> frequentItemsTable;

	protected PrintWriter pw;

	
	
	
	
	public AbstractAPriori(String fileName, double minimumSupport, double minimumConfidence, Classification classification)
			throws Exception {
		this.fileName = fileName;
		this.outputFile = outputFile;
		//frequentItemset = new HashMap<>();

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


		frequentItemsTable = new HashMap<>();
		transactions = new ArrayList<>();
//		currentItems = new HashMap();

		//Reader.readTransations(this, classification, folderData);

		// System.out.println(max);
		// if (T != transactions.size())
		// System.err.println("ERRRRRRRR" + T + " " + transactions.size());

		StringBuilder sb = new StringBuilder();
		sb.append("Folder:" + folderData + "\n" + "fileName:"+fileName+"\n\n");
		sb.append("Input configuration: \n" + N + " items, " + T + " transactions, ");
		sb.append("Min Sup = " + (minimumSupport*100)+"%\n");
		sb.append("Min Conf = " + (minimumConfidence*100) + "%\n");

		pw.write(sb.toString() + "\n");

		System.out.println(sb.toString());

//		System.out.println(frequentItemsTable);
		System.out.println(transactions);
//		System.out.println(currentItems);
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
