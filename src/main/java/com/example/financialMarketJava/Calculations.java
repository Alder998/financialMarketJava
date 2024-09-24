// This file has been created to manage the multiple methods to handle calculations

package com.example.financialMarketJava;

import financialData.yfinanceScraper;

public class Calculations {
	
	public static float computeMeanReturn(String ticker, String period) {
		
		// Get the stock quote's time series
		yfinanceScraper.getHistoricalValues(ticker, period);
		
		return 0;
	}
	
}


