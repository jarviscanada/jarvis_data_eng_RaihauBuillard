package ca.jrvs.apps.jdbc.dao;

import ca.jrvs.apps.jdbc.interfaces.CrudDao;
import ca.jrvs.apps.jdbc.models.Position;
import ca.jrvs.apps.jdbc.models.Quote;

import java.util.Optional;

public class QuoteDao implements CrudDao<Quote, String> {
    @Override
    public Quote save(Quote entity) throws IllegalArgumentException {
        return null;
    }

    @Override
    public Optional<Quote> findById(String s) throws IllegalArgumentException {
        return Optional.empty();
    }

    @Override
    public Iterable<Quote> findAll() {
        return null;
    }

    @Override
    public void deleteById(String s) throws IllegalArgumentException {

    }

    @Override
    public void deleteAll() {

    }
}
