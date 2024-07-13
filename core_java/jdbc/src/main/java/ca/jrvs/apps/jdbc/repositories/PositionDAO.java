package ca.jrvs.apps.jdbc.repositories;

import ca.jrvs.apps.jdbc.exceptions.EntityAlreadyExistsException;
import ca.jrvs.apps.jdbc.interfaces.CrudDao;
import ca.jrvs.apps.jdbc.models.Position;
import ca.jrvs.apps.jdbc.utils.PositionConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PositionDAO implements CrudDao<Position, String> {
    private final Connection dbConnection;
    private final Logger logger = LoggerFactory.getLogger(PositionDAO.class);
    private final String INSERT= "insert into position (" + PositionConstants.SYMBOL_DB_FIELD + ", "
            + PositionConstants.NUMBER_OF_SHARES_DB_FIELD + ", "
            + PositionConstants.VALUE_PAID_DB_FIELD + ") "
            + "values (?,?,?)";

    private final String SELECT_BY_ID = "select * from position where symbol=?";
    private final String SELECT_ALL = "select * from position";
    private final String DELETE_BY_ID = "delete from position where symbol=?";
    private final String DELETE_ALL = "delete from position";

    public PositionDAO(Connection dbConnection){
        this.dbConnection = dbConnection;
    }

    @Override
    public Position save(Position entity){
        String ticker = entity.getTicker();
        if(exists(ticker)){
            throw new EntityAlreadyExistsException("Position with ID "+ticker+" already exists");
        }

        try(PreparedStatement statement = dbConnection.prepareStatement(INSERT)){
            statement.setString(1, ticker);
            statement.setInt(2, entity.getNumOfShares());
            statement.setDouble(3, entity.getValuePaid());
            statement.executeUpdate();

            return findById(ticker).get();
        }catch(SQLException sqlException){
            String errorMessage = "Can't execute the save method due to this error : "+sqlException.getMessage();
            logger.error(errorMessage, sqlException);
            throw new RuntimeException(errorMessage, sqlException);
        }
    }

    @Override
    public Optional<Position> findById(String ticker) throws IllegalArgumentException {
        Position position = null;
        try(PreparedStatement statement = dbConnection.prepareStatement(SELECT_BY_ID)){
            statement.setString(1, ticker);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                position = buildPosition(resultSet);
            }
        }catch(SQLException sqlException){
            String errorMessage = "Can't execute the findById method due to this error : "+sqlException.getMessage();
            logger.error(errorMessage, sqlException);
            throw new RuntimeException(errorMessage, sqlException);
        }

        return Optional.ofNullable(position);
    }


    @Override
    public Iterable<Position> findAll() {
        List<Position> positions = new ArrayList<>();
        try(PreparedStatement statement = dbConnection.prepareStatement(SELECT_ALL)){
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                positions.add(buildPosition(resultSet));
            }
        }catch(SQLException sqlException){
            String errorMessage = "Can't execute findAll method due to this error : "+sqlException.getMessage();
            logger.error(errorMessage, sqlException);
            throw new RuntimeException(errorMessage, sqlException);
        }
        return positions;
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

    private Position buildPosition(ResultSet resultSet) throws SQLException {
        Position position = new Position();
        position.setTicker(resultSet.getString(PositionConstants.SYMBOL_DB_FIELD));
        position.setNumOfShares(resultSet.getInt(PositionConstants.NUMBER_OF_SHARES_DB_FIELD));
        position.setValuePaid(resultSet.getDouble(PositionConstants.VALUE_PAID_DB_FIELD));
        return position;
    }
}
