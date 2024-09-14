// Here we are gathering all the methods useful to get the financial data from Yahoo finance

package financialData;

import java.io.IOException;
import java.util.ArrayList;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

public class yfinanceAPI {
	
	public static Stock getStockFromYahooFinance(String ticker) throws IOException {
		Stock stock = YahooFinance.get(ticker);
		return stock;
	}
	
	public ArrayList<Stock> getStocksFromYahooFinance (ArrayList<String> tickerList) throws IOException {
		ArrayList<Stock> stocksFromYF = new ArrayList<Stock>();
		for (String singleTicker : tickerList) {
			Stock singleStock = this.getStockFromYahooFinance(singleTicker);
			stocksFromYF.add(singleStock);
		}
		return stocksFromYF;
	}
	
}