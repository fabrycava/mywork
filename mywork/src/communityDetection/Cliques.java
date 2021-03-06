package communityDetection;

import java.util.Set;

import apriori.APriori2;
import apriori.APrioriCommunities;
import apriori.APrioriCommunities2;
import enums.Classification;
import itemset.ItemSet;
import util.GraphGenerator;

public class Cliques {

	public static Set<ItemSet> listTriangles(String fileName) throws Exception {
		Set<ItemSet> triangles = listCliques(fileName, 3, 2);
		return triangles;

	}

	public static Set<ItemSet> listCliquesMin(String fileName, int minimumNeighbours, int maxK, int maxComputableItems)
			throws Exception {
		// APriori2 ap2 = new APriori2(fileName, Double.valueOf(minimumNeighbours),
		// maxK, Classification.USOCIAL);
		// ap2.compute();
		// Set<ItemSet> cliques = ap2.getFrequentItemset().keySet();

		APrioriCommunities apc = new APrioriCommunities(fileName, Double.valueOf(minimumNeighbours), maxK,
				Classification.USOCIAL, maxComputableItems);
		apc.compute();
		Set<ItemSet> cliques = apc.getFrequentItemset().keySet();

//		System.out.println("Found " + cliques.size() + " of size " + maxK + " with at least " + minimumNeighbours
//				+ " neighbours in common");
		return cliques;
	}

	
	public static Set<ItemSet> listCliquesZ(String fileName, int minimumNeighbours, int maxK, int maxComputableItems)
			throws Exception {
		// APriori2 ap2 = new APriori2(fileName, Double.valueOf(minimumNeighbours),
		// maxK, Classification.USOCIAL);
		// ap2.compute();
		// Set<ItemSet> cliques = ap2.getFrequentItemset().keySet();

		APrioriCommunities2 apc = new APrioriCommunities2(fileName, Double.valueOf(minimumNeighbours), maxK,
				Classification.USOCIALZ, maxComputableItems);
		apc.compute();
		Set<ItemSet> cliques = apc.getFrequentItemset().keySet();

//		System.out.println("Found " + cliques.size() + " of size " + maxK + " with at least " + minimumNeighbours
//				+ " neighbours in common");
		return cliques;
	}
	
	public static Set<ItemSet> listCliquesR(String fileName, double minimumNeighbours, int maxK, int maxComputableItems)
			throws Exception {
		// APriori2 ap2 = new APriori2(fileName, Double.valueOf(minimumNeighbours),
		// maxK, Classification.USOCIAL);
		// ap2.compute();
		// Set<ItemSet> cliques = ap2.getFrequentItemset().keySet();

		APrioriCommunities2 apc = new APrioriCommunities2(fileName, Double.valueOf(minimumNeighbours), maxK,
				Classification.USOCIALR, maxComputableItems);
		apc.compute();
		Set<ItemSet> cliques = apc.getFrequentItemset().keySet();

//		System.out.println("Found " + cliques.size() + " of size " + maxK + " with at least " + minimumNeighbours
//				+ " neighbours in common");
		return cliques;
	}
	
	public static Set<ItemSet> listCliques(String fileName, int minimumNeighbours, int maxK) throws Exception {
		// APriori2 ap2 = new APriori2(fileName, Double.valueOf(minimumNeighbours),
		// maxK, Classification.USOCIAL);
		// ap2.compute();
		// Set<ItemSet> cliques = ap2.getFrequentItemset().keySet();

		APrioriCommunities apc = new APrioriCommunities(fileName, Double.valueOf(minimumNeighbours), maxK,
				Classification.USOCIAL);
		apc.compute();
		Set<ItemSet> cliques = apc.getFrequentItemset().keySet();

		System.out.println("Found " + cliques.size() + " of size " + maxK + " with at least " + minimumNeighbours
				+ " neighbours in common");
		return cliques;
	}
	
	
	public static Set<ItemSet> listCliques2(String fileName, int minimumNeighbours, int maxK) throws Exception {
		APriori2 ap = new APriori2(fileName, Double.valueOf(minimumNeighbours), Integer.MAX_VALUE, Classification.USOCIALR);
		ap.compute();
		Set<ItemSet> cliques = ap.getFrequentItemset().keySet();

		System.out.println("Found " + cliques.size() + " of size " +ap.getK());
		
		return cliques;
	}
	

