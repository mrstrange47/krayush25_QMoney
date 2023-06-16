package com.crio.warmup.stock;

import com.crio.warmup.stock.dto.*;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.crio.warmup.stock.portfolio.PortfolioManager;
import com.crio.warmup.stock.portfolio.PortfolioManagerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.TypeResolutionContext.Empty;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.client.RestTemplate;
// import java.time.temporal.ChronoUnit;
// import org.apache.logging.log4j.ThreadContext;
// import org.springframework.web.client.RestTemplate;

public class PortfolioManagerApplication {

  // // TODO: CRIO_TASK_MODULE_JSON_PARSING
  // //  Task:
  // //       - Read the json file provided in the argument[0], The file is available in the classpath.
  // //       - Go through all of the trades in the given file,
  // //       - Prepare the list of all symbols a portfolio has.
  // //       - if "trades.json" has trades like
  // //         [{ "symbol": "MSFT"}, { "symbol": "AAPL"}, { "symbol": "GOOGL"}]
  // //         Then you should return ["MSFT", "AAPL", "GOOGL"]
  // //  Hints:
  // //    1. Go through two functions provided - #resolveFileFromResources() and #getObjectMapper
  // //       Check if they are of any help to you.
  // //    2. Return the list of all symbols in the same order as provided in json.

  // //  Note:
  // //  1. There can be few unused imports, you will need to fix them to make the build pass.
  // //  2. You can use "./gradlew build" to check if your code builds successfully.

  public static List<String> mainReadFile(String[] args) throws IOException, URISyntaxException {
    
    File file = resolveFileFromResources(args[0]);
    ObjectMapper objectMapper = getObjectMapper();
    PortfolioTrade[] portfolioTrades = objectMapper.readValue( file, PortfolioTrade[].class);
    List<String> list = new ArrayList<String>();
    for ( PortfolioTrade trade : portfolioTrades) {

          list.add(trade.getSymbol());
    }
     return list;
  }

  // Note:
  // 1. You may need to copy relevant code from #mainReadQuotes to parse the Json.
  // 2. Remember to get the latest quotes from Tiingo API.


  // TODO: CRIO_TASK_MODULE_REST_API
  //  Find out the closing price of each stock on the end_date and return the list
  //  of all symbols in ascending order by its close value on end date.

  // Note:
  // 1. You may have to register on Tiingo to get the api_token.
  // 2. Look at args parameter and the module instructions carefully.
  // 2. You can copy relevant code from #mainReadFile to parse the Json.
  // 3. Use RestTemplate#getForObject in order to call the API,
  //    and deserialize the results in List<Candle>



  private static void printJsonObject(Object object) throws IOException {
    Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
    ObjectMapper mapper = new ObjectMapper();
    logger.info(mapper.writeValueAsString(object));
  }

  private static File resolveFileFromResources(String filename) throws URISyntaxException {
    return Paths.get(
        Thread.currentThread().getContextClassLoader().getResource(filename).toURI()).toFile();
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }


  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  //  Follow the instructions provided in the task documentation and fill up the correct values for
  //  the variables provided. First value is provided for your reference.
  //  A. Put a breakpoint on the first line inside mainReadFile() which says
  //    return Collections.emptyList();
  //  B. Then Debug the test #mainReadFile provided in PortfoliomanagerApplicationTest.java
  //  following the instructions to run the test.
  //  Once you are able to run the test, perform following tasks and record the output as a
  //  String in the function below.
  //  Use this link to see how to evaluate expressions -
  //  https://code.visualstudio.com/docs/editor/debugging#_data-inspection
  //  1. evaluate the value of "args[0]" and set the value
  //     to the variable named valueOfArgument0 (This is implemented for your reference.)
  //  2. In the same window, evaluate the value of expression below and set it
  //  to resultOfResolveFilePathArgs0
  //     expression ==> resolveFileFromResources(args[0])
  //  3. In the same window, evaluate the value of expression below and set it
  //  to toStringOfObjectMapper.
  //  You might see some garbage numbers in the output. Dont worry, its expected.
  //    expression ==> getObjectMapper().toString()
  //  4. Now Go to the debug window and open stack trace. Put the name of the function you see at
  //  second place from top to variable functionNameFromTestFileInStackTrace
  //  5. In the same window, you will see the line number of the function in the stack trace window.
  //  assign the same to lineNumberFromTestFileInStackTrace
  //  Once you are done with above, just run the corresponding test and
  //  make sure its working as expected. use below command to do the same.
  //  ./gradlew test --tests PortfolioManagerApplicationTest.testDebugValues

