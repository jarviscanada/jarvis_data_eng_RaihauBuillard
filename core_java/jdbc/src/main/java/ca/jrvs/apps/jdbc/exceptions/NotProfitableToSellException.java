package ca.jrvs.apps.jdbc.exceptions;

public class NotProfitableToSellException extends RuntimeException{
    public NotProfitableToSellException(String message) {
        super(message);
    }
}
