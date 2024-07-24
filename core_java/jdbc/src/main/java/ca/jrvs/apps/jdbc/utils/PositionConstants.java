package ca.jrvs.apps.jdbc.utils;

public class PositionConstants {
    private PositionConstants(){
        throw new UnsupportedOperationException("This is a constants class and cannot be instantiated");
    }

    public static final String SYMBOL_DB_FIELD = "symbol";
    public static final String NUMBER_OF_SHARES_DB_FIELD = "number_of_shares";
    public static final String VALUE_PAID_DB_FIELD = "value_paid";
}
