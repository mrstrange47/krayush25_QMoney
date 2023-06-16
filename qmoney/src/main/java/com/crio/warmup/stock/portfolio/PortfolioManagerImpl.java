
package com.crio.warmup.stock.portfolio;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.crio.warmup.stock.quotes.StockQuotesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;
import java.util.Collections;


public class PortfolioManagerImpl implements PortfolioManager {

 private StockQuotesService stockQuotesService;
 
 private RestTemplate restTemplate;

  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public PortfolioManagerImpl(StockQuotesService stockQuotesService){
    this.stockQuotesService = stockQuotesService;
  }
  //TODO: CRIO_TASK_MODULE_REFACTOR
  // 1. Now we want to convert our code into a module, so we will not call it from main anymore.
  //    Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  //    into #calculateAnnualizedReturn function here and ensure it follows the method signature.
  // 2. Logic to read Json file and convert them into Objects will not be required further as our
  //    clients will take care of it, going forward.

  // Note:
  // Make sure to exercise the tests inside PortfolioManagerTest using command below:
  // ./gradlew test --tests PortfolioManagerTest

  //CHECKSTYLE:OFF
  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> trades, LocalDate endDate) throws StockQuoteServiceException{
    List<AnnualizedReturn> list = new ArrayList<>();
    
    for(PortfolioTrade trade : trades){
      List<Candle> candle = null;
      try {
        candle = this.stockQuotesService.getStockQuote(trade.getSymbol(), trade.getPurchaseDate(), endDate);
      } catch (JsonProcessingException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }   
      AnnualizedReturn annualizedReturn = 
              calcAnnualizedReturns(endDate,trade,getOpenPriceOnStartDate(candle),getClosePriceOnEndDate(candle));

      list.add(annualizedReturn);
    }

    Collections.sort(list,Comparator.comparingDouble(AnnualizedReturn::getAnnualizedReturn));
    Collections.reverse(list);
    return list;
  }


  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Extract the logic to call Tiingo third-party APIs to a separate function.
  //  Remember to fill out the buildUri function and use that.


  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException {
         RestTemplate restTemplate = new RestTemplate();
         String url = buildUri(symbol, from, to);
         TiingoCandle[] tinCandles = restTemplate.getForObject(url,TiingoCandle[].class);

     return Arrays.asList(tinCandles);
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
      String uriTemplate = "https://api.tiingo.com/tiingo/daily/" +symbol+"/prices?startDate="
      +startDate.toString()+"&endDate="+endDate.toString()+"&token="+getToken();
      return uriTemplate;

  }

  //::::::::::::::::::::::::::::Form PortfolioManagerApplication::::::::::::::::::::
    public static Double getOpenPriceOnStartDate(List<Candle> candles) {
    double startingPrice = candles.get(0).getOpen();
    return startingPrice;
    }

   public static Double getClosePriceOnEndDate(List<Candle> candles) {
      double closingPrice = candles.get(candles.size()-1).getClose();
      return closingPrice;
  }

  private String  getToken(){
    return "5281d0e1c35d77b08fa4c8a710a34cd45ca5d7c9";
  }

  private List<Candle> fetchingCandles(PortfolioTrade trade, LocalDate endDate, String token) {
    RestTemplate restTemplate = new RestTemplate();
    String url = buildUri(trade.getSymbol(),trade.getPurchaseDate(),endDate);
    TiingoCandle[] candle = restTemplate.getForObject(url, TiingoCandle[].class);
    

    return Arrays.asList(candle);
  }

  public static AnnualizedReturn calcAnnualizedReturns(LocalDate endDate,PortfolioTrade trade, Double buyPrice, Double sellPrice) {
      
      double totolReturns =(sellPrice-buyPrice)/buyPrice;
      long noOfDays = ChronoUnit.DAYS.between(trade.getPurchaseDate(), endDate);
      double noOfYears = (double)noOfDays/(365.0);
      double annualReturn = Math.pow(1+totolReturns,(1/noOfYears))-1;


      return new AnnualizedReturn(trade.getSymbol(), annualReturn, totolReturns);
  }

  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturnParallel(List<PortfolioTrade> portfolioTrades, LocalDate endDate, int numThreads)
      throws InterruptedException, StockQuoteServiceException {
    // TODO Auto-generated method stub
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        List<AnnualizedReturn> list = new ArrayList<>();
        List<Future<AnnualizedReturn>> futureList = new ArrayList<>();

        for(PortfolioTrade trade : portfolioTrades){
          Future<AnnualizedReturn> future = executorService.submit(new Task(trade,endDate,this.stockQuotesService));
          futureList.add(future);
        }

        for(Future<AnnualizedReturn> future: futureList){
          
            try {
              AnnualizedReturn response = future.get();
              list.add(response);
            } catch (ExecutionException e) {
              // TODO Auto-generated catch block
              throw new StockQuoteServiceException("invalid response");
            }
         
        }
        Collections.sort(list,Comparator.comparingDouble(AnnualizedReturn::getAnnualizedReturn));
        Collections.reverse(list);


    return list;
  }

  public static class Task implements Callable<AnnualizedReturn>{
    private PortfolioTrade portfolioTrade;
    private LocalDate endDate;
    private StockQuotesService stockQuotesService;
    public Task(PortfolioTrade portfolioTrade, LocalDate endDate, StockQuotesService service){
      this.portfolioTrade = portfolioTrade;
      this.endDate = endDate;
      this.stockQuotesService = service;
    }
    @Override
    public AnnualizedReturn call() throws JsonProcessingException, StockQuoteServiceException{
      List<Candle> candles = this.stockQuotesService.getStockQuote(this.portfolioTrade.getSymbol()
      , this.portfolioTrade.getPurchaseDate(), this.endDate);
      AnnualizedReturn annualizedReturn = 
              calcAnnualizedReturns(endDate,this.portfolioTrade,getOpenPriceOnStartDate(candles),getClosePriceOnEndDate(candles));
      return annualizedReturn;
    }
  }

  
  //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

}

