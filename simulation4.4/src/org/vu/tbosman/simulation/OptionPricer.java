package org.vu.tbosman.simulation;

import java.io.FileWriter;
import java.io.IOException;

public class OptionPricer {

	RandomSimulator sim;
	public OptionPricer(long seed) {
		sim = new RandomSimulator(seed);  
	}
		
	double[][] simulatedOptionPrice(double K, double S0, double vola, 
			double rate, double dividend, double period, int N) {
		double[] ePrices = new double[N]; 
		double[] aPrices = new double[N];
		
		for(int i=0; i<N; i++) {
			double[][] S_V = sim.simulateHeston(S0, period, rate-dividend, vola, (1.0/250)); 
			double[] GBM = S_V[0];
			//Uncomment for GBM simulation
//			double[] GBM = sim.simulateGBMExact(S0, period, rate-dividend, vola, (1.0/250)); 
		
			ePrices[i] = getRealisedEuropeanPrice(GBM, K);
			aPrices[i] = getRealisedAsianPrice(GBM, K);
		
		}
		
		double avPriceA = 0;
		for(int i=0; i<aPrices.length;i++) {
			avPriceA += aPrices[i]*Math.exp((-rate)*period);			
			
		}		
		avPriceA /= N;
		
		double avPriceE = 0; 
		for(int i=0; i<ePrices.length;i++) {
			avPriceE += ePrices[i]*Math.exp((-rate)*period);			
			
		}		
		avPriceE /= N;
		
		System.out.println("Eur-Strike:"+K+" & "+String.format("[%.2f - %.2f]", 
				avPriceE-1.96*Stats.getSampleStd(ePrices)/Math.sqrt(N) ,
				avPriceE+1.96*Stats.getSampleStd(ePrices)/Math.sqrt(N)));
		System.out.println("Asia-Strike:"+K+
				" & "+String.format("[%.2f - %.2f]", 
						avPriceA-1.96*Stats.getSampleStd(aPrices)/Math.sqrt(N) ,
						avPriceA+1.96*Stats.getSampleStd(aPrices)/Math.sqrt(N)));
		
				
		double[][] priceArrays = new double[2][N];
		
		priceArrays[0] = ePrices; 
		priceArrays[1] = aPrices; 
		return priceArrays;
	}
	
	
	double getRealisedAsianPrice(double[] GBM, double K) {
		
		double integral = GBM[0]+GBM[GBM.length-1];
		for(int i=1; i<GBM.length-1;i++) {
			integral += 2*GBM[i];
		}
		integral /= 2*(GBM.length-1);
		return Math.max(0, integral-K);
	}
	
	double getRealisedEuropeanPrice(double[] GBM, double K) {
		return Math.max(0, GBM[GBM.length-1] - K);
	}
	
	static void writeToCsv(String fname, double[][] data) {
		FileWriter writer;
		try {
			writer = new FileWriter(fname);
			
			
			for(int i =0; i < data.length ; i++) {
				for (int j=0; j < data[0].length-1 ; j++) {
					writer.append(data[i][j]+",");
				}
				writer.append(data[i][data[0].length-1]+"\r\n");
			}
			writer.flush();
			writer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void writeHeston(double rate, double dividend, double vola) {
		
		double[][] heston = new RandomSimulator(1).simulateHeston(
				420, 4*100, rate - dividend, vola, 1.0/250);//100k days simulation
		writeToCsv("hestonVolas.csv", heston);
	}
	
	
	
	public static void main(String... args) {
		
			OptionPricer op = new OptionPricer(1);		
			op.simulatedOptionPrice(350, 418.37, 0.13809, 0.00177, 0.001841, 4.0/12, 1000000);
			op.simulatedOptionPrice(360, 418.37, 0.13809, 0.00177, 0.001841, 3.0/12, 1000000);
			op.simulatedOptionPrice(380, 418.37, 0.13809, 0.00177, 0.001841, 2.0/12, 1000000);
			op.simulatedOptionPrice(400, 418.37, 0.13809, 0.00177, 0.001841, 1.0/12, 1000000);
			op.simulatedOptionPrice(420, 418.37, 0.13809, 0.00177, 0.001841, 6.0/12, 1000000);
			
			double[][] eaPrices = op.simulatedOptionPrice(
					420, 418.37, 0.13809, 0.00177, 0.001841, 6.0/12, 1000000);
			writeToCsv("optionVerification.csv", eaPrices);
			
			writeHeston(0.00177, 0.001841, 0.13809);
			
	}

}
