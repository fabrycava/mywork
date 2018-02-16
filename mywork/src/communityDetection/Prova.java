package communityDetection;

import java.io.File;
import java.util.LinkedList;

import javax.print.attribute.standard.PrinterMessageFromOperator;

import org.apache.commons.math3.util.CombinatoricsUtils;

public class Prova {

	public static void main(String[] args) {

		
		System.out.println(CombinatoricsUtils.binomialCoefficient(50, 15));
		
//		double toGB = 1024 * 1024;
//		double toMB = 1024;
//
//		// double total = Runtime.getRuntime().totalMemory() / toGB;
//		// double free = Runtime.getRuntime().freeMemory() / toGB;
//		//
//		// double allocatedMemory = total - free;
//		//
//		// System.out.println(total + "\n" + free);
//		// System.out.println(allocatedMemory );
//
//		File f = new File("recovery");
//		final int MAXMEMORY = 4196;
//		int i = 0;
//		LinkedList<Integer> ll = new LinkedList<>();
//		int c = 5000000;
//
//		int count = 0;
//		while (true) {
//			ll.add(i++);
//			if (i % c == 0) {
//				double total = Runtime.getRuntime().totalMemory() / toGB;
//				double free = Runtime.getRuntime().freeMemory() / toGB;
//
//				double allocatedMemory = total - free;
//
//				System.out.printf("%.3f %.3f %.3f ", total, free, allocatedMemory);
//
//				System.out.println("\n");
//
//				if (count == 0 && total >= MAXMEMORY * 0.8) {
//					c /= 20;
//					count++;
//				} else if (count == 1 && total >= MAXMEMORY * 0.85) {
//					c /= 10;
//					count++;
//				} else if (count == 2 && total >= MAXMEMORY * 0.9) {
//					c /= 5;
//					count++;
//				}
//				if (total >= (MAXMEMORY * 0.95)) {
//					System.out.println("ciaao");
//					System.exit(1);
//
//				}
//
//				// System.out.print(total + "\n" + free);
//				// System.out.println(allocatedMemory+"\n\n\n" );
//			}
//
//		}
//
//		// get the total memory for my app
//		// long total1 = Runtime.getRuntime().totalMemory();
//		// // get the free memory available
//		// long free1 = Runtime.getRuntime().freeMemory();
//		//
//		// // some simple arithmetic to see how much i use
//		// long used = total1 - free1;
//		//
//		// System.out.println("Used memory in bytes: " + used / toGB);
//
//		// System.out.println(CombinatoricsUtils.binomialCoefficientDouble(40, 30));
//		//
//		//
//		// boolean flag=true;
//		// while (true) {
//		// System.out.println("asd");
//		// if (flag)
//		// break;
//		// }
	}
}
