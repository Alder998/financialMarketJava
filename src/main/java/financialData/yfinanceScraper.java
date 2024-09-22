package financialData;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.example.financialMarketJava.DataClass_Utils;

import Objects.HistoricalTimeSeries;

public class yfinanceScraper {
	
	// Here we are Trying to scrape some useful data from Yahoo Finance
	 public static ArrayList<HistoricalTimeSeries> getHistoricalValues (String ticker, String period) {
		 
		 ArrayList<HistoricalTimeSeries> history = new ArrayList<HistoricalTimeSeries>();
		 boolean dividendsPresent = false;
		 boolean splitsPresent = false;

	        try {
	            // Get the URL of the page we want to scrape
	        	String url = "";
	        	if (period == "1y") {
		            url = "https://finance.yahoo.com/quote/" + ticker + "/history/";
	        	}
	        	else {
	        		// The period on the URL is expressed as a number date: convert the interval into date
	        		String period1 = DataClass_Utils.getUnixTimeStampFromString(period);
	        		String period2 = DataClass_Utils.getUnixTimeStampFromString("now"); // the basic date taken is the date of today 
		            url = "https://finance.yahoo.com/quote/" + ticker + "/history/?period1=" + period1 + "&period2=" + period2;
	        	}
	            Document document = Jsoup.connect(url).get();
	            
	            // Work on Extracting the data that we want
	            Elements rowsWithTag = document.select("tr.yf-ewueuo");
	            	         
	            for (Element element : rowsWithTag.subList(1, rowsWithTag.size())) {
	            	// Isolate the basic components of the time series
	            	String component = element.text();
	            	// Isolate the date (always 'mo day, year'), convert it into OffsetDateTime
	            	OffsetDateTime date = DataClass_Utils.extractOffsetDateTimeFromString(component);
	            	
	            	// Prepare to add the data to the object
	            	HistoricalTimeSeries historyRow = new HistoricalTimeSeries();
	            		            	
	            	// to get specific data, isolate the strings using the space
	                String[] words = component.split(" ");
	                // Small refinement to incorporate stock splits, when present
                	String stockSplit = "NaN";
                	float dividend = 0;
	                if (words.length < 7) {
	                	if (Arrays.asList(words).contains("Splits")) {
	                		splitsPresent = true;
		                	stockSplit = words[3];
			            	historyRow.setDate(date);
			            	historyRow.setStockSplit(stockSplit);
	                	}
	                	if (Arrays.asList(words).contains("Dividend")) {
	                		dividendsPresent = true;
	                		dividend = Float.parseFloat(words[3]);
			            	historyRow.setDate(date);
			            	historyRow.setDividend(dividend);
	                	}
	                }
	                else {
		                float open = Float.parseFloat(words[3]);
		    	        float high = Float.parseFloat(words[4]);
		                float low = Float.parseFloat(words[5]);
		                float close = Float.parseFloat(words[6]);
		                float adjClose = Float.parseFloat(words[7]);
		                long volume = Long.parseLong(words[8].replace(",", ""));
		                
		                // Fill the object
		            	historyRow.setDate(date);
		            	historyRow.setOpen(open);
		            	historyRow.setHigh(high);
		            	historyRow.setLow(low);
		            	historyRow.setClose(close);
		            	historyRow.setAdjClose(adjClose);
		            	historyRow.setVolume(volume);
		            	historyRow.setStockSplit(stockSplit);
	                }
	                // finally, append
	            	history.add(historyRow);
	            }
	            
	            // Update the final Object for the extraordinary operations (stock splits and dividends)
		        if (splitsPresent) {
			        history = DataClass_Utils.updateHistoryForStocksOperations(history, "Splits");
		        }
		        if (dividendsPresent) {
			        history = DataClass_Utils.updateHistoryForStocksOperations(history, "Dividends");
		        }

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return history;
	    }
	
}