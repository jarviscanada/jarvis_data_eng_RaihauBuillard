package ca.jrvs.apps.jdbc.helpers;

import ca.jrvs.apps.jdbc.models.Quote;
import ca.jrvs.apps.jdbc.utils.QuoteConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;

public class QuoteHttpHelper {

    final Logger logger = LoggerFactory.getLogger(QuoteHttpHelper.class);
    private String apiKey;
    private OkHttpClient client;

    public QuoteHttpHelper(String apiKey, OkHttpClient client){
        this.apiKey = apiKey;
        this.client = client;
    }


    public Quote fetchQuoteInfo(String symbol) throws IllegalArgumentException{
        Request request = getBuild(symbol);
        try {
            return getQuote(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Request getBuild(String symbol) {
        return new Request.Builder()
                .url("https://alpha-vantage.p.rapidapi.com/query?function=GLOBAL_QUOTE&symbol=" + symbol + "&datatype=json")
                .get()
                .addHeader("x-rapidapi-key", apiKey)
                .addHeader("x-rapidapi-host", "alpha-vantage.p.rapidapi.com")
                .build();
    }

    private Quote getQuote(Request request) throws IOException {
        Response response = client.newCall(request).execute();
        String jsonValue = response.body().string();
        logger.debug(jsonValue);
        Quote quote = JsonParser.toSpecificObjectFromJson(jsonValue, QuoteConstants.GLOBAL_QUOTE_API_FIELD, Quote.class);
        quote.setTimestamp(Timestamp.from(Instant.now()));
        return quote;
    }


}
