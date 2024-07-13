package ca.jrvs.apps.jdbc.unit;

import ca.jrvs.apps.jdbc.enums.Ticker;
import ca.jrvs.apps.jdbc.helpers.QuoteHttpHelper;
import ca.jrvs.apps.jdbc.models.Quote;
import ca.jrvs.apps.jdbc.repositories.QuoteDAO;
import ca.jrvs.apps.jdbc.services.QuoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class QuoteServiceTest {

    @Mock
    private QuoteDAO quoteDAO;

    @Mock
    private QuoteHttpHelper helper;

    @Spy
    @InjectMocks
    private QuoteService quoteService;

    private Quote msftQuote;
    private String ticker;

    @BeforeEach
    public void init(){
        MockitoAnnotations.openMocks(this);
        ticker = Ticker.MICROSOFT.toString();
        msftQuote = new Quote(ticker, 151.6500, 153.4200, 151.0200,
                152.0600, 9425575, new Date(System.currentTimeMillis()), 151.7000, 0.36000, "0.2373%", Timestamp.from(Instant.now()));
    }

    @Test
    public void getLatestQuote_shouldReturnQuoteFromDB_whenQuoteIsUpToDate(){
        when(quoteDAO.findById(ticker)).thenReturn(Optional.of(msftQuote));
        doReturn(true).when(quoteService).isQuoteUpToDate(any(Quote.class));

        Quote expectedQuote = quoteService.getLatestQuote(ticker);

        assertNotNull(expectedQuote);
        assertEquals(msftQuote, expectedQuote);
        verify(quoteDAO, never()).deleteById(anyString());
        verify(quoteDAO, never()).save(any(Quote.class));
    }


    @Test
    public void getLatestQuote_shouldFetchAndReturnQuoteFromApi_whenQuoteIsNotUpToDate(){
        when(quoteDAO.findById(ticker)).thenReturn(Optional.of(msftQuote));
        when(helper.fetchQuoteInfo(ticker)).thenReturn(msftQuote);
        doReturn(false).when(quoteService).isQuoteUpToDate(any(Quote.class));

        Quote expectedQuote = quoteService.getLatestQuote(ticker);

        assertNotNull(expectedQuote);
        assertEquals(msftQuote, expectedQuote);
        verify(quoteDAO).deleteById(anyString());
        verify(quoteDAO).save(any(Quote.class));
    }

    @Test
    public void getLatestNode_shouldFetchAndReturnQuoteFromAPI_whenQuoteNotInDB(){
        when(quoteDAO.findById(ticker)).thenReturn(Optional.empty());
        when(helper.fetchQuoteInfo(ticker)).thenReturn(msftQuote);
        doReturn(true).when(quoteService).isQuoteUpToDate(any(Quote.class));

        Quote expectedQuote = quoteService.getLatestQuote(ticker);

        assertNotNull(expectedQuote);
        assertEquals(msftQuote, expectedQuote);
        verify(quoteDAO).deleteById(anyString());
        verify(quoteDAO).save(any(Quote.class));
    }

    @Test
    public void getLatestNode_shouldThrowIllegalArgumentException_whenQuoteNotFoundInAPI(){
        when(quoteDAO.findById(ticker)).thenReturn(Optional.empty());
        when(helper.fetchQuoteInfo(ticker)).thenReturn(null);
        doReturn(true).when(quoteService).isQuoteUpToDate(any(Quote.class));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> quoteService.getLatestQuote(ticker));
        verify(quoteDAO).deleteById(anyString());
        verify(quoteDAO, never()).save(any(Quote.class));

        String expectedErrorMessage = "Quote with ID " + ticker + " not found from Alpha Vantage API";
        String actualErrorMessage = exception.getMessage();
        assertEquals(actualErrorMessage, expectedErrorMessage);
    }

}
