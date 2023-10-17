package fr.univlyon1.m1if.m1if03.classes;

import fr.univlyon1.m1if.m1if03.daos.AbstractListDao;
import fr.univlyon1.m1if.m1if03.daos.Dao;
import fr.univlyon1.m1if.m1if03.exceptions.MissingParameterException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Objects;

/**
 * Interface that implement methode for adding and update todo from TodoList object.
 *
 */
public interface TodoOperation {

    /**
     * Method used for adding a new Todo objet to the todos DAO.
     *
     * @param request the HTTP/HTTPS request
     * @param response the HTTP/HTTPS response we want to build
     * @throws IOException throw exception if IN/OUT outputs are incorrect
     */
    static void add(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            AbstractListDao<Todo> todos = (AbstractListDao<Todo>) request.getServletContext().getAttribute("todos");
            if (request.getParameter("title") == null || request.getParameter("login") == null) {
                throw new MissingParameterException("Paramètres du Todo insuffisamment spécifiés.");
            }
            todos.add(new Todo(request.getParameter("title"), request.getParameter("login")));
            request.setAttribute("todos", todos.findAll());
            request.getRequestDispatcher("todolist.jsp").include(request, response);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Format de l'index du User incorrect.");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Methode to update a specefic Todo from the DAO todos.
     *
     * @param request the HTTP/HTTPS request
     * @param response the HTTP/HTTPS response we want to build
     * @throws IOException throw exception if IN/OUT outputs are incorrect
     */
    static void update(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            AbstractListDao<Todo> todos = (AbstractListDao<Todo>) request.getServletContext().getAttribute("todos");
            int index = Integer.parseInt(request.getParameter("index"));
            if (index < 0 || index >= todos.size()) {
                throw new StringIndexOutOfBoundsException("Pas de todo avec l'index " + index + ".");
            }
            Todo todo = todos.findOne(index);
            if (request.getParameter("toggle") != null && !request.getParameter("toggle").isEmpty()) {
                todo.setCompleted(Objects.equals(request.getParameter("toggle"), "Done!"));
            } else {
                if (request.getParameter("assign") != null && !request.getParameter("assign").isEmpty()) {
                    String login = (String) request.getSession().getAttribute("login");
                    User user = ((Dao<User>) request.getServletContext().getAttribute("users")).findOne(login);
                    todo.setAssignee(user.getLogin());

                } else {
                    throw new MissingParameterException("Modification à réaliser non spécifiée.");
                }
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Format de l'index du User incorrect.");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    static void getUserTodos(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            AbstractListDao<Todo> todos = (AbstractListDao<Todo>) request.getServletContext().getAttribute("todos");
            request.setAttribute("todos", todos);
            request.getRequestDispatcher("user.jsp").include(request, response);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Format de l'index du User incorrect.");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    static void getTodoList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            AbstractListDao<Todo> todos = (AbstractListDao<Todo>) request.getServletContext().getAttribute("todos");
            request.setAttribute("todos", todos.findAll());
            request.getRequestDispatcher("todolist.jsp").include(request, response);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Format de l'index du User incorrect.");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

}
