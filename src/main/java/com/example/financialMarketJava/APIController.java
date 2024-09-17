package com.example.financialMarketJava;

import java.io.IOException;
import java.util.ArrayList;

// Service Class to Handle SQL data (when implemented)

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import Objects.HistoricalTimeSeries;
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
    public HistoricalTimeSeries getStockHistoryFromScraping(String ticker) throws IOException {
		return data.getStockHistoryFromScraping(ticker);
    }

}

