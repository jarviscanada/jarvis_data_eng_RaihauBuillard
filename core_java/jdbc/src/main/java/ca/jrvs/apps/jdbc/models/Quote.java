package ca.jrvs.apps.jdbc.models;

import java.sql.Timestamp;
import java.util.Date;

public class Quote {
    private String ticker;
    private double open;
    private double high;
    private double low;
    private double price;
    private int volume;
    private Date latestTradingDay;
    private double previousClose;
    private double change;
    private String changePercent;
    private Timestamp timestamp;
}
