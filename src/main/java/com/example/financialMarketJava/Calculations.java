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
	
	public static float computeAverageReturn(String ticker, String period) {
		
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
		float averageReturns = computeMean(returnDiff);
		return averageReturns;
	}
	
}


