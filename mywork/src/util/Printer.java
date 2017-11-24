package util;

import java.io.PrintWriter;

import apriori.APriori;
import apriori.AbstractAPriori;
import apriori.AprioriInterface;

public class Printer {

	AbstractAPriori ap;
	String fileName, folderData;
	PrintWriter pw;

	

	public Printer(AbstractAPriori ap) throws Exception {
		this.ap = ap;
		pw = ap.getPrintWriter();
		this.folderData = ap.getFolderData();
		this.fileName=ap.getFileName();
	}

	

	public void printInputSettings() {
		// System.out.println("Transactions:\n"+ getTransactions());

		StringBuilder sb = new StringBuilder();
		sb.append("Folder:" + folderData + "\n" + "fileName:"+fileName+"\n\n");
		sb.append("Input configuration: \n" + ap.getN() + " items, " + ap.getT() + " transactions, ");
		sb.append("Min Sup = " + ap.getMinimumSupport());
		sb.append(", Min Conf = " + ap.getMinimumConfidence() + "%\n");
		// sb.append("itemsCount" + ap.getItemsCountMap() + "\n");

		pw.write(sb.toString() + "\n");

		System.out.println(sb.toString());
		// System.out.println("Input configuration: \n" + ap.getN() + " items, " +
		// ap.getT() + " transactions, ");
		// System.out.println("Min Sup = " + ap.getMinimumSupport() + "%");
		// System.out.println("Min Conf = " + ap.getMinimumConfidence() + "%");
		// System.out.println("itemsCount" + ap.getItemsCountMap());

	}
}
