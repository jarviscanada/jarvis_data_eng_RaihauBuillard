package ca.jrvs.apps.jdbc.enums;

public enum Ticker {
    MICROSOFT("MSFT"),
    APPLE("AAPL"),
    GOOGLE("GOOGL"),
    AMAZON("AMAZN"),
    META("META");


    private final String label;
    Ticker(String label) {
        this.label = label;
    }

    @Override
    public String toString(){
        return label;
    }
}