	public static void main(String[] args) throws Exception {
		// Set<ItemSet> triangles = ListTriangles.listTriangles("facebook.dat");
		// System.out.println(triangles);

		int nodes = 8;
		double density = 1;
		int communities = 1000;
		double noise = 0.01;
		double noise2=0.025;
		double noise3=0.05;
		double noise4=0.1;
		int minNeighbours = nodes+1;
		String name = "N" + nodes + "C" + communities + "D" + density +"N"+noise;

		// GraphGenerator.generateRandom(nodes, density, "prova");

		 //GraphGenerator.generateHub(nodes, communities,density,noise,name);

		// Set<ItemSet> triangles = Cliques.listTriangles("..\\graphs\\" + "prova");

		// Set<ItemSet> cliques = Cliques.listCliques("..\\graphs\\" + name,
		// minNeighbours, 50);

		// System.out.println(triangles);

		int ms = 1000;
		int sec = 60;
		int min = ms * sec;
		int sleep = 240 * min;
//		System.out.println("sleeping for " + sleep + " min");
//		 Thread.sleep(sleep);
//		System.out.println("JUST WOKE UP. LET'S START ;)");
			

//		for (int i = 10; i <= 100; i += 10) {
//			Cliques.listCliquesMin("..\\Datasets\\" + "facebook.dat", 40, 100, i);
//			System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n");
//		}

		
		//Cliques.listCliquesMin("..\\graphs\\"+name+".dat", minNeighbours, Integer.MAX_VALUE, nodes*communities);
		//System.out.println(Cliques.listCliques2("..\\Datasets\\toy.dat", minNeighbours, Integer.MAX_VALUE));
		

		//Cliques.listCliquesZ("..\\graphs\\"+name+".dat", minNeighbours, Integer.MAX_VALUE, nodes*communities);
		//Cliques.listCliquesR("..\\graphs\\"+name+".dat", minNeighbours, Integer.MAX_VALUE, nodes*communities);
	
		GraphGenerator.generateHub(nodes, communities,density,noise,name);

		Cliques.listCliquesR("..\\graphs\\"+name+".dat", minNeighbours, Integer.MAX_VALUE, nodes*communities);

		GraphGenerator.generateHub(nodes, communities,density,noise2,name);

		Cliques.listCliquesR("..\\graphs\\"+name+".dat", minNeighbours, Integer.MAX_VALUE, nodes*communities);
		
		GraphGenerator.generateHub(nodes, communities,density,noise3,name);

		Cliques.listCliquesR("..\\graphs\\"+name+".dat", minNeighbours, Integer.MAX_VALUE, nodes*communities);
		
//		GraphGenerator.generateHub(nodes, communities,density,noise4,name);
//
//		Cliques.listCliquesR("..\\graphs\\"+name+".dat", minNeighbours, Integer.MAX_VALUE, nodes*communities);
//		
		//Cliques.listCliques2("..\\graphs\\"+name+".dat", minNeighbours, Integer.MAX_VALUE);
		
		
		

		//Cliques.listCliquesR("..\\graphs\\"+name+".dat", 0.004, Integer.MAX_VALUE, nodes*communities);

		
		// Set<ItemSet> cliques3 = Cliques.listCliquesMin("..\\Datasets\\" +
		// "facebook.dat", 40, 100, 10);
		//
		// Set<ItemSet> cliques = Cliques.listCliquesMin("..\\Datasets\\" +
		// "facebook.dat", 40, 100, 50);

		//
		System.out.println("\n\n\n");
		// Set<ItemSet> cliques3 = Cliques.listCliquesMin("..\\Datasets\\" +
		// "facebook.dat", 20, 50, 20);
		// System.out.println("\n\n\n");
		// String name = "N" + nodes + "C" + communities + "D" + density + "N" + noise;
		// GraphGenerator.generateHub(nodes, communities, density, noise, name);
		// Set<ItemSet> cliques1 = Cliques.listCliques("..\\graphs\\" + name + ".dat",
		// 10, 50);
		// System.out.println("\n\n\n");
		//
		// nodes = 30;
		// density = 1;
		// communities = 50;
		//
		// String name2 = "N" + nodes + "C" + communities + "D" + density + "N" + noise;
		// GraphGenerator.generateHub(nodes, communities, density, noise, name2);
		// Set<ItemSet> cliques2 = Cliques.listCliques("..\\graphs\\" + name2 + ".dat",
		// 20, 50);

	}

}
