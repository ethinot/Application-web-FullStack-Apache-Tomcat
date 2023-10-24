package fr.univlyon1.m1if.m1if03.controllers;

import fr.univlyon1.m1if.m1if03.controllers.resources.TodoResource;
import fr.univlyon1.m1if.m1if03.dao.TodoDao;
import fr.univlyon1.m1if.m1if03.dto.todo.TodoDtoMapper;
import fr.univlyon1.m1if.m1if03.dto.todo.TodoResponseDto;
import fr.univlyon1.m1if.m1if03.model.Todo;
import fr.univlyon1.m1if.m1if03.utils.UrlUtils;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.NameNotFoundException;
import java.io.IOException;

/**
 * Contrôleur de ressources "todos".
 * Gère les CU liés aux opérations CRUD sur la collection de todos :
 *     Création / modification / suppression d'un toto : POST, PUT, DELETE
 *     Récupération de la liste de todos / de todo en lien avec un utilisateur / d'un todo en particulier : GET
 * Cette servlet fait appel à une nested class UserResource qui se charge des appels au DAO.
 * Les opérations métier spécifiques (login &amp; logout) sont réalisées par la servlet <a href="UserBusinessController.html"><code>UserBusinessController</code></a>.
 *
 * @author Lionel Médini
 */
@WebServlet(name = "UserResourceController", urlPatterns = {"/todos", "/todos/*"})
public class TodoResourceController extends HttpServlet {
    private TodoDtoMapper todoMapper;
    private TodoResource todoResource;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        TodoDao todoDao = (TodoDao) config.getServletContext().getAttribute("todoDao");
        todoMapper = new TodoDtoMapper(config.getServletContext());
        todoResource = new TodoResource(todoDao);
    }
    //</editor-fold>

    //<editor-fold desc="Méthodes de service">
    /**
     * Réalise la création d'un todo.
     * Renvoie un code 201 (Created) en cas de création d'un utilisateur, ou une erreur HTTP appropriée sinon.
     *
     * @param request  Une requête de création, contenant un body avec un login, un password et un nom
     * @param response Une réponse vide, avec uniquement un code de réponse et éventuellement un header <code>Location</code>
     * @throws IOException Input/output exception
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("X-test", "doPost");
        String[] url = UrlUtils.getUrlParts(request);
        if (url.length == 1) {
            // Création d'un todo
            String title = request.getParameter("titre");
            String creator = request.getParameter("assignee");
        }
    }

    /**
     * Traite les requêtes GET.
     * Renvoie une représentation de la ressource demandée.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("X-test", "doGet");
        String[] url = UrlUtils.getUrlParts(request);
        if (url.length == 1) {
            // Renvoie la collections de todos
            request.setAttribute("todos", todoResource.readAll());
        }
        try {
            Todo todo = todoResource.readOne(Integer.parseInt(url[1]));
            TodoResponseDto todoDto = todoMapper.toDto(todo);
            switch (url.length) {
                case 2 -> {
                    // Renvoie un DTO de todo
                    request.setAttribute("totoDto", todoDto);
                    request.getRequestDispatcher("/WEB-INF/components/user.jsp").include(request, response);
                }
                case 3 -> { // Renvoie une propriété d'un todo
                    switch (url[2]) {
                        case "title" -> {
                            // Renvoie le titre d'un todo
                        }
                        case "assignee" -> {
                            // Renvoie l'utilisateur auquel le todo est assigné
                        }
                        default -> response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    }
                }
                default -> { // Redirige vers l'URL qui devrait correspondre à la sous-propriété demandée (qu'elle existe ou pas ne concerne pas ce contrôleur)
                    if (url[2].equals("assignee")) {
                        // Construction de la fin de l'URL vers laquelle rediriger la sous-propriété
                        String urlEnd = UrlUtils.getUrlEnd(request, 3);
                        response.sendRedirect("users" + urlEnd);
                    } else {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    }
                }
            }
        } catch (IllegalArgumentException ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        } /*catch (NameNotFoundException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Le todo " + url[1] + " n'existe pas.");
        }*/
    }

    /**
     * Réalise la modification d'un todo.
     * En fonction du l'id d'un todo (hash) passé dans l'URL :
     *      - title
     *      - assignee
     * Renvoie un code de statut 204 (No Content) en cas de succès ou une erreur HTTP appropriée sinon.
     *
     * @param request  Une requête dont l'URL est de la forme /todos/{todoId}, et contenant :
     * @param response Une réponse vide (si succès)
     * @throws IOException Voir doc...
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] url = UrlUtils.getUrlParts(request);
        int todoHash = Integer.parseInt(url[1]);
        String title = request.getParameter("title");
        String assignee = request.getParameter("assignee");

        if (url.length == 2) {
            try {
                todoResource.update(todoHash, title, assignee);
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } catch (IllegalArgumentException ex1) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex1.getMessage());
                try {
                    todoResource.create(title, assignee);
                    response.setHeader("Location", "todos/" + todoHash);
                    response.setStatus(HttpServletResponse.SC_CREATED);
                } catch (IllegalArgumentException ex2) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex2.getMessage());
                }
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * Réalise l'aiguillage des requêtes DELETE.<br>
     * En clair : appelle simplement l'opération de suppression de l'utilisateur.<br>
     * Renvoie un code 204 (No Content) si succès ou une erreur HTTP appropriée sinon.
     *
     * @param request  Une requête dont l'URL est de la forme <code>/users/{login}</code>
     * @param response Une réponse vide (si succès)
     * @throws IOException Voir doc...
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] url = UrlUtils.getUrlParts(request);
        int todoId = Integer.parseInt(url[1]);
        if (url.length == 2) {
            try {
                todoResource.delete(todoId);
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } catch (IllegalArgumentException ex) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
            } catch (NameNotFoundException e) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Le todo avec l'id : " + todoId + " n'existe pas.");
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
