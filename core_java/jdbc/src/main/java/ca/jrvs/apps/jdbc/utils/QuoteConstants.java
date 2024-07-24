package ca.jrvs.apps.jdbc.utils;

public final class QuoteConstants {

    private QuoteConstants(){
        throw new UnsupportedOperationException("This is a constants class and cannot be instantiated");
    }

    // Fields name from the Alpha Vantage API
    public static final String GLOBAL_QUOTE_API_FIELD = "Global Quote";
    public static final String SYMBOL_API_FIELD = "01. symbol";
    public static final String OPEN_API_FIELD = "02. open";
    public static final String HIGH_API_FIELD = "03. high";
    public static final String LOW_API_FIELD = "04. low";
    public static final String PRICE_API_FIELD = "05. price";
    public static final String VOLUME_API_FIELD = "06. volume";
    public static final String LATEST_TRAIDING_DAY_API_FIELD = "07. latest trading day";
    public static final String PREVIOUS_CLOSE_API_FIELD = "08. previous close";
    public static final String CHANGE_API_FIELD = "09. change";
    public static final String CHANGE_PERCENT_API_FIELD = "10. change percent";

    // Fields name from the stock_quote database
    public static final String SYMBOL_DB_FIELD = "symbol";
    public static final String OPEN_DB_FIELD = "open";
    public static final String HIGH_DB_FIELD = "high";
    public static final String LOW_DB_FIELD = "low";
    public static final String PRICE_DB_FIELD = "price";
    public static final String VOLUME_DB_FIELD = "volume";
    public static final String LATEST_TRAIDING_DAY_DB_FIELD = "latest_trading_day";
    public static final String PREVIOUS_CLOSE_DB_FIELD = "previous_close";
    public static final String CHANGE_DB_FIELD = "change";
    public static final String CHANGE_PERCENT_DB_FIELD = "change_percent";
    public static final String TIMESTAMP_DB_FIELD = "timestamp";

}
