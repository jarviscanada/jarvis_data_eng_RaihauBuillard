package ca.jrvs.apps.jdbc.integration;

import ca.jrvs.apps.jdbc.enums.Ticker;
import ca.jrvs.apps.jdbc.exceptions.EntityNotFoundException;
import ca.jrvs.apps.jdbc.exceptions.NotProfitableToSellException;
import ca.jrvs.apps.jdbc.helpers.QuoteHttpHelper;
import ca.jrvs.apps.jdbc.models.Position;
import ca.jrvs.apps.jdbc.models.Quote;
import ca.jrvs.apps.jdbc.repositories.PositionDAO;
import ca.jrvs.apps.jdbc.repositories.QuoteDAO;
import ca.jrvs.apps.jdbc.services.PositionService;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.time.Instant;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class PositionServiceTest {

    private static QuoteDAO quoteDAO;
    private static PositionDAO positionDAO;
    private static PositionService positionService;

    private static Connection connection;
    private static QuoteHttpHelper helper;

    private static final String host = "localhost";
    private static final String databaseName = "stock_quote";
    private static final String url = "jdbc:postgresql://"+host+"/"+databaseName;

    private static Quote msftQuote;
    private static Quote profitableQuote;
    private static Quote unProfitableQuote;
    private Position msftPosition;

    private static String ticker;

    @BeforeAll
    public static void init(){
        Properties properties = new Properties();
        properties.setProperty("user", "postgres");
        properties.setProperty("password", "passwrd");

        ticker = Ticker.MICROSOFT.toString();
        msftQuote = new Quote(ticker, 151.6500, 153.4200, 151.0200,
                152.0600, 9425575, new Date(System.currentTimeMillis()), 151.7000, 0.36000, "0.2373%", Timestamp.from(Instant.now()));
        profitableQuote = new Quote(ticker, 151.6500, 153.4200, 151.0200,
                183.0600, 9425575, new Date(System.currentTimeMillis()), 151.7000, 0.36000, "0.2373%", Timestamp.from(Instant.now()));
        unProfitableQuote = new Quote(ticker, 151.6500, 153.4200, 151.0200,
                120.0600, 9425575, new Date(System.currentTimeMillis()), 151.7000, 0.36000, "0.2373%", Timestamp.from(Instant.now()));
        try{
            connection = DriverManager.getConnection(url, properties);
            positionDAO = new PositionDAO(connection);
            positionService = new PositionService(positionDAO);
            quoteDAO = new QuoteDAO(connection);
            quoteDAO.save(msftQuote);
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    static void tearDown(){
        try {
            quoteDAO.deleteAll();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp(){
        msftPosition = new Position(ticker, 250, 250*msftQuote.getPrice());
    }

    @AfterEach
    void cleanUp(){
        positionDAO.deleteAll();
    }


    @Test
    public void buy_shouldSaveAndReturnPosition_whenPositionDoesntExist(){
        Position expectedPosition = positionService.buy(ticker, msftPosition.getNumOfShares(), msftPosition.getValuePaid());

        assertNotNull(expectedPosition);
        assertEquals(msftPosition, expectedPosition);
    }

    @Test
    public void sell_shouldRemoveStocks_whenStocksAreProfitable(){
        positionService.buy(ticker, msftPosition.getNumOfShares(), msftPosition.getValuePaid());
        positionService.sell(profitableQuote);
        assertFalse(positionDAO.findById(ticker).isPresent());
    }

    @Test
    public void sell_ShouldThrowEntityNotFoundException_whenPositionNotInDB(){
        Exception exception = assertThrows(EntityNotFoundException.class, () -> positionService.sell(msftQuote));

        String expectedErrorMessage = "Position with ticker " + ticker + " not found";
        String actualErrorMessage = exception.getMessage();
        assertEquals(actualErrorMessage, expectedErrorMessage);
    }

    @Test
    public void sell_shouldThrowNotProfitableException_whenStocksAreNotProfitable(){
        positionService.buy(ticker, msftPosition.getNumOfShares(), msftPosition.getValuePaid());
        Exception exception = assertThrows(NotProfitableToSellException.class, () -> positionService.sell(unProfitableQuote));

        String expectedErrorMessage = "It's not profitable to sell the stock with ticker " + ticker;
        String actualErrorMessage = exception.getMessage();
        assertEquals(actualErrorMessage, expectedErrorMessage);
    }

}
