package ca.jrvs.apps.jdbc.controllers;

import ca.jrvs.apps.jdbc.exceptions.EntityNotFoundException;
import ca.jrvs.apps.jdbc.exceptions.NotProfitableToSellException;
import ca.jrvs.apps.jdbc.models.Quote;
import ca.jrvs.apps.jdbc.services.PositionService;
import ca.jrvs.apps.jdbc.services.QuoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StockQuoteController {

    private final Logger logger = LoggerFactory.getLogger(StockQuoteController.class);
    private QuoteService quoteService;
    private PositionService positionService;

    public StockQuoteController(QuoteService quoteService, PositionService positionService) {
        this.quoteService = quoteService;
        this.positionService = positionService;
    }

    public void initClient(){


    }

    public void displayQuoteDetails(String ticker){
        try{
            Quote quote = quoteService.getLatestQuote(ticker);

        }catch(IllegalArgumentException illegalArgumentException){

        }

    }

    public void buyStocks(){
        String ticker = "";
        int numberOfShares = 0;
        double valuePaid = 0;
        Quote quote = quoteService.getLatestQuote(ticker);
        valuePaid = quote.getPrice() * numberOfShares;
    }

    public void buyProcess(){

    }

    public void sellStocks(){
        try{

        }catch(EntityNotFoundException notFoundException){

        }catch(NotProfitableToSellException notProfitableException){

        }
    }
}
