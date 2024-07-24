package ca.jrvs.apps.jdbc.unit;

import ca.jrvs.apps.jdbc.enums.Ticker;
import ca.jrvs.apps.jdbc.exceptions.EntityNotFoundException;
import ca.jrvs.apps.jdbc.exceptions.NotProfitableToSellException;
import ca.jrvs.apps.jdbc.models.Position;
import ca.jrvs.apps.jdbc.models.Quote;
import ca.jrvs.apps.jdbc.repositories.PositionDAO;
import ca.jrvs.apps.jdbc.services.PositionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PositionServiceTest {

    @Mock
    private PositionDAO positionDAO;

    @InjectMocks
    private PositionService positionService;

    private Quote msftQuote;
    private Quote profitableQuote;
    private Quote unProfitableQuote;

    private Position msftPosition;
    private String ticker;

    @BeforeEach
    public void init(){
        ticker = Ticker.MICROSOFT.toString();
        MockitoAnnotations.openMocks(this);
        msftQuote = new Quote(ticker, 151.6500, 153.4200, 151.0200,
                152.0600, 9425575, new Date(System.currentTimeMillis()), 151.7000, 0.36000, "0.2373%", Timestamp.from(Instant.now()));
        profitableQuote = new Quote(ticker, 151.6500, 153.4200, 151.0200,
                183.0600, 9425575, new Date(System.currentTimeMillis()), 151.7000, 0.36000, "0.2373%", Timestamp.from(Instant.now()));
        unProfitableQuote = new Quote(ticker, 151.6500, 153.4200, 151.0200,
                120.0600, 9425575, new Date(System.currentTimeMillis()), 151.7000, 0.36000, "0.2373%", Timestamp.from(Instant.now()));

        msftPosition = new Position(ticker, 250, 250*msftQuote.getPrice());
    }

    @Test
    public void buy_shouldSaveAndReturnPosition_whenPositionDoesntExist(){
        when(positionDAO.findById(anyString())).thenReturn(Optional.empty());
        when(positionDAO.save(any(Position.class))).thenReturn(msftPosition);

        Position expectedPosition = positionService.buy(ticker, msftPosition.getNumOfShares(), msftPosition.getValuePaid());

        assertNotNull(expectedPosition);
        assertEquals(msftPosition, expectedPosition);
        verify(positionDAO, never()).deleteById(anyString());
        verify(positionDAO).save(any(Position.class));
    }

    @Test
    public void buy_shouldUpdateAndReturnPosition_whenPositionExists(){
        when(positionDAO.findById(anyString())).thenReturn(Optional.of(msftPosition));
        when(positionDAO.save(any(Position.class))).thenReturn(msftPosition);

        Position expectedPosition = positionService.buy(ticker, msftPosition.getNumOfShares(), msftPosition.getValuePaid());

        assertNotNull(expectedPosition);
        assertEquals(msftPosition, expectedPosition);
        verify(positionDAO).deleteById(anyString());
        verify(positionDAO).save(any(Position.class));
    }

    @Test
    public void sell_shouldRemoveStocks_whenStocksAreProfitable(){
        when(positionDAO.findById(anyString())).thenReturn(Optional.of(msftPosition));
        positionService.sell(profitableQuote);
        verify(positionDAO).deleteById(anyString());
    }

    @Test
    public void sell_ShouldThrowEntityNotFoundException_whenPositionNotInDB(){
        when(positionDAO.findById(anyString())).thenReturn(Optional.empty());
        Exception exception = assertThrows(EntityNotFoundException.class, () -> positionService.sell(msftQuote));

        String expectedErrorMessage = "Position with ticker " + ticker + " not found";
        String actualErrorMessage = exception.getMessage();
        assertEquals(actualErrorMessage, expectedErrorMessage);
    }

    @Test
    public void sell_shouldThrowNotProfitableException_whenStocksAreNotProfitable(){
        when(positionDAO.findById(anyString())).thenReturn(Optional.of(msftPosition));
        Exception exception = assertThrows(NotProfitableToSellException.class, () -> positionService.sell(unProfitableQuote));

        String expectedErrorMessage = "It's not profitable to sell the stock with ticker " + ticker;
        String actualErrorMessage = exception.getMessage();
        assertEquals(actualErrorMessage, expectedErrorMessage);
    }
}
