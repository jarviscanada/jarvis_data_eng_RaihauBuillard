package ca.jrvs.apps.jdbc.integration;

import ca.jrvs.apps.jdbc.enums.Ticker;
import ca.jrvs.apps.jdbc.helpers.QuoteHttpHelper;
import ca.jrvs.apps.jdbc.models.Quote;
import ca.jrvs.apps.jdbc.repositories.QuoteDAO;
import ca.jrvs.apps.jdbc.services.QuoteService;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.time.Instant;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class QuoteServiceTest {
    private static QuoteDAO quoteDAO;
    private static QuoteService quoteService;
    private static QuoteHttpHelper helper;
    private static Connection connection;

    private static final String host = "localhost";
    private static final String databaseName = "stock_quote";
    private static final String url = "jdbc:postgresql://"+host+"/"+databaseName;

    private Quote msftQuote;

    private static String ticker;


    @BeforeAll
    static void init(){
        Properties properties = new Properties();
        properties.setProperty("user", "postgres");
        properties.setProperty("password", "passwrd");
        try {
            connection = DriverManager.getConnection(url, properties);
            quoteDAO = new QuoteDAO(connection);
            helper = new QuoteHttpHelper(System.getenv("SECRET_API_KEY"), new OkHttpClient());
            quoteService = new QuoteService(quoteDAO, helper);
            ticker = Ticker.MICROSOFT.toString();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    static void tearDown(){
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp(){
        msftQuote = new Quote(ticker, 151.6500, 153.4200, 151.0200,
                152.0600, 9425575, new Date(System.currentTimeMillis()), 151.7000, 0.36000, "0.2373%", Timestamp.from(Instant.now()));
    }

    @AfterEach
    void cleanUp(){
        quoteDAO.deleteAll();
    }

    @Test
    void getLatestQuote_shouldReturnQuoteFromDB_whenQuoteIsUpToDate() {
        quoteDAO.save(msftQuote);
        Quote result = quoteService.getLatestQuote(ticker);

        assertNotNull(result);
        assertEquals(msftQuote, result);
    }

    @Test
    void getLatestQuote_shouldFetchAndReturnQuoteFromAPI_whenQuoteIsNotUpToDate() {
        Date fakeDate = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24);
        Quote oldQuote = new Quote(ticker, 151.6500, 153.4200, 151.0200, 152.0600, 9425575,
                fakeDate, 151.7000, 0.36000, "0.2373%", new Timestamp(fakeDate.getTime()));
        quoteDAO.save(oldQuote);

        Quote result = quoteService.getLatestQuote(ticker);

        assertNotNull(result);
        assertNotEquals(oldQuote, result);
    }

    @Test
    void getLatestQuote_shouldFetchAndReturnQuoteFromAPI_whenQuoteIsNotInDB() {
        Quote result = quoteService.getLatestQuote(ticker);
        assertNotNull(result);
    }

    @Test
    void getLatestQuote_shouldThrowException_whenQuoteNotFoundInAPI() {
        String invalid_ticker = "INVALID_TICKER";
        Exception exception = assertThrows(IllegalArgumentException.class, () -> quoteService.getLatestQuote(invalid_ticker));

        String expectedErrorMessage = "Quote with ID " + invalid_ticker + " not found from Alpha Vantage API";
        String actualErrorMessage = exception.getMessage();
        assertEquals(actualErrorMessage, expectedErrorMessage);
    }

}
