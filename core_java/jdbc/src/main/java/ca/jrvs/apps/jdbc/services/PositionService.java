package ca.jrvs.apps.jdbc.services;

import ca.jrvs.apps.jdbc.repositories.PositionDAO;
import ca.jrvs.apps.jdbc.models.Position;

public class PositionService {

    private PositionDAO dao;

    public PositionService(PositionDAO dao){
        this.dao = dao;
    }

    public Position buy(String ticker, int numberOfShares, double price){
        return null;
    }

    public void sell(String ticker){

    }
}
