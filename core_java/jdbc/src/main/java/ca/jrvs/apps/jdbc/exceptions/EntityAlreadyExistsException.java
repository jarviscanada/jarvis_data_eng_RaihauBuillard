package ca.jrvs.apps.jdbc.exceptions;

public class EntityAlreadyExistsException extends RuntimeException {
    public EntityAlreadyExistsException(String message){
        super(message);
    }
}
