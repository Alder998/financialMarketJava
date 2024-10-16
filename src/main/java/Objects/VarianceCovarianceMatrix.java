package Objects;

import java.util.ArrayList;

public class VarianceCovarianceMatrix {
	
	private String period;
	private ArrayList<String> tickers;
	private float[][] varianceCovarianceMatrix;
	
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
	public float[][] getVarianceCovarianceMatrix() {
		return varianceCovarianceMatrix;
	}
	public void setVarianceCovarianceMatrix(float[][] varianceCovarianceMatrix) {
		this.varianceCovarianceMatrix = varianceCovarianceMatrix;
	}
}