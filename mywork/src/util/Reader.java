package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;

import apriori.APriori;
import apriori.AbstractAPriori;
import apriori.AbstractAPrioriCommunities;
import enums.Classification;
import itemset.ItemSet;
import transaction.Transaction;
import transaction.TransactionBits;
import transaction.TransactionSet;

public class Reader {

	private static void readFromBipartiteGraphBI(AbstractAPriori ap, String folderData) throws Exception {

		int N = 0;
		int T = 0;

		BufferedReader br = new BufferedReader(new FileReader(new File(folderData + ap.getFileName())));
		int currBasket = -1;
		Transaction t = null;
		boolean flag = false;
		br.readLine();
		br.readLine();
		int id = 0;
		while (br.ready()) {

			String s = br.readLine();

			StringTokenizer st = new StringTokenizer(s, " ,\t");
			if (st.countTokens() != 2) {
				System.out.println("Error. Graph is not Bipartite");
				System.exit(-1);
			}

			int basket = Integer.parseInt(st.nextToken());
			if (basket == 4)

				System.err.println("aaaaaaaaaaaaaaa");
			if (currBasket != basket) {
				if (flag) {
					T++;
					ap.transactionsAdd(t);
				}
				currBasket = basket;
				t = new TransactionSet(id++);
				flag = true;
			}
			int item = Integer.parseInt(st.nextToken());
			ItemSet unary = new ItemSet();
			unary.add(item);
			t.add(item);
			ap.currentItemsPut(item, false);

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

		PrintWriter pw = new PrintWriter(new File(folderData + "conversion"));
		for (Transaction trans : ap.getTransactions())
			pw.print(trans);

		pw.close();

	}

	private static void readFromBaskets(AbstractAPriori ap, String folderData) throws Exception {
		int N = 0;
		int T = 0;
		int id = 0;
		BufferedReader br = new BufferedReader(new FileReader(new File(folderData + ap.getFileName())));

		while (br.ready()) {
			String s = br.readLine();
			T++;
			Transaction t = new TransactionSet(id++);
			StringTokenizer st = new StringTokenizer(s, " [],\t");
			while (st.hasMoreTokens()) {
				int x = Integer.parseInt(st.nextToken());
				ItemSet unary = new ItemSet();
				unary.add(x);
				t.add(x);
				ap.currentItemsPut(x, false);

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
	}

	public static void readTransations(AbstractAPriori ap, Classification classification, String folderData)
			throws Exception {
		switch (classification) {
		case BIPARTITEbi:
			readFromBipartiteGraphBI(ap, folderData);
			break;
		case BIPARTITEib:
			readFromBipartiteGraphIB(ap, folderData);
			break;
		case TRANSACTIONS:
			readFromBaskets(ap, folderData);
			break;
		case USOCIAL:
			readUndirectedToBipartiteGraph( ap, folderData);
			break;
		case USOCIALZ:
			readUndirectedToBipartiteGraph( ap, folderData);
			break;
		case USOCIALR:
			readUndirectedToBipartiteGraph( ap, folderData);
			break;
		}

		// else if (classification == Classification.BIPARTITEib)
		// readFromBipartiteGraphIB(ap, folderData);
		// else if (classification == Classification.TRANSACTIONS)
		// readFromBaskets(ap, folderData);
	}

	private static void readUndirectedToBipartiteGraph(AbstractAPriori ap, String folderData)
			throws Exception {
		// ap.setCD();
		boolean flagZero = false;
		int N = 0;
		int T = 0;
		Transaction t;
		int max = 0;

		BufferedReader br = new BufferedReader(new FileReader(new File(folderData + ap.getFileName())));

		while (br.ready()) {

			String s = br.readLine();

			StringTokenizer st = new StringTokenizer(s, " \t,[]");

			int x = Integer.parseInt(st.nextToken());
			int y = Integer.parseInt(st.nextToken());
			max = Math.max(x, max);
			max = Math.max(max, y);
			if (x == 0 || y == 0)
				flagZero = true;
			ItemSet unaryX = new ItemSet();
			ItemSet unaryY = new ItemSet();
			unaryX.add(x);
			unaryY.add(y);

			// addItemX
			ap.currentItemsPut(x, false);
			if (!ap.fITContains(unaryX)) {
				ap.fITPut(unaryX, 2);
				N++;
				T++;
			} else {
				int old = ap.fITGet(unaryX);
				ap.fITPut(unaryX, ++old);
			}

			// add itemY
			ap.currentItemsPut(y, false);
			if (!ap.fITContains(unaryY)) {
				ap.fITPut(unaryY, 2);
				N++;
				T++;
			} else {
				int old = ap.fITGet(unaryY);
				ap.fITPut(unaryY, ++old);
			}

			// addTransactionX
			int index = ap.getTransactions().indexOf(new TransactionSet(x));
			if (index == -1) {
				t = new TransactionSet(x);
				t.add(x);
				t.add(y);
				ap.transactionsAdd(t);
			} else
				ap.getTransactions().get(index).add(y);

			// addTransactionY
			index = ap.getTransactions().indexOf(new TransactionSet(y));
			if (index == -1) {
				t = new TransactionSet(y);
				t.add(x);
				t.add(y);
				ap.transactionsAdd(t);
			} else
				ap.getTransactions().get(index).add(x);

		}
		br.close();

		ap.setMaxItem(max);
		ap.setN(N);
		ap.setT(T);
		System.out.println("TTTTTTTTTT=" +T);

	}

	private static void readFromBipartiteGraphIB(AbstractAPriori ap, String folderData) throws Exception {

		int N = 0;
		int T = 0;
		int id = 0;

		BufferedReader br = new BufferedReader(new FileReader(new File(folderData + ap.getFileName())));

		while (br.ready()) {
			String s = br.readLine();
			T++;
			Transaction t = new TransactionSet(id++);
			StringTokenizer st = new StringTokenizer(s, " ");
			while (st.hasMoreTokens()) {
				int x = Integer.parseInt(st.nextToken());
				ItemSet unary = new ItemSet();
				unary.add(x);
				t.add(x);
				ap.currentItemsPut(x, false);

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

	}

	public static void main(String[] args) {
		HashSet<Integer> tr = new HashSet<>();
		tr.add(1);
		tr.add(2);
		tr.add(3);

		Iterator<Integer> it = tr.iterator();
		while (it.hasNext()) {
			it.next();
		}

		System.out.println(tr);
	}

}
