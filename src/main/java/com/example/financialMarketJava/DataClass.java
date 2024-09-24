package com.example.financialMarketJava;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.stereotype.Component;

import Objects.HistoricalTimeSeries;
import Objects.Ticker;
import financialData.yfinanceAPI;
import financialData.yfinanceScraper;
import yahoofinance.Stock;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

// The Data Class is where all the main operations will be performed

@Component
public class DataClass {
	
	// This method may be deleted, when the project structure would be set up, and therefore will be sure
	// that the project is working
	public String displayStatus(String status) {
		System.out.println(status);
		return status;
	}
	
	// Try the Methods from the YF API
	
	// Deprecated Method: Yahoo Finance is closed this API
	public ArrayList<HistoricalQuote> getStockHistory(String ticker, String timeSpan, String interval) throws IOException {
		
		ArrayList<HistoricalQuote> history = new ArrayList<HistoricalQuote>();
		try {
			Stock stock = yfinanceAPI.getStockFromYahooFinance(ticker);
			
			// We need to set the date parameters as they should be set
			Calendar calendarTimeSpan = DataClass_Utils.getDateFromString(timeSpan);
			
			// Now, cover the interval
			ArrayList<String> timeComponents = DataClass_Utils.getTimeAndUnitFromString(interval);
			String intervalString = timeComponents.get(0).toString();
			
			Interval intervalObj = null;
			if (intervalString == "y") {
				intervalObj = Interval.DAILY;
			}
			if (intervalString == "mo") {
				intervalObj = Interval.MONTHLY;
			}
			if (intervalString == "d") {
				intervalObj = Interval.DAILY;
			}
			
			List<HistoricalQuote> historyList = stock.getHistory(calendarTimeSpan, intervalObj);
			// Change from List to ArrayList for simplicity
			history.addAll(historyList);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return history;
	}
	
	public ArrayList<HistoricalTimeSeries> getStockHistoryFromScraping (String ticker, String period) {
		
		return yfinanceScraper.getHistoricalValues(ticker, period);
	}
	
	public Ticker getTicker (String symbol, String period) {
		Ticker ticker = new Ticker();
		
		ticker.setSymbol(symbol);
		ticker.setHistory(this.getStockHistoryFromScraping(symbol, period));
		
		return ticker;
	}
	
	
	
}