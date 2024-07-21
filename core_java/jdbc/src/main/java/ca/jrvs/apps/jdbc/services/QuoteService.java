package ca.jrvs.apps.jdbc.services;

import ca.jrvs.apps.jdbc.helpers.QuoteHttpHelper;
import ca.jrvs.apps.jdbc.models.Quote;
import ca.jrvs.apps.jdbc.repositories.QuoteDAO;

import java.time.LocalDate;
import java.util.Optional;

public class QuoteService {

    private QuoteDAO quoteDAO;
    private QuoteHttpHelper httpHelper;

    public QuoteService(QuoteDAO quoteDAO, QuoteHttpHelper httpHelper){
        this.quoteDAO = quoteDAO;
        this.httpHelper = httpHelper;
    }

    private Optional<Quote> fetchQuoteDataFromAPI(String ticker){
        return Optional.ofNullable(httpHelper.fetchQuoteInfo(ticker));
    }

    public Quote getLatestQuote(String ticker) throws IllegalArgumentException{
        Optional<Quote> quoteFromDB = quoteDAO.findById(ticker);
        if(quoteFromDB.isPresent() && isQuoteUpToDate(quoteFromDB.get())) {
            return quoteFromDB.get();
        }

        quoteDAO.deleteById(ticker);

        Optional<Quote> quoteFromAPI = fetchQuoteDataFromAPI(ticker);
        if(!quoteFromAPI.isPresent() || quoteFromAPI.get().getTicker() == null) {
            throw new IllegalArgumentException("Quote with ID " + ticker + " not found from Alpha Vantage API");
        }

        quoteDAO.save(quoteFromAPI.get());
        return quoteFromAPI.get();
    }

    public boolean isQuoteUpToDate(Quote quote) {
        LocalDate currentDate = LocalDate.now();
        LocalDate latestTradingDay = quote.getTimestamp().toLocalDateTime().toLocalDate();
        return currentDate.equals(latestTradingDay);
    }


}
