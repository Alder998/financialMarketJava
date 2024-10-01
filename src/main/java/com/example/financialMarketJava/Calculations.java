// This file has been created to manage the multiple methods to handle calculations

package com.example.financialMarketJava;

import java.util.ArrayList;
import java.util.HashMap;

import Objects.CovarianceStructure;
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
	
	public static float computeCovariance (ArrayList<Float> numberList1, ArrayList<Float> numberList2) {
		
		// The two number Lists must be of the same length (this must be a control, since for trading days
		// and stuff like this, it may be possible that two series have two different lengths.
		
		// Compute the two means
		float mean1 = computeMean(numberList1);
		float mean2 = computeMean(numberList2);
		
		float sumCov = 0.0f;
		for (int i = 0; i < numberList1.size(); i++) {
			sumCov += (numberList1.get(i) - mean1) * (numberList2.get(i) - mean2);
		}
        float covariance = sumCov / numberList1.size();
        
        return covariance;
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
	
	public static float computeReturnCovariance (String ticker1, String ticker2, String period) {
		ArrayList<Float> returns1 = getReturnDiff (ticker1, period);
		ArrayList<Float> returns2 = getReturnDiff (ticker2, period);
		
		// align the sizes (CHANGE IN FUTURE)
		if (returns1.size() > returns2.size()) {
			returns1 = new ArrayList<Float>(returns1.subList(returns1.size()-returns2.size(), returns1.size()-1));
		}
		else if (returns2.size() > returns1.size()) {
			returns2 = new ArrayList<Float>(returns2.subList(returns2.size()-returns1.size(), returns2.size()-1));
		}
		float covariance = computeCovariance(returns1, returns2);
		
		return covariance;
	}
	
	// Method to compute the covariance of one stock compared to an array of other stocks
	public static CovarianceStructure computeReturnCovariances (String ticker1, ArrayList<String> tickers, String period) {
		
		CovarianceStructure covarianceStructure = new CovarianceStructure();
		// for future purposes, it may be good to see this method as the creation of a row of the variance-covariance matrix
		// therefore, add to the array of stocks the stock itself
		if (!tickers.contains(ticker1)) {
			tickers.add(ticker1);
		}
		
		HashMap<String, Float> covariances = new HashMap<String, Float>();
		for (String singleTicker : tickers) {
			float singleCovariance = computeReturnCovariance(singleTicker, ticker1, period);
			covariances.put(singleTicker, singleCovariance);
		}
		// fill the new Object
		covarianceStructure.setCovariances(covariances);
		covarianceStructure.setTicker(ticker1);
		return covarianceStructure;
	}
	
	// Here it comes the though stuff: Variance-Covariance Matrix
	
	
}


