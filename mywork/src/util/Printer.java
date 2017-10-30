package util;

import java.io.File;
import java.io.PrintWriter;

import apriori.APriori;

public class Printer {

	APriori ap;
	String fileName;
	PrintWriter pw;

	public Printer(APriori ap) throws Exception {
		this.ap = ap;
		

		pw = ap.getPrintWriter();
	}

	public void printInputSettings() {
		// System.out.println("Transactions:\n"+ getTransactions());

		StringBuilder sb = new StringBuilder();
		sb.append("Input configuration: \n" + ap.getN() + " items, " + ap.getT() + " transactions, ");
		sb.append("Min Sup = " + ap.getMinimumSupport() );
		sb.append(", Min Conf = " + ap.getMinimumConfidence() + "%\n");
		//sb.append("itemsCount" + ap.getItemsCountMap() + "\n");

		pw.write(sb.toString()+"\n");
		
		
		
		System.out.println(sb.toString());
//		System.out.println("Input configuration: \n" + ap.getN() + " items, " + ap.getT() + " transactions, ");
//		System.out.println("Min Sup = " + ap.getMinimumSupport() + "%");
//		System.out.println("Min Conf = " + ap.getMinimumConfidence() + "%");
//		System.out.println("itemsCount" + ap.getItemsCountMap());

	}
}
