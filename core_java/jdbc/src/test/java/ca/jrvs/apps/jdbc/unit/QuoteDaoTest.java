package ca.jrvs.apps.jdbc.unit;

import ca.jrvs.apps.jdbc.enums.Ticker;
import ca.jrvs.apps.jdbc.exceptions.EntityAlreadyExistsException;
import ca.jrvs.apps.jdbc.repositories.QuoteDAO;
import ca.jrvs.apps.jdbc.models.Quote;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class QuoteDaoTest {

    private static QuoteDAO quoteDAO;
    private static Connection connection;

    private static final String host = "localhost";
    private static final String databaseName = "stock_quote";
    private static final String url = "jdbc:postgresql://"+host+"/"+databaseName;

    private Quote msftQuote;
    private Quote aaplQuote;
    private Quote googlQuote;


    @BeforeAll
    static void init(){
        Properties properties = new Properties();
        properties.setProperty("user", "postgres");
        properties.setProperty("password", "passwrd");
        try {
            connection = DriverManager.getConnection(url, properties);
            quoteDAO = new QuoteDAO(connection);
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
        msftQuote = new Quote(Ticker.MICROSOFT.toString(), 151.6500, 153.4200, 151.0200,
                152.0600, 9425575, new Date(System.currentTimeMillis()), 151.7000, 0.36000, "0.2373%", Timestamp.from(Instant.now()));
        aaplQuote = new Quote(Ticker.APPLE.toString(), 250.3000, 255.4200, 248.7200,
                252.4000, 12536789, new Date(System.currentTimeMillis()), 249.5000, 2.9000, "1.1623%", Timestamp.from(Instant.now()));
        googlQuote = new Quote(Ticker.GOOGLE.toString(), 1350.1000, 1375.5000, 1345.9000,
                1365.8000, 10457800, new Date(System.currentTimeMillis()), 1358.3000, 7.5000, "0.5519%", Timestamp.from(Instant.now()));
    }

    @AfterEach
    void cleanUp(){
        quoteDAO.deleteAll();
    }

    @Test
    void whenSaveQuote_thenQuoteIsSaved(){
        Quote result = quoteDAO.save(msftQuote);
        assertNotNull(result);
        assertEquals(msftQuote.getOpen(), result.getOpen());
    }

    @Test
    void whenSaveQuoteArleadyExists_thenThrowEntityAlreadyExistsException(){
        quoteDAO.save(msftQuote);
        Exception exception = assertThrows(EntityAlreadyExistsException.class, () -> {
            quoteDAO.save(msftQuote);
        });

        String expectedErrorMessage = "Quote with ID "+msftQuote.getTicker()+" already exists";
        String actualErrorMessage = exception.getMessage();
        assertEquals(actualErrorMessage, expectedErrorMessage);
    }

    @Test
    void whendFindQuoteById_thenReturnEntity(){
        quoteDAO.save(msftQuote);
        Optional<Quote> result = quoteDAO.findById(msftQuote.getTicker());
        assertTrue(result.isPresent());
        assertEquals(msftQuote.getOpen(), result.get().getOpen());
    }

    @Test
    void whenQuoteNotFound_thenReturnNull(){
        Optional<Quote> result = quoteDAO.findById(msftQuote.getTicker());
        assertFalse(result.isPresent());
    }

    @Test
    void whenFindAllQuotes_thenReturnQuotesCollection(){
        quoteDAO.save(msftQuote);
        quoteDAO.save(aaplQuote);
        quoteDAO.save(googlQuote);
        List<Quote> quotes = (List<Quote>) quoteDAO.findAll();

        assertNotNull(quotes);
        assertEquals(3, quotes.size());
        assertEquals(aaplQuote.getTicker(), quotes.get(1).getTicker());
        assertEquals(googlQuote.getTicker(), quotes.get(2).getTicker());
    }

    @Test
    void whenDeleteQuoteById_thenQuoteIsDeleted(){
        quoteDAO.save(msftQuote);
        quoteDAO.deleteById(msftQuote.getTicker());
        Optional<Quote> result = quoteDAO.findById(msftQuote.getTicker());
        assertFalse(result.isPresent());
    }

    @Test
    void whenDeleteAllQuotes_thenAllQuotesAreDeleted(){
        quoteDAO.save(msftQuote);
        quoteDAO.save(aaplQuote);
        quoteDAO.save(googlQuote);
        quoteDAO.deleteAll();
        List<Quote> quotes = (List<Quote>) quoteDAO.findAll();
        assertTrue(quotes.isEmpty());
    }


}
