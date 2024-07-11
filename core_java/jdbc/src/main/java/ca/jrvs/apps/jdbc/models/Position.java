package ca.jrvs.apps.jdbc.models;

public class Position {
    private String ticker;
    private int numOfShares;
    private double valuePaid;

    public Position(){

    }

    public Position(String ticker, int numOfShares, double valuePaid) {
        this.ticker = ticker;
        this.numOfShares = numOfShares;
        this.valuePaid = valuePaid;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public int getNumOfShares() {
        return numOfShares;
    }

    public void setNumOfShares(int numOfShares) {
        this.numOfShares = numOfShares;
    }

    public double getValuePaid() {
        return valuePaid;
    }

    public void setValuePaid(double valuePaid) {
        this.valuePaid = valuePaid;
    }
}
