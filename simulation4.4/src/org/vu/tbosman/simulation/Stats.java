package org.vu.tbosman.simulation;

public class Stats {

	private Stats() {
	}

	public static double getSampleStd(double[] sample) {
		double[] dev = new double[sample.length]; 
		double mean = getMean(sample);
		for(int i=0; i<sample.length;i++) {
			dev[i] = sample[i] - mean; 
			dev[i] *= dev[i];			
		}
		
		double std = 0; 
		for(int i=0; i<dev.length;i++) {
			std += dev[i];
		}
		std /= sample.length-1;
		std = Math.sqrt(std);
		return std;
		
	}
	
	public static double getMean(double[] sample) {
		double mean =0;
		for(int i=0; i<sample.length; i++) {
			mean += sample[i];
		}
		mean /= sample.length;
		return mean;
	}
}
