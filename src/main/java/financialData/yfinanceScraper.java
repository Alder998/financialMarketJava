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
		 
		 // Encode the name of ticker to adapt it to the index that have the character "^"
		 ticker = ticker.replace("^", "%5E");
		 
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
	            
	            if (rowsWithTag.size() == 0) {
	            	throw new Exception ("Yahoo Finance Error! No values found for URL");
	            }
	            	            	            	         
	            for (Element element : rowsWithTag.subList(1, rowsWithTag.size())) {
	            	// Isolate the basic components of the time series
	            	String component = element.text();
	            	//System.out.println(component);
	            	if (!component.toLowerCase().equals("there are no in the selected time period.")) {
	            		
		            	// Isolate the date (always 'mo day, year'), convert it into OffsetDateTime
		            	OffsetDateTime date = DataClass_Utils.extractOffsetDateTimeFromString(component);
		            	
		            	// Prepare to add the data to the object
		            	HistoricalTimeSeries historyRow = new HistoricalTimeSeries();
		            		            	
		            	// to get specific data, isolate the strings using the space
		                String[] words = component.split(" ");
		                // Small refinement to incorporate stock splits, when present
	                	String stockSplit = "NaN";
	                	float dividend = 0;
		            	// set dividend and splits to avoid NullPointer Exception while updating for special
		            	// Operations
		            	historyRow.setDividend(dividend);
		            	historyRow.setStockSplit(stockSplit);
		            	historyRow.setDate(date);
		                if (words.length < 7) {
		                	if (Arrays.asList(words).contains("Splits")) {
		                		splitsPresent = true;
			                	stockSplit = words[3];
				            	historyRow.setStockSplit(stockSplit);
		                	}
		                	if (Arrays.asList(words).contains("Dividend")) {
		                		dividendsPresent = true;
		                		dividend = Float.parseFloat(words[3]);
				            	historyRow.setDividend(dividend);
		                	}
		                }
		                else {
			                float open = Float.parseFloat(words[3].replace(",", ""));
			    	        float high = Float.parseFloat(words[4].replace(",", ""));
			                float low = Float.parseFloat(words[5].replace(",", ""));
			                float close = Float.parseFloat(words[6].replace(",", "").replace("<", "").replace(">", ""));
			                // Better to add an if, to avoid to have an error if volume or Adjusted Close are not available
			                // (it may be not available for very old dates)
		                	float adjClose = 0.0f;
			                if (words.length > 7 && !words[7].contains("-") && !words[7].contains("<")) {
			                	adjClose = Float.parseFloat(words[7].replace(",", ""));
			                }
		                	long volume = 0;
			                if (words.length > 8 && !words[8].contains("-") && !words[8].contains(">")) {
				                volume = Long.parseLong(words[8].replace(",", ""));
			                }
			                
			                // Fill the object
			            	historyRow.setDate(date);
			            	historyRow.setOpen(open);
			            	historyRow.setHigh(high);
			            	historyRow.setLow(low);
			            	historyRow.setClose(close);
			            	historyRow.setAdjClose(adjClose);
			            	historyRow.setVolume(volume);
			            	historyRow.setStockSplit(stockSplit);
			            	historyRow.setDividend(dividend);
	
		                }
		                // finally, append
		            	history.add(historyRow);
		            }
	            	else {
		            	history.add(new HistoricalTimeSeries());
	            	}
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