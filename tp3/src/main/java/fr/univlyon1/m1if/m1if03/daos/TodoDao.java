package fr.univlyon1.m1if.m1if03.daos;

import fr.univlyon1.m1if.m1if03.classes.Todo;

import java.io.Serializable;

/**
 * Implémentation de l'interface DAO pour la classe Todo.
 *
 *
 */
public class TodoDao extends AbstractListDao<Todo> {

    @Override
    protected Serializable getKeyForElement(Todo element) {
        return element.hashCode();
    }
}
