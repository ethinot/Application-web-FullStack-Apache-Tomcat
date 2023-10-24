package fr.univlyon1.m1if.m1if03.controllers.resources;

import fr.univlyon1.m1if.m1if03.dao.TodoDao;
import jakarta.validation.constraints.Positive;

/**
 * Nested class qui réalise l'opérations de changement de status d'un todo (completé ou non).<br>
 *
 */
public class TodoBusiness {

    private final TodoDao todoDao;

    /**
     * Constructeur avec une injection du DAO nécessaire aux opérations.
     * @param todoDao le DAO de todos provenant du contexte applicatif
     */
    public TodoBusiness(TodoDao todoDao) {
        this.todoDao = todoDao;
    }

    public void modifyStatus(@Positive int todoHash) {
        boolean actuelStatus = todoDao.findByHash(todoHash).isCompleted();
        todoDao.findByHash(todoHash).setCompleted(!actuelStatus);
    }
}