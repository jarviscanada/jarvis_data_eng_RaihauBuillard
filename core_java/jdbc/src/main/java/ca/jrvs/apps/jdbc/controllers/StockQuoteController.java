package ca.jrvs.apps.jdbc.controllers;

import ca.jrvs.apps.jdbc.exceptions.EntityNotFoundException;
import ca.jrvs.apps.jdbc.exceptions.NotProfitableToSellException;
import ca.jrvs.apps.jdbc.helpers.DateFormat;
import ca.jrvs.apps.jdbc.models.Position;
import ca.jrvs.apps.jdbc.models.Quote;
import ca.jrvs.apps.jdbc.services.PositionService;
import ca.jrvs.apps.jdbc.services.QuoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.InputMismatchException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class StockQuoteController {

    public static final String INCORRECT_TICKER_ERROR_MESSAGE = "The specified ticker symbol is incorrect, can not find any quote information";
    private final Logger flowLogger = LoggerFactory.getLogger("general_flow_logs");
    private final Logger errorLogger = LoggerFactory.getLogger("error_logs");
    private QuoteService quoteService;
    private PositionService positionService;

    private static final int GET_QUOTE_INFO = 1;
    private static final int BUY_STOCKS = 2;
    private static final int SELL_STOCKS = 3;
    private static final int LIST_STOCKS = 4;
    private static final int QUIT = 5;

    public StockQuoteController(QuoteService quoteService, PositionService positionService) {
        this.quoteService = quoteService;
        this.positionService = positionService;
    }

    public void initClient(){
        try(Scanner scanner = new Scanner(System.in)){
            printMessage("Welcome to the stock quote application");
            int option = 0;
            while(option != QUIT){
                printMenu();
                option = handleUserOption(scanner);
            }
        }
    }

    private int handleUserOption(Scanner scanner) {
        int option = -1;
        while (option == -1) {
            try {
                option = scanner.nextInt();
                if (option < GET_QUOTE_INFO || option > QUIT) {
                    printMessage("Invalid option. Please try again.");
                    option = -1;
                } else {
                    executeOption(option, scanner);
                }
            } catch (InputMismatchException e) {
                printMessage("Invalid input. Please enter a number.");
                scanner.next();
                flowLogger.warn("User entered invalid input");
            } catch (NoSuchElementException e){
                printMessage("You didn't launch the application in interactive mode");
                flowLogger.warn("User didn't launch program in interactive mode");
                option = QUIT;
            }
        }
        return option;
    }

    private void executeOption(int option, Scanner scanner) {
        switch(option){
            case GET_QUOTE_INFO:
                printQuoteDetails(scanner);
                break;
            case BUY_STOCKS:
                buyProcess(scanner);
                break;
            case SELL_STOCKS:
                sellStocks(scanner);
                break;
            case LIST_STOCKS:
                listStock();
                break;
            default:
                printMessage("Bye!");
                break;
        }
    }

    private void printMenu() {
        printMessage("");
        printMessage("Choose an option :");
        printMessage("1. Get stock quote information");
        printMessage("2. Buy stocks");
        printMessage("3. Sell stocks");
        printMessage("4. List all stocks");
        printMessage("5. Quit");
    }

    public String getTicker(Scanner scanner){
        printMessage("Select a ticker (MSFT for microsoft, AAPL for apple, GOOGL for google, etc...) :");
        return scanner.next().toUpperCase();
    }

    public void printQuoteDetails(Scanner scanner){
        String ticker = getTicker(scanner);
        try{
            Quote quote = quoteService.getLatestQuote(ticker);
            quoteDetails(quote);
            printMessage("Do you want to buy it ? (Y or N)");
            char answer = scanner.next().charAt(0);
            if(Character.toUpperCase(answer) == 'Y'){
                buyStocks(quote, scanner);
            }
            flowLogger.info("User print quote details");
        }catch(IllegalArgumentException illegalArgumentException){
            printMessage(INCORRECT_TICKER_ERROR_MESSAGE);
            errorLogger.error(illegalArgumentException.getMessage(), illegalArgumentException);
        }
    }

    public void buyProcess(Scanner scanner){
        try{
            String ticker = getTicker(scanner);
            Quote quote = quoteService.getLatestQuote(ticker);
            buyStocks(quote, scanner);
        }catch(IllegalArgumentException illegalArgumentException){
            printMessage(INCORRECT_TICKER_ERROR_MESSAGE);

        }
    }

    public void buyStocks(Quote quote, Scanner scanner){
        printMessage("How many share do you want to buy ?");
        int numberOfShares = scanner.nextInt();
        Position position = positionService.buy(quote.getTicker(), numberOfShares, quote.getPrice()*numberOfShares);
        positionDetails(position);
        flowLogger.info("User bought a stock from the "+quote.getTicker()+" quote");
    }

    public void sellStocks(Scanner scanner){
        try{
            String ticker = getTicker(scanner);
            Quote quote = quoteService.getLatestQuote(ticker);
            positionService.sell(quote);
            flowLogger.info("User sold a stock from the "+quote.getTicker()+" quote");
        }catch(IllegalArgumentException illegalArgumentException){
            printMessage(INCORRECT_TICKER_ERROR_MESSAGE);
            errorLogger.error(illegalArgumentException.getMessage(), illegalArgumentException);
        }catch(EntityNotFoundException notFoundException){
            printMessage("Failed to sell because, you don't own this quote.");
            errorLogger.error(notFoundException.getMessage(), notFoundException);
        }catch(NotProfitableToSellException notProfitableException){
            printMessage("It's not profitable to sell it.");
            errorLogger.error(notProfitableException.getMessage(), notProfitableException);
        }
    }

    public void listStock(){
        List<Position> positions = positionService.getPositions();
        if(positions.isEmpty()){
            printMessage("You don't have any stock.");
        }
        positions.forEach(this::positionDetails);
        flowLogger.info("User displays all his stocks");
    }

    private void quoteDetails(Quote quote){
        printMessage("Quote information for "+quote.getTicker()+" (latest version) :");
        printMessage("");
        printMessage("Price at which the stock opened at the beginning of the trading day (Open) :"+quote.getOpen());
        printMessage("Highest price at which the stock traded during the day (High) :"+quote.getHigh());
        printMessage("Lowest price at which the stock traded during the day (Low) :"+quote.getLow());
        printMessage("Current price of the stock (Price) :"+quote.getPrice());
        printMessage("Total number of shares traded during the trading day (Volume) :"+quote.getVolume());
        printMessage("Most recent day the stock was traded (Latest trading day) :"+DateFormat.formatDate(quote.getLatestTradingDay()));
        printMessage("Price of the stock at the close of the previous trading day (Previous close) :"+quote.getPreviousClose());
        printMessage("Difference between the current price and the previous close (Change) :"+quote.getChange());
        printMessage("Percentage change in price compared to the previous close (Change percentage) :"+quote.getChangePercent());
        printMessage("The exact time when the quote information was last updated :"+ DateFormat.formatTimestamp(quote.getTimestamp()));
        printMessage("");
    }

    private void positionDetails(Position position){
        printMessage("Total number shares owned for "+position.getTicker()+" is "+position.getNumOfShares()+" and it costs "+position.getValuePaid());
    }

    private void printMessage(String message){
        System.out.println(message);
    }
}
