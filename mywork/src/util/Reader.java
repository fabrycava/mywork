package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import apriori.APriori;
import enums.Classification;
import itemset.ItemSet;
import transaction.Transaction;
import transaction.TransactionSet;

public class Reader {

	private static void readFromBipartiteGraphBI(APriori ap, String folderData) throws Exception {

		int N = 0;
		int T = 0;
		int max = 0;

		BufferedReader br = new BufferedReader(new FileReader(new File(folderData+ ap.getFileName())));
		int currBasket = -1;
		Transaction t = null;
		boolean flag=false;
		br.readLine();
		br.readLine();
		while (br.ready()) {

			String s = br.readLine();

			StringTokenizer st = new StringTokenizer(s, " ,\t");
			if (st.countTokens() != 2) {
				System.out.println("Error. Graph is not Bipartite");
				System.exit(-1);
			}

			int basket = Integer.parseInt(st.nextToken());
			if(basket==4)
				System.err.println("aaaaaaaaaaaaaaa");
			if (currBasket != basket) {
				if (flag) {
					T++;
					ap.transactionsAdd(t);
				}
				currBasket = basket;
				t = new TransactionSet();
				flag=true;
			}
			int item = Integer.parseInt(st.nextToken());
			ItemSet unary = new ItemSet();
			unary.add(item);
			t.add(item);
			ap.currentItemsPut(item, false);
			if (item > max)
				max = item;
			if (!ap.fITContains(unary)) {
				ap.fITPut(unary, 1);
				N++;
			} else {
				int old = ap.fITGet(unary);
				ap.fITPut(unary, ++old);
			}
		}
		ap.transactionsAdd(t);
		br.close();

		ap.setN(N);
		ap.setT(T);
		ap.setMax(max);

		PrintWriter pw = new PrintWriter(new File(folderData+"conversion"));
		for (Transaction trans : ap.getTransactions())
			pw.print(trans);
			
		pw.close();

	}

	private static void readFromBaskets(APriori ap, String folderData) throws Exception {
		int N = 0;
		int T = 0;
		int max = 0;

		BufferedReader br = new BufferedReader(new FileReader(new File(folderData+ap.getFileName())));

		while (br.ready()) {
			String s = br.readLine();
			T++;
			Transaction t = new TransactionSet();
			StringTokenizer st = new StringTokenizer(s, " [],\t");
			while (st.hasMoreTokens()) {
				int x = Integer.parseInt(st.nextToken());
				ItemSet unary = new ItemSet();
				unary.add(x);
				t.add(x);
				ap.currentItemsPut(x, false);
				if (x > max)
					max = x;
				if (!ap.fITContains(unary)) {
					ap.fITPut(unary, 1);
					N++;
				} else {
					int old = ap.fITGet(unary);
					ap.fITPut(unary, ++old);
				}
			}
			ap.transactionsAdd(t);
		}
		br.close();

		ap.setN(N);
		ap.setT(T);
		ap.setMax(max);
	}

	public static void readTransations(APriori ap, Classification classification, String folderData) throws Exception {
		if (classification == Classification.BIPARTITEbi)
			readFromBipartiteGraphBI(ap,folderData);
		else if (classification == Classification.BIPARTITEib)
			readFromBipartiteGraphIB(ap,folderData);
		else if (classification == Classification.TRANSACTIONS)
			readFromBaskets(ap,folderData);
	}

	private static void readFromBipartiteGraphIB(APriori ap, String folderData) throws Exception {

		int N = 0;
		int T = 0;
		int max = 0;

		BufferedReader br = new BufferedReader(new FileReader(new File(folderData+ap.getFileName())));

		while (br.ready()) {
			String s = br.readLine();
			T++;
			Transaction t = new TransactionSet();
			StringTokenizer st = new StringTokenizer(s, " ");
			while (st.hasMoreTokens()) {
				int x = Integer.parseInt(st.nextToken());
				ItemSet unary = new ItemSet();
				unary.add(x);
				t.add(x);
				ap.currentItemsPut(x, false);
				if (x > max)
					max = x;
				if (!ap.fITContains(unary)) {
					ap.fITPut(unary, 1);
					N++;
				} else {
					int old = ap.fITGet(unary);
					ap.fITPut(unary, ++old);
				}
			}
			ap.transactionsAdd(t);
		}
		br.close();

		ap.setN(N);
		ap.setT(T);
		ap.setMax(max);

	}

}