  public static List<String> debugOutputs() {

     String valueOfArgument0 = "trades.json";
     String resultOfResolveFilePathArgs0 = "/home/crio-user/workspace/krayush25-ME_QMONEY_V2/qmoney/bin/main/trades.json";
     String toStringOfObjectMapper = "com.fasterxml.jackson.databind.ObjectMapper@30bce90b";
     String functionNameFromTestFileInStackTrace = "PortfolioManagerApplicationTes.mainReadFile()";
     String lineNumberFromTestFileInStackTrace = "53";


    return Arrays.asList(new String[]{valueOfArgument0, resultOfResolveFilePathArgs0,
        toStringOfObjectMapper, functionNameFromTestFileInStackTrace,
        lineNumberFromTestFileInStackTrace});
  }


  // Note:
  // Remember to confirm that you are getting same results for annualized returns as in Module 3.
  public static List<String> mainReadQuotes(String[] args) throws IOException, URISyntaxException {
    String token = "5281d0e1c35d77b08fa4c8a710a34cd45ca5d7c9";
    //convert String to LocalDate
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String date = args[1];
    LocalDate endDate = LocalDate.parse(date, formatter);

    RestTemplate restTemplate = new RestTemplate();
    List<PortfolioTrade> portfolioTrades = readTradesFromJson(args[0]);
    List<TotalReturnsDto> listOfDTO = new ArrayList<>();

    for(PortfolioTrade trade : portfolioTrades){
      String url = prepareUrl(trade,endDate,token);
      TiingoCandle[] tiingoCandle = restTemplate.getForObject(url, TiingoCandle[].class);
      TotalReturnsDto totalReturnsDto = new TotalReturnsDto(trade.getSymbol(), tiingoCandle[tiingoCandle.length-1].getClose());
      listOfDTO.add(totalReturnsDto);
     }
     
     Collections.sort(listOfDTO,Comparator.comparingDouble(TotalReturnsDto::getClosingPrice));
     
     List<String> ListOfSymbol = listOfDTO.stream().map(TotalReturnsDto :: getSymbol).collect(Collectors.toList());
     return ListOfSymbol;
  }
  
  // TODO:
  //  After refactor, make sure that the tests pass by using these two commands
  //  ./gradlew test --tests PortfolioManagerApplicationTest.readTradesFromJson
  //  ./gradlew test --tests PortfolioManagerApplicationTest.mainReadFile
  public static List<PortfolioTrade> readTradesFromJson(String filename) throws IOException, URISyntaxException {
    File file = resolveFileFromResources(filename);
    ObjectMapper objectMapper = getObjectMapper();

    PortfolioTrade[] portfolioTrades =  objectMapper.readValue( file, PortfolioTrade[].class); 
    
    return Arrays.asList(portfolioTrades);
  }


  // TODO:
  //  Build the Url using given parameters and use this function in your code to cann the API.
  public static String prepareUrl(PortfolioTrade trade, LocalDate endDate, String token) {
     //return Collections.emptyList();
     String url = "https://api.tiingo.com/tiingo/daily/" +trade.getSymbol()+"/prices?startDate="
                          +trade.getPurchaseDate().toString()+"&endDate="+endDate.toString()+"&token="+token;
     return url;
  }



  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  Now that you have the list of PortfolioTrade and their data, calculate annualized returns
  //  for the stocks provided in the Json.
  //  Use the function you just wrote #calculateAnnualizedReturns.
  //  Return the list of AnnualizedReturns sorted by annualizedReturns in descending order.

  // Note:
  // 1. You may need to copy relevant code from #mainReadQuotes to parse the Json.
  // 2. Remember to get the latest quotes from Tiingo API.




