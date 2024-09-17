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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import javax.xml.crypto.Data;

@SpringBootTest
@AutoConfigureMockMvc
class APITest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetAPIData() throws Exception {
        mockMvc.perform(get("/api/status").param("status", "API Passage is Working Correctly!"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
    
  @Test
  public void getStockHistory() throws Exception {
   mockMvc.perform(get("/api/history").param("ticker", "AMZN"))
             .andExpect(MockMvcResultMatchers.status().isOk());
  }
    
}