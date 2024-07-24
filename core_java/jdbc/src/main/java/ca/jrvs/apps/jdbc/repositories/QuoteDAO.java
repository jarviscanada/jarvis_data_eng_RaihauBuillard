package ca.jrvs.apps.jdbc.repositories;

import ca.jrvs.apps.jdbc.exceptions.EntityAlreadyExistsException;
import ca.jrvs.apps.jdbc.interfaces.CrudDao;
import ca.jrvs.apps.jdbc.models.Quote;
import ca.jrvs.apps.jdbc.utils.QuoteConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QuoteDAO implements CrudDao<Quote, String> {
    private final Connection dbConnection;
    private final Logger logger = LoggerFactory.getLogger(QuoteDAO.class);
    private final String INSERT= "insert into quote (" + QuoteConstants.SYMBOL_DB_FIELD + ", "
            + QuoteConstants.OPEN_DB_FIELD + ", "
            + QuoteConstants.HIGH_DB_FIELD + ", "
            + QuoteConstants.LOW_DB_FIELD + ", "
            + QuoteConstants.PRICE_DB_FIELD + ", "
            + QuoteConstants.VOLUME_DB_FIELD + ", "
            + QuoteConstants.LATEST_TRAIDING_DAY_DB_FIELD + ", "
            + QuoteConstants.PREVIOUS_CLOSE_DB_FIELD + ", "
            + QuoteConstants.CHANGE_DB_FIELD + ", "
            + QuoteConstants.CHANGE_PERCENT_DB_FIELD + ", "
            + QuoteConstants.TIMESTAMP_DB_FIELD + ") "
            + "values (?,?,?,?,?,?,?,?,?,?,?)";

    private final String SELECT_BY_ID = "select * from quote where symbol=?";
    private final String SELECT_ALL = "select * from quote";
    private final String DELETE_BY_ID = "delete from quote where symbol=?";
    private final String DELETE_ALL = "delete from quote";

    public QuoteDAO(Connection dbConnection){
        this.dbConnection = dbConnection;
    }

    @Override
    public Quote save(Quote entity){
        String ticker = entity.getTicker();
        if(exists(ticker)){
            throw new EntityAlreadyExistsException("Quote with ID "+ticker+" already exists");
        }

        try(PreparedStatement statement = dbConnection.prepareStatement(INSERT)){
            statement.setString(1, ticker);
            statement.setDouble(2, entity.getOpen());
            statement.setDouble(3, entity.getHigh());
            statement.setDouble(4, entity.getLow());
            statement.setDouble(5, entity.getPrice());
            statement.setInt(6, entity.getVolume());
            statement.setDate(7, entity.getLatestTradingDay());
            statement.setDouble(8, entity.getPreviousClose());
            statement.setDouble(9, entity.getChange());
            statement.setString(10, entity.getChangePercent());
            statement.setTimestamp(11, entity.getTimestamp());
            statement.executeUpdate();

            return findById(ticker).get();

        }catch(SQLException sqlException){
            String errorMessage = "Can't execute save method due to this error : "+sqlException.getMessage();
            logger.error(errorMessage, sqlException);
            throw new RuntimeException(errorMessage, sqlException);
        }
    }

    @Override
    public Optional<Quote> findById(String ticker) throws IllegalArgumentException {
        Quote quote = null;
        try(PreparedStatement statement = dbConnection.prepareStatement(SELECT_BY_ID)){
            statement.setString(1, ticker);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                quote = buildQuote(resultSet);
            }
        }catch(SQLException sqlException){
            String errorMessage = "Can't execute findById method due to this error : "+sqlException.getMessage();
            logger.error(errorMessage, sqlException);
            throw new RuntimeException(errorMessage, sqlException);
        }
        return Optional.ofNullable(quote);
    }


    @Override
    public Iterable<Quote> findAll() {
        List<Quote> quotes = new ArrayList<>();
        try(PreparedStatement statement = dbConnection.prepareStatement(SELECT_ALL)){
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                quotes.add(buildQuote(resultSet));
            }
        }catch(SQLException sqlException){
            String errorMessage = "Can't execute findAll method due to this error : "+sqlException.getMessage();
            logger.error(errorMessage, sqlException);
            throw new RuntimeException(errorMessage, sqlException);
        }
        return quotes;
    }

    @Override
    public void deleteById(String ticker) throws IllegalArgumentException {
        try(PreparedStatement statement = dbConnection.prepareStatement(DELETE_BY_ID)){
            statement.setString(1, ticker);
            statement.executeUpdate();
        }catch(SQLException sqlException){
            String errorMessage = "Can't execute deleteById method due to this error : "+sqlException.getMessage();
            logger.error(errorMessage, sqlException);
            throw new RuntimeException(errorMessage, sqlException);
        }
    }

    @Override
    public void deleteAll() {
        try(PreparedStatement statement = dbConnection.prepareStatement(DELETE_ALL)){
            statement.executeUpdate();
        }catch(SQLException sqlException){
            String errorMessage = "Can't execute deleteAll method due to this error : "+sqlException.getMessage();
            logger.error(errorMessage, sqlException);
            throw new RuntimeException(errorMessage, sqlException);
        }
    }

    @Override
    public boolean exists(String ticker){
        return findById(ticker).isPresent();
    }

    private Quote buildQuote(ResultSet resultSet) throws SQLException {
        Quote quote = new Quote();
        quote.setTicker(resultSet.getString(QuoteConstants.SYMBOL_DB_FIELD));
        quote.setOpen(resultSet.getDouble(QuoteConstants.OPEN_DB_FIELD));
        quote.setHigh(resultSet.getDouble(QuoteConstants.HIGH_DB_FIELD));
        quote.setLow(resultSet.getDouble(QuoteConstants.LOW_DB_FIELD));
        quote.setPrice(resultSet.getDouble(QuoteConstants.PRICE_DB_FIELD));
        quote.setVolume(resultSet.getInt(QuoteConstants.VOLUME_DB_FIELD));
        quote.setLatestTradingDay(resultSet.getDate(QuoteConstants.LATEST_TRAIDING_DAY_DB_FIELD));
        quote.setPreviousClose(resultSet.getDouble(QuoteConstants.PREVIOUS_CLOSE_DB_FIELD));
        quote.setChange(resultSet.getDouble(QuoteConstants.CHANGE_DB_FIELD));
        quote.setChangePercent(resultSet.getString(QuoteConstants.CHANGE_PERCENT_DB_FIELD));
        quote.setTimestamp(resultSet.getTimestamp(QuoteConstants.TIMESTAMP_DB_FIELD));
        return quote;
    }

}
