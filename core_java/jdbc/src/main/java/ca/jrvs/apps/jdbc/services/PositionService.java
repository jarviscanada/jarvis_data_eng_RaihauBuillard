package ca.jrvs.apps.jdbc.services;

import ca.jrvs.apps.jdbc.exceptions.EntityNotFoundException;
import ca.jrvs.apps.jdbc.exceptions.NotProfitableToSellException;
import ca.jrvs.apps.jdbc.models.Position;
import ca.jrvs.apps.jdbc.models.Quote;
import ca.jrvs.apps.jdbc.repositories.PositionDAO;

import java.util.Optional;

public class PositionService {

    private PositionDAO positionDAO;

    public PositionService(PositionDAO positionDAO){
        this.positionDAO = positionDAO;
    }

    public Position buy(String ticker, int numberOfShares, double price){
        Optional<Position> optPosition = positionDAO.findById(ticker);
        if(optPosition.isPresent()){
            positionDAO.deleteById(ticker);
        }
        return positionDAO.save(new Position(ticker, numberOfShares, price));
    }

    public void sell(Quote quote){
        String ticker = quote.getTicker();
        Optional<Position> optPosition = positionDAO.findById(ticker);
        if(!optPosition.isPresent()){
            throw new EntityNotFoundException("Position with ticker " + ticker + " not found");
        }

        Position position = optPosition.get();
        if(!isThereAnyProfit(quote, position)) {
            throw new NotProfitableToSellException("It's not profitable to sell the stock with ticker " + ticker);
        }

        positionDAO.deleteById(ticker);
    }

    private boolean isThereAnyProfit(Quote quote, Position position) {
        return position.getValuePaid() < position.getNumOfShares()*quote.getPrice();
    }
}