  // TODO:
  //  Ensure all tests are passing using below command
  //  ./gradlew test --tests ModuleThreeRefactorTest
  static Double getOpeningPriceOnStartDate(List<Candle> candles) {
     double startingPrice = candles.get(0).getOpen();
     return startingPrice;
  }


  public static Double getClosingPriceOnEndDate(List<Candle> candles) {
     double closingPrice = candles.get(candles.size()-1).getClose();
     return closingPrice;
  }


  public static List<Candle> fetchCandles(PortfolioTrade trade, LocalDate endDate, String token) {
    RestTemplate restTemplate = new RestTemplate();
    String url = prepareUrl(trade,endDate,token);
    TiingoCandle[] candle = restTemplate.getForObject(url, TiingoCandle[].class);

    return Arrays.asList(candle);
  }

  public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args) throws IOException, URISyntaxException {

    List<AnnualizedReturn> list = new ArrayList<>();
    List<PortfolioTrade> trades = readTradesFromJson(args[0]);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String date = args[1];
    LocalDate endDate = LocalDate.parse(date, formatter);

    for(PortfolioTrade trade : trades){
      List<Candle> candle = fetchCandles(trade, endDate, getToken());
      AnnualizedReturn annualizedReturn = 
              calculateAnnualizedReturns(endDate,trade,getOpeningPriceOnStartDate(candle),getClosingPriceOnEndDate(candle));

      list.add(annualizedReturn);
    }

    Collections.sort(list,Comparator.comparingDouble(AnnualizedReturn::getAnnualizedReturn));
    Collections.reverse(list);
    return list;
  }

  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  Return the populated list of AnnualizedReturn for all stocks.
  //  Annualized returns should be calculated in two steps:
  //   1. Calculate totalReturn = (sell_value - buy_value) / buy_value.
  //      1.1 Store the same as totalReturns
  //   2. Calculate extrapolated annualized returns by scaling the same in years span.
  //      The formula is:
  //      annualized_returns = (1 + total_returns) ^ (1 / total_num_years) - 1
  //      2.1 Store the same as annualized_returns
  //  Test the same using below specified command. The build should be successful.
  //     ./gradlew test --tests PortfolioManagerApplicationTest.testCalculateAnnualizedReturn

  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate,
      PortfolioTrade trade, Double buyPrice, Double sellPrice) {
      
      double totolReturns =(sellPrice-buyPrice)/buyPrice;
      long noOfDays = ChronoUnit.DAYS.between(trade.getPurchaseDate(), endDate);
      double noOfYears = (double)noOfDays/(365.0);
      double annualReturn = Math.pow(1+totolReturns,(1/noOfYears))-1;


      return new AnnualizedReturn(trade.getSymbol(), annualReturn, totolReturns);
  }

  public static String  getToken(){
    return "5281d0e1c35d77b08fa4c8a710a34cd45ca5d7c9";
  }


  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Once you are done with the implementation inside PortfolioManagerImpl and
  //  PortfolioManagerFactory, create PortfolioManager using PortfolioManagerFactory.
  //  Refer to the code from previous modules to get the List<PortfolioTrades> and endDate, and
  //  call the newly implemented method in PortfolioManager to calculate the annualized returns.

  // Note:
  // Remember to confirm that you are getting same results for annualized returns as in Module 3.

  public static List<AnnualizedReturn> mainCalculateReturnsAfterRefactor(String[] args)
      throws Exception {
      RestTemplate restTemplate = new RestTemplate();
        
      PortfolioManager portfolioManager = PortfolioManagerFactory.getPortfolioManager(restTemplate);
      List<AnnualizedReturn> list = new ArrayList<>();
      String file = args[0];
       
      LocalDate endDate = LocalDate.parse(args[1]);
      List<PortfolioTrade> portfolioTrades = readTradesFromJson(args[0]);
      ObjectMapper objectMapper = getObjectMapper();
      return portfolioManager.calculateAnnualizedReturn(portfolioTrades, endDate);
        //return list;
  }


  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());


    // printJsonObject(mainReadQuotes(args));


    // printJsonObject(mainCalculateSingleReturn(args));

    printJsonObject(mainCalculateReturnsAfterRefactor(args));
  }
}

