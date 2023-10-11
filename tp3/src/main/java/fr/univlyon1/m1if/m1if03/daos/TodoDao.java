package fr.univlyon1.m1if.m1if03.daos;

import javax.naming.InvalidNameException;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import java.io.Serializable;
import java.util.Collection;

public class TodoDao implements Dao {


    @Override
    public Serializable add(Object element) throws NameAlreadyBoundException {
        return null;
    }

    @Override
    public void delete(Object element) throws NameNotFoundException {

    }

    @Override
    public void deleteById(Serializable id) throws NameNotFoundException, InvalidNameException {

    }

    @Override
    public void update(Serializable id, Object element) throws InvalidNameException {

    }

    @Override
    public Serializable getId(Object element) throws NameNotFoundException {
        return null;
    }

    @Override
    public Object findOne(Serializable id) throws NameNotFoundException, InvalidNameException {
        return null;
    }

    @Override
    public Collection findAll() {
        return null;
    }
}

