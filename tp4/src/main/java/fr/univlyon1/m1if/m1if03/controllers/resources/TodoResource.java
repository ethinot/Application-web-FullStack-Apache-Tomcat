package fr.univlyon1.m1if.m1if03.controllers.resources;

import fr.univlyon1.m1if.m1if03.dao.TodoDao;
import fr.univlyon1.m1if.m1if03.model.Todo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import javax.naming.NameNotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Nested class qui réalise les opérations "simples" (CRUD) de gestion des ressources de type Todo.
 * Son utilité est surtout de prendre en charge les différentes exceptions qui peuvent être levées par le DAO.
 *
 */
public class TodoResource {
    private final TodoDao todoDao;

    /**
     * Constructeur avec une injection du DAO nécessaire aux opérations.
     *
     * @param totoDao le DAO de todo provenant du contexte applicatif
     */
    public TodoResource(TodoDao totoDao) {
        this.todoDao = totoDao;
    }

    /**
     * Crée un toto et le place dans le DAO.
     *
     * @param titre        Titre de la todo créée
     * @param creatorLogin Login de l'utilisateur qui crée la todo
     * @return int Le hash du todo crée
     * @throws IllegalArgumentException Si le creatorLogin ou titre est null ou vide
     */
    public int create(@NotNull String titre, @NotNull String creatorLogin) throws IllegalArgumentException {
        if (creatorLogin == null || creatorLogin.isEmpty()) {
            throw new IllegalArgumentException("Le login du créateur ne doit pas être null ou vide.");
        }
        if (titre == null) {
            throw new IllegalArgumentException("Le titre ne doit pas être null.");
        }
        Todo newTodo = new Todo(titre, creatorLogin);
        todoDao.add(newTodo);
        return newTodo.hashCode();
    }

    public Collection<Todo> readAll() {
        return todoDao.findAll();
    }

    public List<Integer> readAllIds() {
        return todoDao.getAllIds();
    }

    public Todo readOne(@Positive int todoHash) {
        return todoDao.findByHash(todoHash);
    }

    public void update(@Positive int todoHash, String titre, String assignee) throws NoSuchElementException{
        Todo todo = readOne(todoHash);
        if(titre != null && !titre.isEmpty()) {
            todo.setTitle(titre);
        }
        if(assignee != null && !assignee.isEmpty()) {
            todo.setAssignee(assignee);
        }
    }

    public void delete(@Positive int todoHash) throws NoSuchElementException, NameNotFoundException {
        Todo todo = todoDao.findByHash(todoHash);
        todoDao.delete(todo);
    }

    public String readByTitle(@Positive int todoHash) throws NoSuchElementException{
        return todoDao.findByHash(todoHash).getTitle();
    }

    public String readByAssignee(@Positive int todoHash) throws NoSuchElementException {
        return todoDao.findByHash(todoHash).getAssignee();
    }
}
