package com.crio.warmup.stock.quotes;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;
import com.crio.warmup.stock.dto.AlphavantageCandle;
import com.crio.warmup.stock.dto.AlphavantageDailyResponse;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.management.RuntimeErrorException;
import org.springframework.web.client.RestTemplate;

public class AlphavantageService implements StockQuotesService {
  private RestTemplate restTemplate;
  public AlphavantageService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to) throws JsonProcessingException, StockQuoteServiceException {
    // TODO Auto-generated method stub

    String url = buildUrl(symbol);
    List<Candle> candleList = null;
    try{
    String response = restTemplate.getForObject(url, String.class);
    if(response == null){
      throw new StockQuoteServiceException("null response");
    }
    System.out.println(response); //newly added
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    AlphavantageDailyResponse dailyResponse = null;
    try{
       dailyResponse = objectMapper.readValue(response, AlphavantageDailyResponse.class);
    }
    catch(JsonProcessingException e){
      throw new StockQuoteServiceException("Exception while parsing");
    }
    
    Map<LocalDate,AlphavantageCandle> candles = dailyResponse.getCandles();
    if(candles == null){
      throw new StockQuoteServiceException("invalid response");
    }
    candles.forEach((key,value) -> value.setDate(key));
    Map<LocalDate,AlphavantageCandle> finalCandles = candles.entrySet().stream().filter(k -> (k.getKey().compareTo(from) >= 0 && k.getKey().compareTo(to) <= 0))
    .collect(Collectors.toMap(k ->k.getKey(), k -> k.getValue()));
   // Map<LocalDate, AlphavantageCandle> finalCandles = candles.entrySet().stream().filter(e->e.getKey().isAfter(from)).filter(e->e.getKey().isBefore(to)).collect(Collectors.toMap());
   
   candleList = finalCandles.entrySet().stream().map(Map.Entry :: getValue).collect(Collectors.toList());
   Collections.sort(candleList, Comparator.comparing(Candle::getDate));
  }
  catch(RuntimeErrorException e){
    e.printStackTrace();
  }
   return candleList;
  }



  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement the StockQuoteService interface as per the contracts. Call Alphavantage service
  //  to fetch daily adjusted data for last 20 years.
  //  Refer to documentation here: https://www.alphavantage.co/documentation/
  //  --
  //  The implementation of this functions will be doing following tasks:
  //    1. Build the appropriate url to communicate with third-party.
  //       The url should consider startDate and endDate if it is supported by the provider.
  //    2. Perform third-party communication with the url prepared in step#1
  //    3. Map the response and convert the same to List<Candle>
  //    4. If the provider does not support startDate and endDate, then the implementation
  //       should also filter the dates based on startDate and endDate. Make sure that
  //       result contains the records for for startDate and endDate after filtering.
  //    5. Return a sorted List<Candle> sorted ascending based on Candle#getDate
  //  IMP: Do remember to write readable and maintainable code, There will be few functions like
  //    Checking if given date falls within provided date range, etc.
  //    Make sure that you write Unit tests for all such functions.
  //  Note:
  //  1. Make sure you use {RestTemplate#getForObject(URI, String)} else the test will fail.
  //  2. Run the tests using command below and make sure it passes:
  //    ./gradlew test --tests AlphavantageServiceTest
  //CHECKSTYLE:OFF
    //CHECKSTYLE:ON
  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  1. Write a method to create appropriate url to call Alphavantage service. The method should
  //     be using configurations provided in the {@link @application.properties}.
  //  2. Use this method in #getStockQuote.

  private String buildUrl(String symbol){
    
    String url = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol="+
                    symbol+
                    "&outputsize=full&apikey="+getTokken();

    return url;

  }
  private String getTokken(){
    return "9PL8V7OBB9QISCUU";
  }



  // TODO: CRIO_TASK_MODULE_EXCEPTIONS
  //   1. Update the method signature to match the signature change in the interface.
  //   2. Start throwing new StockQuoteServiceException when you get some invalid response from
  //      Alphavantage, or you encounter a runtime exception during Json parsing.
  //   3. Make sure that the exception propagates all the way from PortfolioManager, so that the
  //      external user's of our API are able to explicitly handle this exception upfront.
  //CHECKSTYLE:OFF

}

