package financialData;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import Objects.HistoricalTimeSeries;

public class yfinanceScraper {
	
	// Here we are Trying to scrape some useful data from Yahoo Finance
	 public static HistoricalTimeSeries getHistoricalValues (String ticker) {
		 
		 HistoricalTimeSeries history = new HistoricalTimeSeries();
	        try {
	            // Get the URL of the page we want to scrape
	            String url = "https://finance.yahoo.com/quote/" + ticker + "/history/";
	            Document document = Jsoup.connect(url).get();
	            
	            // Work on Extracting the data that we want
	            Elements rowsWithTag = document.select("tr.yf-ewueuo");
	            	            
	            for (Element element : rowsWithTag) {
	            	System.out.println(element.text());
	            }
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        
	        return history;
	    }
	
}