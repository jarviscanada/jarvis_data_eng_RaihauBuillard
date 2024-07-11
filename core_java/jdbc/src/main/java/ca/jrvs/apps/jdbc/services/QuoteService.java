package ca.jrvs.apps.jdbc.services;

import ca.jrvs.apps.jdbc.repositories.QuoteDAO;
import ca.jrvs.apps.jdbc.helpers.QuoteHttpHelper;
import ca.jrvs.apps.jdbc.models.Quote;

import java.util.Optional;

public class QuoteService {

    private QuoteDAO dao;
    private QuoteHttpHelper httpHelper;

    public QuoteService(QuoteDAO dao, QuoteHttpHelper httpHelper){
        this.dao = dao;
        this.httpHelper = httpHelper;
    }

    public Optional<Quote> fetchQuoteDataFromAPI(String ticker){
        return Optional.of(httpHelper.fetchQuoteInfo(ticker));
    }

}
