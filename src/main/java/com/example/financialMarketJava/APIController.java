package com.example.financialMarketJava;

import java.io.IOException;
import java.util.ArrayList;

// Service Class to Handle SQL data (when implemented)

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import Objects.CovarianceStructure;
import Objects.HistoricalTimeSeries;
import Objects.Portfolio;
import Objects.Ticker;
import yahoofinance.histquotes.HistoricalQuote;

@Controller
public class APIController {
	
    @Autowired
    private DataClass data;

    // TODO: Delete
    @Transactional
    public String displayStatus(String status) throws Exception {
        return data.displayStatus(status);
    }
    
    @Transactional
    public ArrayList<HistoricalQuote> getStockHistory(String ticker, String timeSpan, String interval) throws IOException {
		return data.getStockHistory(ticker, timeSpan, interval);
    }
    
    @Transactional
    public ArrayList<HistoricalTimeSeries> getStockHistoryFromScraping(String ticker, String period) throws IOException {
		return data.getStockHistoryFromScraping(ticker, period);
    }
    
    @Transactional
    public Ticker getTicker(String ticker, String period) throws IOException {
		return data.getTicker(ticker, period);
    }

    @Transactional
    public float calculateAverageReturns(String ticker, String period) throws Exception {
		return data.calculateAverageReturns(ticker, period);
    }
    
    @Transactional
    public double calculateStdDeviation(String ticker, String period) throws Exception {
		return data.calculateStdDeviation(ticker, period);
    }
    
    @Transactional
    public float calculateCovariance (String ticker1, String ticker2, String period, Boolean fromCached) throws Exception {
		return data.calculateCovariance (ticker1, ticker2, period, fromCached);
    }
    
    @Transactional
    public CovarianceStructure calculateCovariances (String ticker1, ArrayList<String> tickers, String period, Boolean fromCached) throws Exception {
		return data.calculateCovariances (ticker1, tickers, period, fromCached);
    }
    @Transactional
    public float[][] generateVarianceCovarianceMatrix (ArrayList<String> tickers, String period, Boolean fromCached) throws Exception {
    	return data.generateVarianceCovarianceMatrix(tickers, period, fromCached);
    }
    @Transactional
    public void createCovarianceMatrix (ArrayList<String> tickers, String period, String assetClass, Boolean fromCached) throws Exception {
    	data.createCovarianceMatrix (tickers, period, assetClass, fromCached);
    }
    
    @Transactional
    public void createVarianceCovarianceMatrixAndReturnFromDatabase (String period, String assetClass, String stockIndex, Integer subList) throws Exception {
    	data.createVarianceCovarianceMatrixAndReturnFromDatabase (period,assetClass, stockIndex, subList);
    }
    @Transactional
    public void updateVarianceCovarianceMatrixFromDatabase (String period, String stockIndex, Integer subList, String assetClass) throws Exception {
    	data.addTickersToReturnsAndVarianceCovarianceMatrixFromDatabase (period, stockIndex, subList, assetClass);
    }
    @Transactional
    public Portfolio optimizeStockPortfolio (String period, String assetClass) throws Exception {
    	return data.optimizeStockPortfolio (period, assetClass);
    }
    @Transactional
    public Portfolio optimizeStocksAndBondsPortfolio (String period, ArrayList<String> assetClasses) throws Exception {
    	return data.optimizeMultiAssetPortfolio (period, assetClasses);
    }
}

