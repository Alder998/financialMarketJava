package Objects;

import java.util.ArrayList;
import java.util.HashMap;

public class CovarianceStructure {
	
	private String ticker;
	private HashMap<String, Float> covariances;
	
	public String getTicker() {
		return ticker;
	}
	public void setTicker(String ticker) {
		this.ticker = ticker;
	}
	public HashMap<String, Float> getCovariances() {
		return covariances;
	}
	public void setCovariances(HashMap<String, Float> covariances) {
		this.covariances = covariances;
	}
	
}