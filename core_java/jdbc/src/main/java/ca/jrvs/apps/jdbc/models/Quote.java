package ca.jrvs.apps.jdbc.models;

import ca.jrvs.apps.jdbc.utils.QuoteConstants;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;
import java.sql.Date;

public class Quote {
    @JsonProperty(QuoteConstants.SYMBOL_API_FIELD)
    private String ticker;

    @JsonProperty(QuoteConstants.OPEN_API_FIELD)
    private double open;

    @JsonProperty(QuoteConstants.HIGH_API_FIELD)
    private double high;

    @JsonProperty(QuoteConstants.LOW_API_FIELD)
    private double low;

    @JsonProperty(QuoteConstants.PRICE_API_FIELD)
    private double price;

    @JsonProperty(QuoteConstants.VOLUME_API_FIELD)
    private int volume;

    @JsonProperty(QuoteConstants.LATEST_TRAIDING_DAY_API_FIELD)
    private Date latestTradingDay;

    @JsonProperty(QuoteConstants.PREVIOUS_CLOSE_API_FIELD)
    private double previousClose;

    @JsonProperty(QuoteConstants.CHANGE_API_FIELD)
    private double change;

    @JsonProperty(QuoteConstants.CHANGE_PERCENT_API_FIELD)
    private String changePercent;

    private Timestamp timestamp;

    public Quote(){

    }

    public Quote(String ticker, double open, double high, double low, double price, int volume, Date latestTradingDay, double previousClose, double change, String changePercent, Timestamp timestamp) {
        this.ticker = ticker;
        this.open = open;
        this.high = high;
        this.low = low;
        this.price = price;
        this.volume = volume;
        this.latestTradingDay = latestTradingDay;
        this.previousClose = previousClose;
        this.change = change;
        this.changePercent = changePercent;
        this.timestamp = timestamp;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public Date getLatestTradingDay() {
        return latestTradingDay;
    }

    public void setLatestTradingDay(Date latestTradingDay) {
        this.latestTradingDay = latestTradingDay;
    }

    public double getPreviousClose() {
        return previousClose;
    }

    public void setPreviousClose(double previousClose) {
        this.previousClose = previousClose;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public String getChangePercent() {
        return changePercent;
    }

    public void setChangePercent(String changePercent) {
        this.changePercent = changePercent;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object obj){
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Quote)) {
            return false;
        }
        Quote other = (Quote) obj;
        return this.ticker.equals(other.ticker) &&
                this.open == other.open &&
                this.high == other.high &&
                this.low == other.low &&
                this.price == other.price &&
                this.volume == other.volume &&
                this.previousClose == other.previousClose &&
                this.change == other.change &&
                this.changePercent.equals(other.changePercent);
    }
}
