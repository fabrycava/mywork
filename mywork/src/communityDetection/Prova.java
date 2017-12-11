package communityDetection;

import org.apache.commons.math3.util.CombinatoricsUtils;
import org.jgrapht.graph.*;

public class Prova {

	public static void main(String[] args) {

		double toGB = 1024 * 1024;
		double toMB = 1024;
		
		double total=Runtime.getRuntime().totalMemory()/toGB;
		double free=Runtime.getRuntime().freeMemory()/toGB;
		

		long allocatedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
	
		System.out.println(total+"\n"+free);
		System.out.println(allocatedMemory/toGB);
		
	
	}
}
