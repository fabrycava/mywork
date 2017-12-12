package util;

import java.io.File;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import itemset.ItemSet;

public class GraphGenerator {

	public static void generateHub(int nodes, int communities, double density, double noise, String output)
			throws Exception {

		PrintWriter pw = new PrintWriter(new File("graphs\\" + output+".dat"));

		String s = "Generating a Hub Graph with\n " + nodes + " nodes * " + communities
				+ " communities\n Community density = " + density + " Graph noise = " + noise;
		//pw.write(s + "\n");
		System.out.println(s);

		ItemSet graph = new ItemSet();

		int total = nodes * communities;

		for (int j = 0; j < communities; j++) {
			ItemSet is = new ItemSet();
			for (int i = (nodes * j); i < nodes * (j + 1); i++) {
				is.add(i);
				graph.add(i);
			}
			SubsetIterator<Integer> sit = new SubsetIterator<>(is, 2);
			while (sit.hasNext()) {
				is = sit.next();
				if (Math.random() < density) {
					String string = is.toString();
					StringTokenizer st = new StringTokenizer(string, " (),\t");
					if (st.countTokens() != 2) {
						s = st.countTokens() + " tokens for the graph generator!!!\t2 expected" + string;
						pw.write(s);
						pw.close();
						throw new RuntimeException(s);
					} else {
						String edge = st.nextToken() + " " + st.nextToken();
						// System.out.println(edge);
						pw.write(edge + "\n");
					}
				}
			}
		}

		// System.out.println("noisssssssse");
		SubsetIterator<Integer> sit = new SubsetIterator<>(graph, 2);
		while (sit.hasNext()) {
			ItemSet is = sit.next();
			if (Math.random() < noise) {
				String string = is.toString();
				StringTokenizer st = new StringTokenizer(string, " (),\t");
				if (st.countTokens() != 2) {
					s = st.countTokens() + " tokens for the graph generator!!!\t2 expected" + string;
					pw.write(s);
					pw.close();
					throw new RuntimeException(s);
				} else {
					String edge = st.nextToken() + " " + st.nextToken();
					// System.out.println(edge);
					pw.write(edge + "\n");
				}
			}
		}
		pw.close();
	}

	public static void generateRandom(int nodes, double dense, String output) throws Exception {

		PrintWriter pw = new PrintWriter(new File("graphs\\" + output+".dat"));

		String s = "Generating a Random Graph with\nnodes = " + nodes + "\nDensity = " + dense + "\n";
		//pw.write(s + "\n");
		System.out.println(s);

		ItemSet graph = new ItemSet();

		for (int i = 0; i < nodes; i++) {

			graph.add(i);
		}

		// PrintWriter pw = new PrintWriter(new File("Datasets\\" +
		// output+"_N"+nodes+"_D"+dense));

		// System.out.println("noisssssssse");
		SubsetIterator<Integer> sit = new SubsetIterator<>(graph, 2);
		while (sit.hasNext()) {
			ItemSet is = sit.next();
			if (Math.random() < dense) {
				String string = is.toString();
				StringTokenizer st = new StringTokenizer(string, " (),\t");
				if (st.countTokens() != 2) {
					s = st.countTokens() + " tokens for the graph generator!!!\t2 expected" + string;
					pw.write(s);
					pw.close();
					throw new RuntimeException(s);
				} else {
					String edge = st.nextToken() + " " + st.nextToken();
					// System.out.println(edge);
					pw.write(edge + "\n");
				}
			}
		}
		pw.close();
	}

	public static void main(String[] args) throws Exception {
		GraphGenerator.generateHub(5, 20, 0.50, 0.005, "prova");

		//
		// APriori2 ap2 = new APriori2("..\\graphs\\prova", 0.01, 10,
		// Classification.USOCIAL);
		// ap2.compute();
		// CommunityDetection cm = new CommunityDetection(ap2.getFrequentItemset());
		// System.out.println("k = " + ap2.getK());
		// System.out.println("Found " + ap2.getFrequentItemset().size() + " of size " +
		// (ap2.getK() - 2));

	}

}
