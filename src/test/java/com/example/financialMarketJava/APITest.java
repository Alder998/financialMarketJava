package com.example.financialMarketJava;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.crypto.Data;

@SpringBootTest
@AutoConfigureMockMvc
class APITest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;


    @Test
    public void testGetAPIData() throws Exception {
        mockMvc.perform(get("/api/status").param("status", "API Passage Status: OK"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
    
  //@Test
  public void getStockHistory() throws Exception {
	  MvcResult result = mockMvc.perform(get("/api/ticker").param("ticker", "AAPL").param("period", "40y"))
             .andExpect(MockMvcResultMatchers.status().isOk())
             .andReturn();
   
	   // Get the JSON body as String
	   String responseBody = result.getResponse().getContentAsString();
	   
       // Deserialize and serialize again the JSON file to get the JSON Formatted
       Object json = objectMapper.readValue(responseBody, Object.class);
       String formattedJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
       
	   // Print the JSON Body (optional)
	   // System.out.println("Body of the API: " + formattedJson);
  }
  
  //@Test
  public void calculateAverageReturns() throws Exception {
	  String ticker = "AAPL";
	  String period = "1mo";
	  MvcResult result = mockMvc.perform(get("/api/returns").param("ticker", ticker).param("period", period))
             .andExpect(MockMvcResultMatchers.status().isOk())
             .andReturn();
   
	   // Get the JSON body as String
	   String responseBody = result.getResponse().getContentAsString();
	   //System.out.println("Average daily Returns for Ticker " + ticker + " and Period " + period + ": " + responseBody + " %");
  }
  
  // Generalized Test on returns
  //@Test
  public void generalizedReturnsTest() throws Exception {
	  
	  ArrayList<String> tickers = new ArrayList<>(Arrays.asList("AAPL", "ISP.MI", "STLA", "CBK.DE", "IBE.MC"));
	  ArrayList<String> periods = new ArrayList<>(Arrays.asList("5y", "10y", "20y"));
	  System.out.println("---GENERALIZED RETURN CONTROL OVER " + tickers.size() * periods.size() + " DOWNLOADS---");

	  for (String ticker : tickers) {
		  for (String period : periods) {
			  MvcResult result = mockMvc.perform(get("/api/returns").param("ticker", ticker).param("period", period))
			         .andExpect(MockMvcResultMatchers.status().isOk())
			         .andReturn();
			
			   // Get the JSON body as String
			   String responseBody = result.getResponse().getContentAsString();
			   System.out.println("Average daily Returns for Ticker " + ticker + " and Period " + period +
					   ": " + responseBody + " %");
		  }
	  }
  }
  
  //@Test
  public void generalizedReturnsSTDTest() throws Exception {
	  
	  ArrayList<String> tickers = new ArrayList<>(Arrays.asList("AAPL", "ISP.MI", "STLA", "CBK.DE", "IBE.MC"));
	  ArrayList<String> periods = new ArrayList<>(Arrays.asList("20y"));
	  System.out.println("---GENERALIZED STD DEVIATION CONTROL OVER " + tickers.size() * periods.size() + " DOWNLOADS---");

	  for (String ticker : tickers) {
		  for (String period : periods) {
			  MvcResult result = mockMvc.perform(get("/api/std").param("ticker", ticker).param("period", period))
			         .andExpect(MockMvcResultMatchers.status().isOk())
			         .andReturn();
			
			   // Get the JSON body as String
			   String responseBody = result.getResponse().getContentAsString();
			   System.out.println("Std for Ticker " + ticker + " and Period " + period +
					   ": " + responseBody);
		  }
	  }
  }
  
  //@Test
  public void calculateCovariance() throws Exception {
	  String ticker1 = "AAPL";
	  String ticker2 = "ISP.MI";
	  String period = "20y";
	  String fromCached = "false";
	  MvcResult result = mockMvc.perform(get("/api/covariance")
			  .param("ticker1", ticker1)
			  .param("ticker2", ticker2)
			  .param("period", period)
			  .param("fromCached", fromCached)
			  )
             .andExpect(MockMvcResultMatchers.status().isOk())
             .andReturn();
   
	   // Get the JSON body as String
	   String responseBody = result.getResponse().getContentAsString();
	   System.out.println("Covariance between Ticker: " + ticker1 +
			   " and Ticker: " + ticker2 +
			   " and Period " + period + ": " + responseBody);
  }
  
  //@Test
  public void calculateCovariances() throws Exception {
	  String ticker1 = "AAPL";
	  ArrayList<String> tickers = new ArrayList<String>();
	  tickers.add("AMZN");
	  tickers.add("NFLX");
	  tickers.add("JPM");
	  tickers.add("GOOG");
      String tickersParam = String.join(",", tickers);
	  String period = "20y";
	  String fromCached = "false";
	  MvcResult result = mockMvc.perform(get("/api/covariances")
			  .param("ticker1", ticker1)
			  .param("tickers", tickersParam)
			  .param("period", period)
			  .param("fromCached", fromCached)
			  )
             .andExpect(MockMvcResultMatchers.status().isOk())
             .andReturn();
   
	   // Get the JSON body as String
	   String responseBody = result.getResponse().getContentAsString();
	   System.out.println(responseBody);
  }
  
  //@Test
  public void generateVarianceCovarianceMatrix() throws Exception {
	  ArrayList<String> tickers = new ArrayList<String>();
	  tickers.add("AAPL");
	  tickers.add("ISP.MI");
	  tickers.add("STLA");
	  tickers.add("CBK.DE");
	  tickers.add("IBE.MC");
      String tickersParam = String.join(",", tickers);
	  String period = "20y";
	  String fromCached = "false";
	  MvcResult result = mockMvc.perform(get("/api/varianceCovarianceMatrix")
			  .param("tickers", tickersParam)
			  .param("period", period)
			  .param("fromCached", fromCached)
			  )
             .andExpect(MockMvcResultMatchers.status().isOk())
             .andReturn();
   
	   // Get the JSON body as String
	   String responseBody = result.getResponse().getContentAsString();
	   System.out.println(responseBody);
  }
  
  //@Test
  public void createVarianceCovarianceMatrix() throws Exception {
	  ArrayList<String> tickers = new ArrayList<String>();
	  tickers.add("AAPL");
	  tickers.add("ISP.MI");
	  tickers.add("STLA");
	  tickers.add("CBK.DE");
	  tickers.add("IBE.MC");
      String tickersParam = String.join(",", tickers);
	  String period = "20y";
	  String fromCached = "false";
	  String assetClass = "Stocks";
	  MvcResult result = mockMvc.perform(post("/api/createVarianceCovarianceMatrix")
			  .param("tickers", tickersParam)
			  .param("period", period)
			  .param("assetClass", assetClass)
			  .param("fromCached", fromCached)
			  )
             .andExpect(MockMvcResultMatchers.status().isOk())
             .andReturn();
   
	   // Get the JSON body as String
	   String responseBody = result.getResponse().getContentAsString();
	   System.out.println(responseBody);
  }
  
  //@Test
  public void createVarianceCovarianceMatrixAndReturnFromDatabase() throws Exception {
	  String stockIndex = "ustreasurybonds";
	  String subList = "4";
	  String period = "20y";
	  String assetClass = "Bonds";
	  MvcResult result = mockMvc.perform(post("/api/portfolioConstructionComponents")
			  .param("period", period)
			  .param("assetClass", assetClass)
			  .param("stockIndex", stockIndex)
			  .param("subList", subList)
			  )
             .andExpect(MockMvcResultMatchers.status().isOk())
             .andReturn();
   
	   // Get the JSON body as String
	   String responseBody = result.getResponse().getContentAsString();
	   System.out.println(responseBody);
  }
  
  //@Test
  public void updateVarianceCovarianceMatrixFromDatabase() throws Exception {
	  String stockIndex = "SP500";
	  String subList = "20";
	  String period = "20y";
	  String assetClass = "Stocks";
	  MvcResult result = mockMvc.perform(post("/api/updateVarianceCovarianceMatrix")
			  .param("period", period)
			  .param("stockIndex", stockIndex)
			  .param("subList", subList)
			  .param("assetClass", assetClass))
             .andExpect(MockMvcResultMatchers.status().isOk())
             .andReturn();
   
	   // Get the JSON body as String
	   String responseBody = result.getResponse().getContentAsString();
	   System.out.println(responseBody);
  }
  
  //@Test
  public void optimizeStockPortfolio() throws Exception {
	  String period = "20y";
	  String assetClass = "Stocks";
	  MvcResult result = mockMvc.perform(post("/api/optimizeStockPortfolio")
			  .param("period", period)
			  .param("assetClass", assetClass))
             .andExpect(MockMvcResultMatchers.status().isOk())
             .andReturn();
	   // Get the JSON body as String
	   String responseBody = result.getResponse().getContentAsString();
	   System.out.println(responseBody);
  }
  
  @Test
  public void optimizeStocksAndBondsPortfolio() throws Exception {
	  String period = "20y";
	  ArrayList<String> assetClasses = new ArrayList<String>();
	  assetClasses.add("Stocks");
	  assetClasses.add("Bonds");
      String assetClassesString = String.join(",", assetClasses);
	  MvcResult result = mockMvc.perform(post("/api/optimizeStocksAndBondsPortfolio")
			  .param("period", period)
			  .param("assetClasses", assetClassesString))
             .andExpect(MockMvcResultMatchers.status().isOk())
             .andReturn();
	   // Get the JSON body as String
	   String responseBody = result.getResponse().getContentAsString();
	   System.out.println(responseBody);
  }
  
}