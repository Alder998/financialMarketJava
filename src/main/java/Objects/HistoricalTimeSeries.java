package Objects;

import java.time.OffsetDateTime;

public class HistoricalTimeSeries {
	
	private String ticker;
	private OffsetDateTime date;
	private float open;
	private float close;
	private float high;
	private float low;
	private float adjClose;
	
	public String getTicker() {
		return ticker;
	}
	public void setTicker(String ticker) {
		this.ticker = ticker;
	}
	public OffsetDateTime getDate() {
		return date;
	}
	public void setDate(OffsetDateTime date) {
		this.date = date;
	}
	public float getOpen() {
		return open;
	}
	public void setOpen(float open) {
		this.open = open;
	}
	public float getClose() {
		return close;
	}
	public void setClose(float close) {
		this.close = close;
	}
	public float getHigh() {
		return high;
	}
	public void setHigh(float high) {
		this.high = high;
	}
	public float getLow() {
		return low;
	}
	public void setLow(float low) {
		this.low = low;
	}
	public float getAdjClose() {
		return adjClose;
	}
	public void setAdjClose(float adjClose) {
		this.adjClose = adjClose;
	}

	
	
}