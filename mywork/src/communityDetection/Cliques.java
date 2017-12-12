package communityDetection;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.math3.util.CombinatoricsUtils;

import apriori.APriori;
import apriori.APriori2;
import apriori.APrioriCommunities;
import enums.Classification;
import itemset.ItemSet;
import util.GraphGenerator;

public class Cliques {

	public static Set<ItemSet> listTriangles(String fileName) throws Exception {
		Set<ItemSet> triangles = listCliques(fileName, 3, 2);
		return triangles;

	}

	public static Set<ItemSet> listCliquesMin(String fileName, int minimumNeighbours, int maxK,int maxComputableItems) throws Exception {
//		APriori2 ap2 = new APriori2(fileName, Double.valueOf(minimumNeighbours), maxK, Classification.USOCIAL);
//		ap2.compute();
//		Set<ItemSet> cliques = ap2.getFrequentItemset().keySet();
		
		APrioriCommunities apc = new APrioriCommunities(fileName, Double.valueOf(minimumNeighbours), maxK, Classification.USOCIAL,maxComputableItems);
		apc.compute();
		Set<ItemSet> cliques = apc.getFrequentItemset().keySet();
		
		
		System.out.println("Found " + cliques.size() + " of size " + maxK + " with at least " + minimumNeighbours
				+ " neighbours in common");
		return cliques;
	}
	
	public static Set<ItemSet> listCliques(String fileName, int minimumNeighbours, int maxK) throws Exception {
//		APriori2 ap2 = new APriori2(fileName, Double.valueOf(minimumNeighbours), maxK, Classification.USOCIAL);
//		ap2.compute();
//		Set<ItemSet> cliques = ap2.getFrequentItemset().keySet();
		
		APrioriCommunities apc = new APrioriCommunities(fileName, Double.valueOf(minimumNeighbours), maxK, Classification.USOCIAL);
		apc.compute();
		Set<ItemSet> cliques = apc.getFrequentItemset().keySet();
		
		
		System.out.println("Found " + cliques.size() + " of size " + maxK + " with at least " + minimumNeighbours
				+ " neighbours in common");
		return cliques;
	}

	public static void main(String[] args) throws Exception {
		// Set<ItemSet> triangles = ListTriangles.listTriangles("facebook.dat");
		// System.out.println(triangles);

		int nodes = 20;
		double density = 1;
		int communities =50 ;		
		double noise=0.01;
		int minNeighbours = 150;
		//String name = "N" + nodes + "C" + communities + "D" + density +"N"+noise;
		
		// GraphGenerator.generateRandom(nodes, density, "prova");
		
		 //GraphGenerator.generateHub(nodes, communities,density,noise,name);

		// Set<ItemSet> triangles = Cliques.listTriangles("..\\graphs\\" + "prova");

		 
		
	//	Set<ItemSet> cliques = Cliques.listCliques("..\\graphs\\" + name, minNeighbours, 50);

		// System.out.println(triangles);
		
		int ms=1000;
		int sec=60;
		int min=ms*sec;
		Thread.sleep(200*min);
		 
		Set<ItemSet> cliques = Cliques.listCliquesMin("..\\Datasets\\" + "facebook.dat", minNeighbours, 100,100);
		
		
		
		
		String name = "N" + nodes + "C" + communities + "D" + density + "N" + noise;
		GraphGenerator.generateHub(nodes, communities, density, noise, name);
		Set<ItemSet> cliques1 = Cliques.listCliques("..\\graphs\\" + name, minNeighbours, 50);
		
		nodes = 30;
		density = 0.5;
		communities =50 ;
		
		
		String name2 = "N" + nodes + "C" + communities + "D" + density + "N" + noise;
		GraphGenerator.generateHub(nodes, communities, density, noise, name);
		Set<ItemSet> cliques2 = Cliques.listCliques("..\\graphs\\" + name2, minNeighbours, 50);
		


	}

}
