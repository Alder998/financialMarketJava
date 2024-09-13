package Objects;

import java.time.OffsetDateTime;

public class Stock {
	
	private String ticker;
	private String name;
	private StockInformation information;
	private float dividend;
	private OffsetDateTime lastDividendDate;
	private float stockSplit;
	private OffsetDateTime lastSplitDate;
	private float price;
	
}