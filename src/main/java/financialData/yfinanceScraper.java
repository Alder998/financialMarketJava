package financialData;

import java.time.OffsetDateTime;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.example.financialMarketJava.DataClass_Utils;

import Objects.HistoricalTimeSeries;

public class yfinanceScraper {
	
	// Here we are Trying to scrape some useful data from Yahoo Finance
	 public static ArrayList<HistoricalTimeSeries> getHistoricalValues (String ticker) {
		 
		 ArrayList<HistoricalTimeSeries> history = new ArrayList<HistoricalTimeSeries>();
	        try {
	            // Get the URL of the page we want to scrape
	            String url = "https://finance.yahoo.com/quote/" + ticker + "/history/";
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

	                // finally, append
	            	history.add(historyRow);
	            }
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return history;
	    }
	
}