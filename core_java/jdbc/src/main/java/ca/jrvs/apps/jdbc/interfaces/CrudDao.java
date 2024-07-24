package ca.jrvs.apps.jdbc.interfaces;

import ca.jrvs.apps.jdbc.exceptions.EntityAlreadyExistsException;
import ca.jrvs.apps.jdbc.exceptions.EntityNotFoundException;

import java.util.Optional;

public interface CrudDao<T, ID>{

    T save(T entity) throws EntityAlreadyExistsException;
    Optional<T> findById(ID id) throws EntityNotFoundException;
    Iterable<T> findAll();
    void deleteById(ID id) throws EntityNotFoundException;
    void deleteAll();
    boolean exists(ID id);

}
