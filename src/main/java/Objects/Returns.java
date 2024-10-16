package Objects;

import java.util.ArrayList;

public class Returns {
	private String period;
	private ArrayList <String> tickers;
	private ArrayList <Float> returns;
	
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public ArrayList<String> getTickers() {
		return tickers;
	}
	public void setTickers(ArrayList<String> tickers) {
		this.tickers = tickers;
	}
	public ArrayList<Float> getReturns() {
		return returns;
	}
	public void setReturns(ArrayList<Float> returns) {
		this.returns = returns;
	}
	
	
}