package org.vu.tbosman.simulation;

import java.util.Random;

public class RandomSimulator {

	Random random;
	public RandomSimulator(long seed) {
		this.random = new Random(seed);
	}
	
	double[] simulateGBMEuler(double S0, double period, 
			double drift, double vola, double interval) {
		int M = (int) Math.ceil(1+period/interval);
		
		double[] GBM = new double[M];
		GBM[0] = S0;
		for(int i=1; i<M ; i++) {
			GBM[i] = GBM[i-1]*(1 + drift*interval 
					+ vola*Math.sqrt(interval)*random.nextGaussian());
		}
		return GBM; 
	}
	
	double[] simulateGBMExact(double S0, double period, 
			double drift, double vola, double interval) {
		int M = (int) Math.ceil(1+period/interval);
		
		double[] GBM = new double[M];
		GBM[0] = S0;
		for(int i=1; i<M ; i++) {
			GBM[i] = GBM[i-1]*Math.exp((drift - 0.5*vola*vola)*interval 
					+ vola*Math.sqrt(interval)*random.nextGaussian());
		}
		return GBM; 
	}
	
	double[][] simulateHeston(double S0, double period, 
			double drift, double initVola, double interval){
		int M = (int) Math.ceil(1+period/interval);
		double[] S = new double[M]; 
		double[] V = new double[M]; 
		S[0] = S0; 
		V[0] = initVola; 
		
		//##Constants##	
		double KAPPA = 2; 
		double THETA = 0.04; 
		double XI = 0.1; 
		double RHO = -0.6;
		
		for(int i=1; i<M;i++) {
			double Z1 = random.nextGaussian(); 
			double Z2 = RHO*Z1 + Math.sqrt(1-RHO*RHO)*random.nextGaussian(); 
			S[i] = S[i-1]*(1 + drift + Math.sqrt(V[i-1]*interval)*Z1);
			V[i] = V[i-1]*(1 - KAPPA*interval 
					+ (XI/Math.sqrt(V[i-1]))*Math.sqrt(interval)*Z2) 
					+ KAPPA*THETA*interval;
		}
		
		double[][] output = new double[2][M];
		output[0] = S;
		output[1] = V;
		
		return output;
		
	}

}
