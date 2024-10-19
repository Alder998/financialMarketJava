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
		else if (type.toLowerCase().equals("ustreasurybonds")) {
			sql = "SELECT Ticker FROM USTreasuryBonds";
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
	public VarianceCovarianceMatrix getVarianceCovarianceMatrixByPeriodAndAssetClass (String period, String assetClass) throws DataAccessException, JsonMappingException, JsonProcessingException {
		String sql = "SELECT * FROM VarianceCovarianceMatrix WHERE period = ? AND AssetClass = ?";
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
	    }, new Object[]{period, assetClass});

	    // Return the first result or null if no data found
	    return results.isEmpty() ? null : results.get(0);
	}
	
	// Create Method
	public void createCovarianceMatrix (ArrayList<String> tickers, String period, String assetClass, Boolean fromCached) throws DataAccessException {
		String sql = "INSERT INTO VarianceCovarianceMatrix (period, stockNumber, assetClass, tickers, VarianceCovarianceMatrix) VALUES (?,?,?,?,?)";
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
        // get the assetClass
		if (assetClass.toLowerCase().equals("bonds")) {
			assetClass = "Bonds";
		} else if (assetClass.toLowerCase().equals("stocks")) {
			assetClass = "Stocks";
		}
        // Update Command that adds a row to the SQL Table
        jdbcTemplate.update(sql, period, tickers.size(), assetClass, tickersJSON, covarianceMatrixJson);
	}
	
	public void createCovarianceMatrixFromObject (VarianceCovarianceMatrix matrix) {
		// delete Existing
		this.deleteVarianceCovarianceMatrixByPeriod(matrix.getPeriod());
		String sql = "INSERT INTO VarianceCovarianceMatrix (period, stockNumber, tickers, VarianceCovarianceMatrix) VALUES (?,?,?,?)";
		ObjectMapper objectMapper = new ObjectMapper();
        String covarianceMatrixJson = null;
        try {
            covarianceMatrixJson = objectMapper.writeValueAsString(matrix.getVarianceCovarianceMatrix());
        } catch (Exception e) {
            e.printStackTrace();
        }
		// Convert the Ticker Array into a JSON String
        String tickersJSON = null;
        try {
        	tickersJSON = objectMapper.writeValueAsString(matrix.getTickers());
        } catch (Exception e) {
            e.printStackTrace();
        }
        jdbcTemplate.update(sql, matrix.getPeriod(), matrix.getTickers().size(), tickersJSON, covarianceMatrixJson);
	}
	
	public void deleteVarianceCovarianceMatrixByPeriod (String period) {
		String sql = "DELETE FROM VarianceCovarianceMatrix WHERE period = ?";
        jdbcTemplate.update(sql, period);
	}
	
	public void createCovarianceMatrixFromReturns (String period, String assetClass, Boolean fromCached) {
		// create Covariance Matrix based on returns that are in the database
		// get Returns Ticker set
		Returns returnsFromDB = this.getReturnsByPeriodAndAssetClass(period, assetClass);
		ArrayList<String> tickersInDatabase = returnsFromDB.getTickers();
		// create Covariance Matrix and store it in the DB. The returns in the database have not to be
		// null, or infinity. Therefore the matrix will not be affected by these values.
		createCovarianceMatrix (tickersInDatabase, period, assetClass, fromCached);
	}
	
	// CRUD methods to create and Store Returns
	public String createReturn (String ticker, String period, String assetClass) {
		// SQL String
		String sql = "INSERT INTO Returns (period, ticker, assetClass, return) VALUES (?,?,?,?)";
		// Compute average return
		float singleReturn = this.calculateAverageReturns(ticker, period);
		// Update SQL, only if the return is finite, add it to the list
		// Get the assetClass
		if (assetClass.toLowerCase().equals("bonds")) {
			assetClass = "Bonds";
		} else if (assetClass.toLowerCase().equals("stocks")) {
			assetClass = "Stocks";
		}
		ArrayList<String> tickerAdded = new ArrayList<String>();
		if (!Double.isNaN(singleReturn) && !Double.isInfinite(singleReturn)) {
	        jdbcTemplate.update(sql, period, ticker, assetClass, singleReturn);
	        tickerAdded.add(ticker);
		}
		if (tickerAdded.contains(ticker)) {
			return ticker;
		}
		else {
			return null;
		}
	}
	
	public void createReturns (ArrayList<String> tickers, String period, String assetClass) {
		// Iterate through the Ticker's list
		int iteration = 0;
		for (String ticker : tickers) {
			this.createReturn (ticker, period, assetClass);
			// Logging
			System.out.println("Calculating Returns: Processing Ticker " + (iteration + 1) + " Of " + tickers.size() + " Tickers");
			iteration++;
		}
	}
	
	// get method for Returns
	public Returns getReturnsByPeriodAndAssetClass (String period, String assetClass) {
		Returns returnsOutput = new Returns();
		String sql = "SELECT * FROM Returns WHERE period = ? AND assetClass = ?";
		  List<Float> returns = jdbcTemplate.query(
		            sql,
		            (rs, rowNum) -> rs.getFloat("Return"),
		            new Object[]{period, assetClass}
		        );
		  List<String> tickers = jdbcTemplate.query(
		            sql,
		            (rs, rowNum) -> rs.getString("Ticker"),
		            new Object[]{period, assetClass}
		        );
		  // Populate Object
		  returnsOutput.setPeriod(period);
		  returnsOutput.setReturns(new ArrayList<Float>(returns));
		  returnsOutput.setTickers(new ArrayList<String>(tickers));

		return returnsOutput;
	}
	
	// Method to create a massive Variance-Covariance Matrix and return Table
	public void createVarianceCovarianceMatrixAndReturnFromDatabase (String period, String assetClass, String stockIndex, Integer subList) {
		// Get the Stock Index from Database
		ArrayList<String> stockSample = this.getStockSample(stockIndex, subList);
		// if integrate = True, then take the non processed tickers from the sample
		// Compute the main Portfolio Driver Components
		// force to Bonds if the stockSample is the one containing Bonds
		if (stockIndex.toLowerCase().equals("ustreasurybonds")) {
			assetClass = "Bonds";
		}
		this.createReturns(stockSample, period, assetClass);
		this.createCovarianceMatrixFromReturns(period, assetClass, true);
	}
	
	// Deprecated;
	public ArrayList<String> addTickersToExistingReturns(String period, ArrayList<String> tickersToAdd, String assetClass) {
		// get the tickers present in the database of returns
		Returns existingReturns = this.getReturnsByPeriodAndAssetClass(period, assetClass);
		ArrayList<String> tickersNotInDB = new ArrayList<String>();
		for (String ticker : tickersToAdd) {
			if (!existingReturns.getTickers().contains(ticker)) {
				tickersNotInDB.add(ticker);
			}
		}
		int iteration = 0;
		ArrayList<String> tickerAddedToDatabase = new ArrayList<String>();
		for (String ticker1 : tickersNotInDB) {
			// Logging
			System.out.println("Calculating Returns: Processing Ticker " + (iteration + 1) + " Of " + tickersNotInDB.size() + " Tickers");
			String tickerAdded = this.createReturn(ticker1, period, assetClass);
			
			if (tickerAdded != null) {
				tickerAddedToDatabase.add(tickerAdded);
			}
			
			iteration ++;
		}
		return tickerAddedToDatabase;
	}
	
	// Deprecated;
	public VarianceCovarianceMatrix addTickersToExistingVarianceCovarianceMatrix (String period, ArrayList<String> tickersToAdd, String assetClass) throws JsonMappingException, DataAccessException, JsonProcessingException {
		// Instantiate the New Variance Covariance Matrix
		VarianceCovarianceMatrix newVarCovMatrix = new VarianceCovarianceMatrix();
		// get the Matrix to update
		VarianceCovarianceMatrix existingVarCovMatrix = getVarianceCovarianceMatrixByPeriodAndAssetClass(period, assetClass);
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
		int existingSize = existingVarCovMatrix.getTickers().size();
		// Add to the matrix a number of Rows and columns that is Equivalent to the number of New tickers
		float[][] newMatrix = DataClass_Utils.expandMatrix(matrixToUpdate, existingSize + tickerNotPresentInDB.size());
		
		// Compute the covariance among the new tickers and add them to the matrix
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
				newMatrix[existingSize + row][existingSize + column] = covariance;
				// Update columns
				column ++;
			}
			row ++;
		}
		
		// Compute the covariance among the existing tickers and the new ones (computationally Expensive, since the returns are not already stored in the cache)
		// Since the returns may be re-used many times
		int iterationForCache = 0;
		for (String tickerAlreadyPresentInDB : existingVarCovMatrix.getTickers()) {
			// Hopefully, this will store Returns in the Cache
			// Logging
			System.out.println("Caching Returns of Stock: " + iterationForCache +
					" Out of: " + existingVarCovMatrix.getTickers().size() + " Tickers");
			Calculations.getReturnDiff(tickerAlreadyPresentInDB, period);
			iterationForCache ++;
		}
		
		// Now, separately, compute the covariances
		// Update columns: New columns, Existing rows
		System.out.println("UPDATING VARIANCE-COVARIANCE MATRIX: EXISTING ROWS, NEW COLUMNS");
		int row1 = 0;
		for (String newTicker : tickerNotPresentInDB) {
			CovarianceStructure covarianceWithExistingTickers = Calculations.computeReturnCovariances(newTicker, existingVarCovMatrix.getTickers(), period, true);
			int column1 = 0;
			for (String tickerCovariance : tickerNotPresentInDB) {
				float covariance = covarianceWithExistingTickers.getCovariances().get(tickerCovariance);
				newMatrix[row1][existingSize + column1] = covariance;
				// Update columns
				column1 ++;
			}
			row1 ++;
		}
		// Update columns: New rows, Existing columns
		System.out.println("UPDATING VARIANCE-COVARIANCE MATRIX: EXISTING COLUMNS, NEW ROWS");
		int row2 = 0;
		for (String newTicker : tickerNotPresentInDB) {
			CovarianceStructure covarianceWithExistingTickers = Calculations.computeReturnCovariances(newTicker, existingVarCovMatrix.getTickers(), period, true);
			int column2 = 0;
			for (String tickerCovariance : tickerNotPresentInDB) {
				float covariance = covarianceWithExistingTickers.getCovariances().get(tickerCovariance);
				newMatrix[row2 + existingSize][column2] = covariance;
				// Update columns
				column2 ++;
			}
			row2 ++;
		}
		
		// Populate Object
		ArrayList<String> allTickers = existingVarCovMatrix.getTickers();
		allTickers.addAll(tickerNotPresentInDB);
		newVarCovMatrix.setVarianceCovarianceMatrix(newMatrix);
		newVarCovMatrix.setTickers(allTickers);
		newVarCovMatrix.setPeriod(period);
		// handle the Database
		this.createCovarianceMatrixFromObject(newVarCovMatrix);
		return newVarCovMatrix;
	}
	
	// Deprecated;
	public void addTickersToReturnsAndVarianceCovarianceMatrixFromDatabase (String period, String stockIndex, Integer subList, String assetClass) throws JsonMappingException, DataAccessException, JsonProcessingException {
		// Get the Stock Index from Database
		ArrayList<String> stockSample = this.getStockSample(stockIndex, subList);
		// if integrate = True, then take the non processed tickers from the sample
		// Compute the main Portfolio Driver Components
		ArrayList<String> tickersReturn = this.addTickersToExistingReturns(period, stockSample, assetClass);
		this.addTickersToExistingVarianceCovarianceMatrix(period, tickersReturn, assetClass);
	}
	
	// Optimization Method based just on Variance Covariance Matrix
	public void optimizeStockPortfolio (String period, String assetClass) throws JsonMappingException, DataAccessException, JsonProcessingException {
		// get Variance-Covariance Matrix
		VarianceCovarianceMatrix varCovMat = this.getVarianceCovarianceMatrixByPeriodAndAssetClass(period, assetClass);
		Calculations.optimizeStockPortfolio(varCovMat);
	}
	
}