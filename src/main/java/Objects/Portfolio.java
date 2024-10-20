package Objects;

import java.util.ArrayList;

public class Portfolio {
	
	private ArrayList<String> tickers;
	private ArrayList<Float> weights;
	private String mainAssetClass;
	
	public ArrayList<String> getTickers() {
		return tickers;
	}
	public void setTickers(ArrayList<String> tickers) {
		this.tickers = tickers;
	}
	public ArrayList<Float> getWeights() {
		return weights;
	}
	public void setWeights(ArrayList<Float> weights) {
		this.weights = weights;
	}
	public String getMainAssetClass() {
		return mainAssetClass;
	}
	public void setMainAssetClass(String mainAssetClass) {
		this.mainAssetClass = mainAssetClass;
	}
	
}