package fr.univlyon1.m1if.m1if03.controllers;

import fr.univlyon1.m1if.m1if03.controllers.resources.TodoResource;
import fr.univlyon1.m1if.m1if03.dao.TodoDao;
import fr.univlyon1.m1if.m1if03.dao.UserDao;
import fr.univlyon1.m1if.m1if03.dto.todo.TodoDtoMapper;
import fr.univlyon1.m1if.m1if03.dto.todo.TodoResponseDto;
import fr.univlyon1.m1if.m1if03.dto.user.UserDtoMapper;
import fr.univlyon1.m1if.m1if03.dto.user.UserResponseDto;
import fr.univlyon1.m1if.m1if03.exceptions.ForbiddenLoginException;
import fr.univlyon1.m1if.m1if03.exceptions.UnauthentifiedUserException;
import fr.univlyon1.m1if.m1if03.model.Todo;
import fr.univlyon1.m1if.m1if03.model.User;
import fr.univlyon1.m1if.m1if03.utils.UrlUtils;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;

import javax.naming.AuthenticationException;
import javax.naming.InvalidNameException;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.Collection;

/**
 * Contrôleur de ressources "todos".
 * Gère les CU liés aux opérations CRUD sur la collection de todos :
 *     Création / modification / suppression d'un toto : POST, PUT, DELETE
 *     Récupération de la liste de todos / de todo en lien avec un utilisateur / d'un todo en particulier : GET
 * Cette servlet fait appel à une nested class UserResource</code> qui se charge des appels au DAO.
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
     * Réalise la création d'un utilisateur.
     * Renvoie un code 201 (Created) en cas de création d'un utilisateur, ou une erreur HTTP appropriée sinon.
     *
     * @param request  Une requête de création, contenant un body avec un login, un password et un nom
     * @param response Une réponse vide, avec uniquement un code de réponse et éventuellement un header <code>Location</code>
     * @throws IOException Voir doc...
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("X-test", "doPost");

        String[] url = UrlUtils.getUrlParts(request);

        if (url.length == 1) {// Création d'un todo
            String titre = request.getParameter("title");
            String creatorLogin = request.getParameter("login");
            try {
                todoResource.create(titre, creatorLogin);
                response.setHeader("Location", "todos/" + titre);
                response.setStatus(HttpServletResponse.SC_CREATED);
            } catch (IllegalArgumentException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            }
            //TODO Exception User non connecté
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * Traite les requêtes GET.
     * Renvoie une représentation de la ressource demandée.
     * <ul>
     *     <li>Soit la liste de tous les utilisateurs</li>
     *     <li>Soit un utilisateur</li>
     *     <li>soit une propriété d'un utilisateur</li>
     *     <li>soit une redirection vers une sous-propriété</li>
     * </ul>
     * Renvoie un code de réponse 200 (OK) en cas de représentation, 302 (Found) en cas de redirection, sinon une erreur HTTP appropriée.
     *
     * @param request  Une requête vide
     * @param response Une réponse contenant :
     *                 <ul>
     *                     <li>la liste des liens vers les instances de <code>User</code> existantes</li>
     *                     <li>les propriétés d'un utilisateur</li>
     *                     <li>une propriété donnée d'un utilisateur donné</li>
     *                 </ul>
     * @throws ServletException Voir doc...
     * @throws IOException      Voir doc...
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("X-test", "doGet");
        String[] url = UrlUtils.getUrlParts(request);
        if (url.length == 1) { // Renvoie la liste de tous les todos
            request.setAttribute("todos", todoResource.readAll());
            // Transfère la gestion de l'interface à une JSP
            request.getRequestDispatcher("/WEB-INF/components/users.jsp").include(request, response);
            return;
        }
        try {
            Todo todo = todoResource.readOne(Integer.parseInt(url[1]));
            TodoResponseDto todoDto = todoMapper.toDto(todo);
            switch (url.length) {
                case 1 -> {
                    request.setAttribute("todos", todoDto);
                    request.getRequestDispatcher("/WEB-INF/components/user.jsp").include(request, response);
                }
                default -> { // Redirige vers l'URL qui devrait correspondre à la sous-propriété demandée (qu'elle existe ou pas ne concerne pas ce contrôleur)
                    if (url[2].equals("assignedTodos")) {
                        // Construction de la fin de l'URL vers laquelle rediriger
                        String urlEnd = UrlUtils.getUrlEnd(request, 3);
                        response.sendRedirect("todos" + urlEnd);
                    } else {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    }
                }
            }
        } catch (IllegalArgumentException ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        }
    }

    /**
     * Réalise la modification d'un utilisateur.
     * En fonction du login passé dans l'URL :
     * <ul>
     *     <li>création de l'utilisateur s'il n'existe pas</li>
     *     <li>Mise à jour sinon</li>
     * </ul>
     * Renvoie un code de statut 204 (No Content) en cas de succès ou une erreur HTTP appropriée sinon.
     *
     * @param request  Une requête dont l'URL est de la forme <code>/users/{login}</code>, et contenant :
     *                 <ul>
     *                     <li>un password (obligatoire en cas de création)</li>
     *                     <li>un nom (obligatoire en cas de création)</li>
     *                 </ul>
     * @param response Une réponse vide (si succès)
     * @throws IOException Voir doc...
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] url = UrlUtils.getUrlParts(request);
        String login = url[1];
        // TODO Parsing des paramètres "old school" ; sera amélioré dans la partie négociation de contenus...
        String password = request.getParameter("password");
        String name = request.getParameter("name");

        if (url.length == 2) {
            try {
                todoResource.update(login, password, name);
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } catch (IllegalArgumentException ex) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
            } catch (NameNotFoundException e) {
                try {
                    todoResource.create(login, password, name);
                    response.setHeader("Location", "users/" + login);
                    response.setStatus(HttpServletResponse.SC_CREATED);
                } catch (NameAlreadyBoundException ignored) {
                } catch (IllegalArgumentException | ForbiddenLoginException ex) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
                }
            } catch (InvalidNameException ignored) {
                // Ne devrait pas arriver car les paramètres sont déjà des Strings
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
        String login = url[1];
        if (url.length == 2) {
            try {
                todoResource.delete(login);
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } catch (IllegalArgumentException ex) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
            } catch (NameNotFoundException e) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "L'utilisateur " + login + " n'existe pas.");
            } catch (InvalidNameException ignored) {
                // Ne devrait pas arriver car les paramètres sont déjà des Strings
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Nested class qui interagit avec le DAO">
}
