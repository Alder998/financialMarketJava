package Objects;

import java.util.ArrayList;

public class Ticker {
	
	private String symbol;
	private ArrayList <HistoricalTimeSeries> history;
	
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public ArrayList<HistoricalTimeSeries> getHistory() {
		return history;
	}
	public void setHistory(ArrayList<HistoricalTimeSeries> history) {
		this.history = history;
	}
	
}