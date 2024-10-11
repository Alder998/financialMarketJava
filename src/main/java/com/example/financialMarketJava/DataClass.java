package com.example.financialMarketJava;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import Objects.CovarianceStructure;
import Objects.HistoricalTimeSeries;
import Objects.Ticker;
import financialData.yfinanceAPI;
import financialData.yfinanceScraper;
import yahoofinance.Stock;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;
import org.springframework.jdbc.core.JdbcTemplate;

// The Data Class is where all the main operations will be performed

@Component
public class DataClass {
	
    @Autowired
    private JdbcTemplate jdbcTemplate;
	
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
	
	public float calculateAverageReturns (String ticker, String period) {
		return Calculations.computeAverageReturn(ticker, period);
	}
	
	public double calculateStdDeviation (String ticker, String period) {
		return Calculations.computeReturnStdDeviation(ticker, period);
	}
	
	public float calculateCovariance (String ticker1, String ticker2, String period) {
		return Calculations.computeReturnCovariance(ticker1, ticker2, period);
	}
	
	public CovarianceStructure calculateCovariances (String ticker1, ArrayList<String> tickers, String period) {
		return Calculations.computeReturnCovariances(ticker1, tickers, period);
	}
	
	public float[][] generateVarianceCovarianceMatrix (ArrayList<String> tickers, String period) {
		return Calculations.getVarianceCovarianceMatrix(tickers, period);
	}
	
	public float[][] getVarianceCovarianceMatrix (ArrayList<String> tickers, String period) throws DataAccessException {
		String sql = "SELECT * FROM VarianceCovarianceMatrix";
		List<float[]> matrixRows = null;
		try {
			matrixRows = jdbcTemplate.query(sql, (ResultSet rs) -> {
			        return DataClass_Utils.extractMatrix(rs);
			    });
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
	   return matrixRows.toArray(new float[matrixRows.size()][]);
	}
	
	public void createCovarianceMatrix (ArrayList<String> tickers, String period) throws DataAccessException {
		String sql = "INSERT INTO VarianceCovarianceMatrix (period, stockNumber, VarianceCovarianceMatrix) VALUES (?, ?, ?)";
		float[][] covarianceMatrix = new float[tickers.size()][tickers.size()];
		covarianceMatrix = generateVarianceCovarianceMatrix(tickers, period);
		// Convert the variance Covariance Matrix into a String into a JSON
		ObjectMapper objectMapper = new ObjectMapper();
        String covarianceMatrixJson = null;
        try {
            covarianceMatrixJson = objectMapper.writeValueAsString(covarianceMatrix);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataAccessException("Error Serializing the Variance-Covariance Matrix in JSON!") {};
        }
        jdbcTemplate.update(sql, period, tickers.size(), covarianceMatrixJson);
	}
	
}