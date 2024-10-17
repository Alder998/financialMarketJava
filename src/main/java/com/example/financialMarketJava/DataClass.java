package com.example.financialMarketJava;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import Objects.CovarianceStructure;
import Objects.HistoricalTimeSeries;
import Objects.Returns;
import Objects.Ticker;
import Objects.VarianceCovarianceMatrix;
import financialData.yfinanceAPI;
import financialData.yfinanceScraper;
import yahoofinance.Stock;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;
import org.springframework.jdbc.core.JdbcTemplate;

// The Data Class is where all the main operations will be performed

@Component
public class DataClass {
	
    @Autowired
    private JdbcTemplate jdbcTemplate;
	
	// This method may be deleted, when the project structure would be set up, and therefore will be sure
	// that the project is working
	public String displayStatus(String status) {
		System.out.println(status);
		return status;
	}
	
	// Try the Methods from the YF API
	
	// Deprecated Method: Yahoo Finance is closed this API
	public ArrayList<HistoricalQuote> getStockHistory(String ticker, String timeSpan, String interval) throws IOException {
		
		ArrayList<HistoricalQuote> history = new ArrayList<HistoricalQuote>();
		try {
			Stock stock = yfinanceAPI.getStockFromYahooFinance(ticker);
			
			// We need to set the date parameters as they should be set
			Calendar calendarTimeSpan = DataClass_Utils.getDateFromString(timeSpan);
			
			// Now, cover the interval
			ArrayList<String> timeComponents = DataClass_Utils.getTimeAndUnitFromString(interval);
			String intervalString = timeComponents.get(0).toString();
			
			Interval intervalObj = null;
			if (intervalString == "y") {
				intervalObj = Interval.DAILY;
			}
			if (intervalString == "mo") {
				intervalObj = Interval.MONTHLY;
			}
			if (intervalString == "d") {
				intervalObj = Interval.DAILY;
			}
			
			List<HistoricalQuote> historyList = stock.getHistory(calendarTimeSpan, intervalObj);
			// Change from List to ArrayList for simplicity
			history.addAll(historyList);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return history;
	}
	
	public ArrayList<HistoricalTimeSeries> getStockHistoryFromScraping (String ticker, String period) {
		
		return yfinanceScraper.getHistoricalValues(ticker, period);
	}
	
	public Ticker getTicker (String symbol, String period) {
		Ticker ticker = new Ticker();
		
		ticker.setSymbol(symbol);
		ticker.setHistory(this.getStockHistoryFromScraping(symbol, period));
		
		return ticker;
	}
	
	public float calculateAverageReturns (String ticker, String period) {
		return Calculations.computeAverageReturn(ticker, period);
	}
	
	public double calculateStdDeviation (String ticker, String period) {
		return Calculations.computeReturnStdDeviation(ticker, period);
	}
	
	public float calculateCovariance (String ticker1, String ticker2, String period, Boolean fromCached) {
		return Calculations.computeReturnCovariance(ticker1, ticker2, period, fromCached);
	}
	
	public CovarianceStructure calculateCovariances (String ticker1, ArrayList<String> tickers, String period, Boolean fromCached) {
		return Calculations.computeReturnCovariances(ticker1, tickers, period, fromCached);
	}
	
	public float[][] generateVarianceCovarianceMatrix (ArrayList<String> tickers, String period, Boolean fromCached) {
		return Calculations.getVarianceCovarianceMatrix(tickers, period, fromCached);
	}
	
	// Methods to get a stock Sample from SQL
	public ArrayList<String> getStockSample (String type, Integer subList) {
		String sql = "";
		if (type.toLowerCase().equals("allstocks")) {
			sql = "SELECT Ticker FROM AllStocksTraded";
		}
		else if (type.toLowerCase().equals("sp500")) {
			sql = "SELECT Ticker FROM SP500Ticker";
		}
	  List<String> tickers = jdbcTemplate.query(
	            sql, 
	            (rs, rowNum) -> rs.getString("Ticker")
	        );
	
	  // Convert to ArrayList<String>
	  ArrayList<String> tickerList = new ArrayList<>(tickers);
	  if (subList!=null) {
		  tickerList = new ArrayList<String>(tickerList.subList(0, subList));
	  }
	  return tickerList;
	}
	
	// CRUD methods to get the Variance-Covariance Matrix
	// get Method
	public VarianceCovarianceMatrix getVarianceCovarianceMatrixByPeriod (String period) throws DataAccessException, JsonMappingException, JsonProcessingException {
		String sql = "SELECT * FROM VarianceCovarianceMatrix WHERE period = ?";
		// Get Variance Covariance Matrix
	    List<VarianceCovarianceMatrix> results = jdbcTemplate.query(sql, (rs, rowNum) -> {
	        VarianceCovarianceMatrix varCovObject = new VarianceCovarianceMatrix();
	        ObjectMapper objectMapper = new ObjectMapper();

	        // Deserialize covariance matrix
	        float[][] covarianceMatrix = null;
			try {
				covarianceMatrix = objectMapper.readValue(rs.getString("varianceCovarianceMatrix"), float[][].class);
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}

	        // Deserialize tickers
	        List<String> tickers = null;
			try {
				tickers = objectMapper.readValue(rs.getString("tickers"), new TypeReference<List<String>>() {});
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}

	        // Populate the object
	        varCovObject.setVarianceCovarianceMatrix(covarianceMatrix);
	        varCovObject.setTickers(new ArrayList<String>(tickers));
	        varCovObject.setPeriod(rs.getString("period"));

	        return varCovObject;
	    }, new Object[]{period});

	    // Return the first result or null if no data found
	    return results.isEmpty() ? null : results.get(0);
	}
	
	// Create Method
	public void createCovarianceMatrix (ArrayList<String> tickers, String period, Boolean fromCached) throws DataAccessException {
		String sql = "INSERT INTO VarianceCovarianceMatrix (period, stockNumber, tickers, VarianceCovarianceMatrix) VALUES (?,?,?,?)";
		float[][] covarianceMatrix = new float[tickers.size()][tickers.size()];
		covarianceMatrix = generateVarianceCovarianceMatrix(tickers, period, fromCached);
		// Convert the variance Covariance Matrix into a JSON String
		ObjectMapper objectMapper = new ObjectMapper();
        String covarianceMatrixJson = null;
        try {
            covarianceMatrixJson = objectMapper.writeValueAsString(covarianceMatrix);
        } catch (Exception e) {
            e.printStackTrace();
        }
		// Convert the Ticker Array into a JSON String
        String tickersJSON = null;
        try {
        	tickersJSON = objectMapper.writeValueAsString(tickers);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Update Command that adds a row to the SQL Table
        jdbcTemplate.update(sql, period, tickers.size(), tickersJSON, covarianceMatrixJson);
	}
	
	public void deleteVarianceCovarianceMatrixByPeriod (String period) {
		String sql = "DELETE FROM VarianceCovarianceMatrix WHERE period = ?";
        jdbcTemplate.update(sql, period);
	}
	
	public void createCovarianceMatrixFromReturns (String period, Boolean fromCached) {
		// create Covariance Matrix based on returns that are in the database
		// get Returns Ticker set
		Returns returnsFromDB = this.getReturnsByPeriod(period);
		ArrayList<String> tickersInDatabase = returnsFromDB.getTickers();
		// create Covariance Matrix and store it in the DB. The returns in the database have not to be
		// null, or infinity. Therefore the matrix will not be affected by these values.
		createCovarianceMatrix (tickersInDatabase, period, fromCached);
	}
	
	// CRUD methods to create and Store Returns
	public void createReturn (String ticker, String period) {
		// SQL String
		String sql = "INSERT INTO Returns (period, ticker, return) VALUES (?,?,?)";
		// Compute average return
		float singleReturn = this.calculateAverageReturns(ticker, period);
		// Update SQL, only if the return is finite
		if (!Double.isNaN(singleReturn) && !Double.isInfinite(singleReturn)) {
	        jdbcTemplate.update(sql, period, ticker, singleReturn);
		}
	}
	
	public void createReturns (ArrayList<String> tickers, String period) {
		// Iterate through the Ticker's list
		int iteration = 0;
		for (String ticker : tickers) {
			this.createReturn (ticker, period);
			// Logging
			System.out.println("Calculating Returns: Processing Ticker " + (iteration + 1) + " Of " + tickers.size() + " Tickers");
			iteration++;
		}
	}
	
	// get method for Returns
	public Returns getReturnsByPeriod (String period) {
		Returns returnsOutput = new Returns();
		String sql = "SELECT * FROM Returns WHERE period = ?";
		  List<Float> returns = jdbcTemplate.query(
		            sql,
		            (rs, rowNum) -> rs.getFloat("Return"),
		            new Object[]{period}
		        );
		  List<String> tickers = jdbcTemplate.query(
		            sql,
		            (rs, rowNum) -> rs.getString("Ticker"),
		            new Object[]{period}
		        );
		  // Populate Object
		  returnsOutput.setPeriod(period);
		  returnsOutput.setReturns(new ArrayList<Float>(returns));
		  returnsOutput.setTickers(new ArrayList<String>(tickers));

		return returnsOutput;
	}
	
	// Method to create a massive Variance-Covariance Matrix and return Table
	public void createVarianceCovarianceMatrixAndReturnFromDatabase (String period, String stockIndex, Integer subList) {
		// Get the Stock Index from Database
		ArrayList<String> stockSample = this.getStockSample(stockIndex, subList);
		// if integrate = True, then take the non processed tickers from the sample
		// Compute the main Portfolio Driver Components
		this.createReturns(stockSample, period);
		this.createCovarianceMatrixFromReturns(period, true);
	}
	
	public void addTickersToExistingReturns(String period, ArrayList<String> tickersToAdd) {
		// get the tickers present in the database of returns
		Returns existingReturns = this.getReturnsByPeriod(period);
		ArrayList<String> tickersNotInDB = new ArrayList<String>();
		for (String ticker : tickersToAdd) {
			if (!existingReturns.getTickers().contains(ticker)) {
				tickersNotInDB.add(ticker);
			}
		}
		for (String ticker1 : tickersNotInDB) {
			this.createReturn(ticker1, period);
		}
	}
	
	public VarianceCovarianceMatrix addTickersToExistingVarianceCovarianceMatrix (String period, ArrayList<String> tickersToAdd) throws JsonMappingException, DataAccessException, JsonProcessingException {
		// Instantiate the New Variance Covariance Matrix
		VarianceCovarianceMatrix newVarCovMatrix = new VarianceCovarianceMatrix();
		// get the Matrix to update
		VarianceCovarianceMatrix existingVarCovMatrix = getVarianceCovarianceMatrixByPeriod(period);
		// In the ticker we must provide the new Ticker that we want to add to the Matrix
		// First of all, select the ticker that are not already present in the Matrix
		ArrayList<String> tickerNotPresentInDB = new ArrayList<String>();
		for (String tickerToAdd : tickersToAdd) {
			if (!existingVarCovMatrix.getTickers().contains(tickerToAdd)) {
				tickerNotPresentInDB.add(tickerToAdd);
			}
		}
		// Now we can Proceed to update the Matrix
		float[][] matrixToUpdate = existingVarCovMatrix.getVarianceCovarianceMatrix();
		int existingSize = existingVarCovMatrix.getTickers().size() - 1;
		// Add to the matrix a number of Rows and columns that is Equivalent to the number of New tickers
		int row = 0;
		for (String ticker : tickerNotPresentInDB) {
			// Add some logging
			System.out.println("Computing Variance-Covariance Matrix for Ticker: " + ticker +
					" - Processing stock: " + (row + 1) + " out of " + tickerNotPresentInDB.size() + " Tickers" );
			CovarianceStructure singleCovarianceStructure = Calculations.computeReturnCovariances(ticker, tickerNotPresentInDB, period, true);
			// Unpack the single covariances, update the row number
			int column = 0;
			for (String tickerCovariance : tickerNotPresentInDB) {
				float covariance = singleCovarianceStructure.getCovariances().get(tickerCovariance);
				matrixToUpdate[existingSize + row][existingSize + column] = covariance;
				// Update columns
				column ++;
			}
			row ++;
		}
		ArrayList<String> allTickers = existingVarCovMatrix.getTickers();
		allTickers.addAll(tickerNotPresentInDB);
		// Populate Object
		newVarCovMatrix.setVarianceCovarianceMatrix(matrixToUpdate);
		newVarCovMatrix.setTickers(allTickers);
		
		return newVarCovMatrix;
	}
	
	public void updateVarianceCovarianceMatrixFromDatabase (String period, String stockIndex, Integer subList) throws JsonMappingException, DataAccessException, JsonProcessingException {
		// Get the Stock Index from Database
		ArrayList<String> stockSample = this.getStockSample(stockIndex, subList);
		// if integrate = True, then take the non processed tickers from the sample
		// Compute the main Portfolio Driver Components
		this.addTickersToExistingReturns(period, stockSample);
		this.addTickersToExistingVarianceCovarianceMatrix(period, stockSample);
	}
	
}