package ca.jrvs.apps.jdbc.unit;

import ca.jrvs.apps.jdbc.enums.Ticker;
import ca.jrvs.apps.jdbc.exceptions.EntityAlreadyExistsException;
import ca.jrvs.apps.jdbc.models.Position;
import ca.jrvs.apps.jdbc.models.Quote;
import ca.jrvs.apps.jdbc.repositories.PositionDAO;
import ca.jrvs.apps.jdbc.repositories.QuoteDAO;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PositionDaoTest {

    private static QuoteDAO quoteDAO;
    private static PositionDAO positionDAO;
    private static Connection connection;

    private static final String host = "localhost";
    private static final String databaseName = "stock_quote";
    private static final String url = "jdbc:postgresql://"+host+"/"+databaseName;

    private Position msftPosition;
    private Position aaplPosition;
    private Position googlPosition;

    private static Quote msftQuote;
    private static Quote aaplQuote;
    private static Quote googlQuote;


    @BeforeAll
    static void init(){
        Properties properties = new Properties();
        properties.setProperty("user", "postgres");
        properties.setProperty("password", "passwrd");

        msftQuote = new Quote(Ticker.MICROSOFT.toString(), 151.6500, 153.4200, 151.0200,
                152.0600, 9425575, new Date(System.currentTimeMillis()), 151.7000, 0.36000, "0.2373%", Timestamp.from(Instant.now()));
        aaplQuote = new Quote(Ticker.APPLE.toString(), 250.3000, 255.4200, 248.7200,
                252.4000, 12536789, new Date(System.currentTimeMillis()), 249.5000, 2.9000, "1.1623%", Timestamp.from(Instant.now()));
        googlQuote = new Quote(Ticker.GOOGLE.toString(), 1350.1000, 1375.5000, 1345.9000,
                1365.8000, 10457800, new Date(System.currentTimeMillis()), 1358.3000, 7.5000, "0.5519%", Timestamp.from(Instant.now()));

        try {
            connection = DriverManager.getConnection(url, properties);
            positionDAO = new PositionDAO(connection);
            quoteDAO = new QuoteDAO(connection);
            quoteDAO.save(msftQuote);
            quoteDAO.save(aaplQuote);
            quoteDAO.save(googlQuote);
        } catch (SQLException e) {
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
        msftPosition = new Position(Ticker.MICROSOFT.toString(), 200, 15843.4200);
        aaplPosition = new Position(Ticker.APPLE.toString(), 250, 454458.5420);
        googlPosition = new Position(Ticker.GOOGLE.toString(), 135, 1375.5000);
    }

    @AfterEach
    void cleanUp(){
        positionDAO.deleteAll();
    }

    @Test
    void whenSavePosition_thenPositionIsSaved(){
        Position result = positionDAO.save(msftPosition);
        assertNotNull(result);
        assertEquals(msftPosition.getNumOfShares(), result.getNumOfShares());
    }

    @Test
    void whenSavePositionArleadyExists_thenThrowEntityAlreadyExistsException(){
        positionDAO.save(msftPosition);
        Exception exception = assertThrows(EntityAlreadyExistsException.class, () -> {
            positionDAO.save(msftPosition);
        });

        String expectedErrorMessage = "Position with ID "+ msftPosition.getTicker()+" already exists";
        String actualErrorMessage = exception.getMessage();
        assertEquals(actualErrorMessage, expectedErrorMessage);
    }

    @Test
    void whendFindPositionById_thenReturnEntity(){
        positionDAO.save(msftPosition);
        Optional<Position> result = positionDAO.findById(msftPosition.getTicker());
        assertTrue(result.isPresent());
        assertEquals(msftPosition.getNumOfShares(), result.get().getNumOfShares());
    }

    @Test
    void whenPositionNotFound_thenReturnNull(){
        Optional<Position> result = positionDAO.findById(msftPosition.getTicker());
        assertFalse(result.isPresent());
    }

    @Test
    void whenFindAllPositions_thenReturnPositionsCollection(){
        positionDAO.save(msftPosition);
        positionDAO.save(aaplPosition);
        positionDAO.save(googlPosition);
        List<Position> positions = (List<Position>) positionDAO.findAll();

        assertNotNull(positions);
        assertEquals(3, positions.size());
        assertEquals(aaplPosition.getTicker(), positions.get(1).getTicker());
        assertEquals(googlPosition.getTicker(), positions.get(2).getTicker());
    }

    @Test
    void whenDeletePositionById_thenPositionIsDeleted(){
        positionDAO.save(msftPosition);
        positionDAO.deleteById(msftPosition.getTicker());
        Optional<Position> result = positionDAO.findById(msftPosition.getTicker());
        assertFalse(result.isPresent());
    }

    @Test
    void whenDeleteAllQuotes_thenAllQuotesAreDeleted(){
        positionDAO.save(msftPosition);
        positionDAO.save(aaplPosition);
        positionDAO.save(googlPosition);
        positionDAO.deleteAll();
        List<Position> positions = (List<Position>) positionDAO.findAll();
        assertTrue(positions.isEmpty());
    }
}
