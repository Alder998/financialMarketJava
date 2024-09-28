// This file has been created to manage the multiple methods to handle calculations

package com.example.financialMarketJava;

import java.util.ArrayList;

import Objects.HistoricalTimeSeries;
import financialData.yfinanceScraper;

public class Calculations {
	
	public static float computeMean (ArrayList<Float> numberList) {
        float sum = 0.0f;
        for (Float value : numberList) {
            sum += value;
        }
        float mean = sum / numberList.size();
        return mean;
	}
	
	public static double computeStdDeviation (ArrayList<Float> numberList) {
		float mean = computeMean(numberList);
		float sum = 0.0f;
        for (Float value : numberList) {
            sum += Math.pow((value - mean), 2);
        }
        double variance = sum / numberList.size();
        double stdDeviation = Math.sqrt(variance); 
        return stdDeviation;
	}
	
	public static ArrayList<Float> getReturnDiff(String ticker, String period) {
		
		// Get the stock quote's time series
		ArrayList<HistoricalTimeSeries> tsData =  yfinanceScraper.getHistoricalValues(ticker, period);
		// Iterate through the Objects in the time series data
		ArrayList<Float> returns = new ArrayList<Float>();
		for (HistoricalTimeSeries singleQuote : tsData) {
			returns.add(singleQuote.getAdjClose());
		}
		// compute the difference among different price for unit of time
		ArrayList<Float> returnDiff = new ArrayList<Float>();
		for (int i = returns.size() - 1; i > 0; i--) {
			float diff = ((returns.get(i) - returns.get(i-1)) / returns.get(i-1)) * 100;
			returnDiff.add(diff);
		}
		// Compute the mean
		return returnDiff;
	}
	
	public static float computeAverageReturn (String ticker, String period) {
		ArrayList<Float> returns = getReturnDiff (ticker, period);
		// Get the mean
		return computeMean(returns);
	}
	
	public static double computeReturnStdDeviation (String ticker, String period) {
		ArrayList<Float> returns = getReturnDiff (ticker, period);
		// Get the mean
		return computeStdDeviation(returns);
	}
	
}


