package com.example.financialMarketJava;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Objects.CovarianceStructure;
import Objects.HistoricalTimeSeries;
import Objects.Ticker;
import yahoofinance.histquotes.HistoricalQuote;

@RestController
public class APIEndPoint {

    @Autowired
    private APIController controller;

    @GetMapping("/api/status")
    public String displayStatus(@RequestParam String status) throws Exception {
        return controller.displayStatus(status);
    }
    
    @GetMapping("/api/tickerHistory")
    public ArrayList<HistoricalQuote> getStockHistory(@RequestParam String ticker, @RequestParam String timeSpan,
    												  @RequestParam String interval) throws Exception {
        return controller.getStockHistory(ticker, timeSpan, interval);
    }
    
    @GetMapping("/api/history")
    public ArrayList<HistoricalTimeSeries> getStockHistoryFromScraping(@RequestParam String ticker, @RequestParam String period) throws Exception {
        return controller.getStockHistoryFromScraping(ticker, period);
    }
    
    @GetMapping("/api/ticker")
    public Ticker getTicker(@RequestParam String ticker, @RequestParam String period) throws Exception {
        return controller.getTicker(ticker, period);
    }
    
    @GetMapping("/api/returns")
    public float calculateAverageReturns(@RequestParam String ticker, @RequestParam String period) throws Exception {
        return controller.calculateAverageReturns(ticker, period);
    }
    
    @GetMapping("/api/std")
    public double calculateStdDeviation(@RequestParam String ticker, @RequestParam String period) throws Exception {
        return controller.calculateStdDeviation(ticker, period);
    }
    
    @GetMapping("/api/covariance")
    public float calculateCovariance(@RequestParam String ticker1, @RequestParam String ticker2, @RequestParam String period) throws Exception {
        return controller.calculateCovariance(ticker1, ticker2, period);
    }
    
    @GetMapping("/api/covariances")
    public CovarianceStructure calculateCovariances(@RequestParam String ticker1, @RequestParam ArrayList<String> tickers, @RequestParam String period) throws Exception {
        return controller.calculateCovariances(ticker1, tickers, period);
    }
    
    @GetMapping("/api/varianceCovarianceMatrix")
    public float[][] generateVarianceCovarianceMatrix(@RequestParam ArrayList<String> tickers, @RequestParam String period) throws Exception {
        return controller.generateVarianceCovarianceMatrix(tickers, period);
    }
    
}