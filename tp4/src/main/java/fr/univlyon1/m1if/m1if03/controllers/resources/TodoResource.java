package fr.univlyon1.m1if.m1if03.controllers.resources;

import fr.univlyon1.m1if.m1if03.dao.TodoDao;
import fr.univlyon1.m1if.m1if03.dao.UserDao;
import fr.univlyon1.m1if.m1if03.exceptions.ForbiddenLoginException;
import fr.univlyon1.m1if.m1if03.model.Todo;
import fr.univlyon1.m1if.m1if03.model.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import javax.naming.InvalidNameException;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import java.util.Collection;

public class TodoResource {
    private final TodoDao todoDao;

    /**
     * Constructeur avec une injection du DAO nécessaire aux opérations.
     * @param totoDao le DAO de todo provenant du contexte applicatif
     */
    public TodoResource(TodoDao totoDao) {
        this.todoDao = totoDao;
    }

    /**
     * Crée un toto et le place dans le DAO.
     *
     * @param titre Titre de la todo créée
     * @param creatorLogin Login de l'utilisateur qui crée la todo
     * @throws IllegalArgumentException Si le creatorLogin ou titre est null ou vide
     */
    public void create(@NotNull String titre, @NotNull String creatorLogin) throws IllegalArgumentException {
        if (creatorLogin == null || creatorLogin.isEmpty()) {
            throw new IllegalArgumentException("Le login du créateur ne doit pas être null ou vide.");
        }
        if (titre == null) {
            throw new IllegalArgumentException("Le titre ne doit pas être null.");
        }
        todoDao.add(new Todo(titre, creatorLogin));
    }

    // TODO move to business
    public void modifyStatus(@Positive int todoHash) {
        boolean actuelStatus = todoDao.findByHash(todoHash).isCompleted();
        todoDao.findByHash(todoHash).setCompleted(!actuelStatus);
    }

    public Collection<Todo> readAll() { return todoDao.findAll(); }

    public Todo readOne(@Positive int todoHash) {
        return todoDao.findByHash(todoHash);
    }

    public void update(@Positive int todoHash, String titre, String assignee) {
        Todo todo = readOne(todoHash);
        if(titre != null && assignee.isEmpty()){
            todo.setTitle(titre);
        }
        if(assignee != null && titre.isEmpty()) {
            todo.setAssignee(assignee);
        }
    }

    public void delete(@Positive int todoHash) throws NameNotFoundException {
        Todo todo = todoDao.findByHash(todoHash);
        todoDao.delete(todo);
    }

    public String readByTitle(@Positive int todoHash) {
        return todoDao.findByHash(todoHash).getTitle();
    }

    public String readByAssignee(@Positive int todoHash) throws NameNotFoundException {
        return todoDao.findByHash(todoHash).getAssignee();
    }


}
