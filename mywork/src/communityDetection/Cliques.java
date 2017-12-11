package communityDetection;

import java.util.Set;

import org.apache.commons.math3.util.CombinatoricsUtils;

import apriori.APriori;
import apriori.APriori2;
import enums.Classification;
import itemset.ItemSet;
import util.GraphGenerator;

public class Cliques {

	public static Set<ItemSet> listTriangles(String fileName) throws Exception {	
		Set<ItemSet> triangles=listCliques(fileName, 3, 2);		
		return triangles;

	}
	
	public static Set<ItemSet> listCliques(String fileName,int minimumNeighbours, int maxK) throws Exception{
		APriori2 ap2 = new APriori2(fileName, Double.valueOf(minimumNeighbours),maxK, Classification.USOCIAL);
		ap2.compute();
		Set<ItemSet> cliques=ap2.getIngrementedTuple();
		System.out.println("Found " + cliques.size() + " of size " + maxK+" with at least "+minimumNeighbours +" neighbours in common");
		return ap2.getIngrementedTuple();
	}

	public static void main(String[] args) throws Exception {
//		 Set<ItemSet> triangles = ListTriangles.listTriangles("facebook.dat");
//		 System.out.println(triangles);

		int nodes = 150;
		double density = 0.08;
		//GraphGenerator.generateRandom(nodes, density, "prova");
		int communities=50;
		
		int minNeighbours=100;
		
		//GraphGenerator.generateHub(nodes, communities,0.4, 0.007, "prova");
		

		//Set<ItemSet> triangles = Cliques.listTriangles("..\\graphs\\" + "prova");
		
		Set<ItemSet> cliques = Cliques.listCliques("..\\graphs\\" + "prova", 100,100);


		//System.out.println(triangles);

	}

}
