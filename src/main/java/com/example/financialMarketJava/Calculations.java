// This file has been created to manage the multiple methods to handle calculations

package com.example.financialMarketJava;

import java.util.ArrayList;
import java.util.HashMap;

import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Variable;

import Objects.CovarianceStructure;
import Objects.HistoricalDataCache;
import Objects.HistoricalTimeSeries;
import Objects.Portfolio;
import Objects.VarianceCovarianceMatrix;
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
		
		// check if the return Diff data is already in the cache
		if (HistoricalDataCache.get(ticker) != null) {
			return HistoricalDataCache.get(ticker);
		}
		else {
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
		// Save the data in the cache
		HistoricalDataCache.put(ticker, returnDiff);
		return returnDiff;
		}
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
	
	public static float computeReturnCovariance (String ticker1, String ticker2, String period, Boolean fromCached) {
		ArrayList<Float> returns1 = new ArrayList<Float>();
		ArrayList<Float> returns2 = new ArrayList<Float>();
		if (fromCached) {
			returns1 = HistoricalDataCache.get(ticker1);
			returns2 = HistoricalDataCache.get(ticker2);
		}
		else {
			returns1 = getReturnDiff (ticker1, period);
			returns2 = getReturnDiff (ticker2, period);
		}

		// align the sizes (CHANGE IN FUTURE)
		if (returns1.size() > returns2.size()) {
			returns1 = new ArrayList<Float>(returns1.subList(returns1.size()-returns2.size(), returns1.size()));
		}
		else if (returns2.size() > returns1.size()) {
			returns2 = new ArrayList<Float>(returns2.subList(returns2.size()-returns1.size(), returns2.size()));
		}
		float covariance = computeCovariance(returns1, returns2);
		
		return covariance;
	}
	
	// Method to compute the covariance of one stock compared to an array of other stocks
	public static CovarianceStructure computeReturnCovariances (String ticker1, ArrayList<String> tickers, String period, Boolean fromCached) {
		
		CovarianceStructure covarianceStructure = new CovarianceStructure();
		// for future purposes, it may be good to see this method as the creation of a row of the variance-covariance matrix
		// therefore, add to the array of stocks the stock itself
		if (!tickers.contains(ticker1)) {
			tickers.add(ticker1);
		}
		
		HashMap<String, Float> covariances = new HashMap<String, Float>();
		for (String singleTicker : tickers) {
			float singleCovariance = computeReturnCovariance(singleTicker, ticker1, period, fromCached);
			covariances.put(singleTicker, singleCovariance);
		}
		// fill the new Object
		covarianceStructure.setCovariances(covariances);
		covarianceStructure.setTicker(ticker1);
		return covarianceStructure;
	}
	
	// Here it comes the though stuff: Variance-Covariance Matrix
	public static float[][] getVarianceCovarianceMatrix (ArrayList<String> tickers, String period, Boolean fromCached) {
		float[][] varianceCovarianceMatrix = new float[tickers.size()][tickers.size()];
		
		// Get the single covariance structure
		int row = 0; // this counters are to populate the matrix with values
		for (String ticker : tickers) {
			// Add some logging
			System.out.println("Computing Variance-Covariance Matrix for Ticker: " + ticker +
					" - Processing stock: " + (row + 1) + " out of " + tickers.size() + " Tickers" );
			CovarianceStructure singleCovarianceStructure = computeReturnCovariances(ticker, tickers, period, fromCached);
			// Unpack the single covariances, update the row number
			int column = 0;
			for (String tickerCovariance : tickers) {
				float covariance = singleCovarianceStructure.getCovariances().get(tickerCovariance);
				varianceCovarianceMatrix[row][column] = covariance;
				// Update columns
				column ++;
			}
			row ++;
		}
		return varianceCovarianceMatrix;
	}
	
	// Optimization Function
	// Calculation of Portfolio Metrics
	
    // Optimization Problem for Portfolio with OjAlgo
    public static Portfolio optimizeStockPortfolio (VarianceCovarianceMatrix varianceCovarianceMatrixObject) {
    	Portfolio portfolio = new Portfolio();
    	// 1. Weights definition (variable according variance-covariance definition)
    	float [][] varianceCovarianceMatrix = varianceCovarianceMatrixObject.getVarianceCovarianceMatrix();
    	int n = varianceCovarianceMatrix.length;
        ExpressionsBasedModel model = new ExpressionsBasedModel();
    	for (int i=0; i<n; i++) {
            model.addVariable("w" + i).lower(0).upper(1);
    	}

    	// 2. Add constraints
        model.addExpression("WeightConstraint").level(1);
    	for (int j=0; j<n; j++) {            
            // Add Objective Function
            model.getExpression("WeightConstraint").set(model.getVariables().get(j), 1);
    	}
    	
        // 3. Define the Objective function: Minimize w^T Σ w
        model.addExpression("Objective").weight(1);
        for (int l = 0; l < n; l++) {
            for (int k = 0; k < n; k++) {
                float covariance = varianceCovarianceMatrix[l][k];
                model.getExpression("Objective").set(model.getVariables().get(l),
                		model.getVariables().get(k), covariance);
            }
        }
    	
        // 4. Solve Optimization Problem
        Optimisation.Result result = model.minimise();
        // 5. print output
        System.out.println("Solution State: " + result.getState());
        System.out.println("Optimal Weights: ");
        // Print the sum of weights
        double wTot = 0;
        for (int i = 0; i<result.size(); i++) {
        	wTot += result.get(i).doubleValue();
        }
        System.out.println("Sum of Portfolio Weights: " + wTot);
        
        ArrayList<String> portfolioTickers = new ArrayList<String>();
        ArrayList<Float> portfolioWeights = new ArrayList<Float>();
        // Print the Name of Stocks to be bought with the relative weight and fill the arrays for Object
        for (int m = 0; m<result.size(); m++) {
            System.out.println(varianceCovarianceMatrixObject.getTickers().get(m) + " Weight in portfolio: " + result.get(m));
            if (result.get(m).doubleValue() > 0) {
                portfolioTickers.add(varianceCovarianceMatrixObject.getTickers().get(m));
                portfolioWeights.add((float) result.get(m).doubleValue());
            }
        }
        // Populate Object
        portfolio.setTickers(portfolioTickers);
        portfolio.setWeights(portfolioWeights);
        
        return portfolio;
    }
        
    // Portfolio Analytics
    public static float computePortfolioReturns(Portfolio portfolio) {
    	// iterate for single components
    	float portfolioReturns = 0;
    	// Logging
    	System.out.println("Computing Portfolio Return...");
    	for (int i = 0; i < portfolio.getTickers().size(); i++) {
    		portfolioReturns += computeAverageReturn(portfolio.getTickers().get(i), portfolio.getMetricsPeriod()) *
    							portfolio.getWeights().get(i);
    	}
    	return portfolioReturns;
    }
    

    // Get Portfolio Variance sigma_p^2 = w^T * Sigma * w
    public static double calculatePortfolioVariance(Portfolio portfolio) {
    	
    	// compute Portfolio tickers Variance Covariance Matrix
    	// compute it from cached, because we suppose the returns to be calculated before and the diff
    	// stored in the cache
    	float[][] covMatrix = getVarianceCovarianceMatrix(portfolio.getTickers(), portfolio.getMetricsPeriod(), true);
    	
        double portfolioVariance = 0.0;
        // Iterate Over Variance-Covariance Matrix
        for (int i = 0; i < portfolio.getTickers().size(); i++) {
            for (int j = 0; j < portfolio.getTickers().size(); j++) {
            	portfolioVariance += portfolio.getWeights().get(i) * covMatrix[i][j] * portfolio.getWeights().get(j);
            }
        }
        return portfolioVariance;
    }
    	
}


