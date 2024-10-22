package Objects;

import java.util.ArrayList;

public class Portfolio {
	
	private ArrayList<String> tickers;
	private ArrayList<Float> weights;
	private String mainAssetClass;
	private float avrerageReturns;
	private float variance;
	private String metricsPeriod;
	
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
	public float getAvrerageReturns() {
		return avrerageReturns;
	}
	public void setAvrerageReturns(float avrerageReturns) {
		this.avrerageReturns = avrerageReturns;
	}
	public float getVariance() {
		return variance;
	}
	public void setVariance(float variance) {
		this.variance = variance;
	}
	public String getMetricsPeriod() {
		return metricsPeriod;
	}
	public void setMetricsPeriod(String metricsPeriod) {
		this.metricsPeriod = metricsPeriod;
	}
	
}