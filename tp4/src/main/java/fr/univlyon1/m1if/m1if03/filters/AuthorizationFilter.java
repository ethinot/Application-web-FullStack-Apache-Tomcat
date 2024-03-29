package fr.univlyon1.m1if.m1if03.filters;

import com.auth0.jwt.exceptions.TokenExpiredException;
import fr.univlyon1.m1if.m1if03.dao.TodoDao;
import fr.univlyon1.m1if.m1if03.dto.todo.TodoRequestDto;
import fr.univlyon1.m1if.m1if03.model.Todo;
import fr.univlyon1.m1if.m1if03.utils.UrlUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

/**
 * Filtre d'autorisation.
 * <ul>
 *     <li>Vérifie que l'utilisateur authentifié a le droit d'accéder à certaines ressources. Renvoie un code 403 (Forbidden) sinon.</li>
 *     <li>Vérifie si l'utilisateur authentifié a le droit d'afficher complètement certaines ressources. Positionne un attribut de requête en conséquence.</li>
 * </ul>
 *
 * @author Lionel Médini
 */
@WebFilter
public class AuthorizationFilter extends HttpFilter {

    // Liste des ressources pour lesquelles renvoyer un 403 si l'utilisateur n'est pas le bon
    private static final String[][] RESOURCES_WITH_AUTHORIZATION = {
            {"PUT", "users", "*"},
            {"POST", "users", "*", "password"},
            {"PUT", "users", "*", "password"},
            {"POST", "users", "*", "name"},
            {"PUT", "users", "*", "name"},
            {"*", "users", "*", "assignedTodos"},
            {"POST", "todos", "toggleStatus"},
            {"*", "todos", "*", "assignee"}
    };

    // Liste des ressources qui
    private static final String[][] RESOURCES_WITH_LIMITATIONS = {
            {"GET", "users", "*"},
            {"GET", "todos", "*"}
    };

    public void init(FilterConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        // Si l'utilisateur n'est pas authentifié (mais que la requête a passé le filtre d'authentification), c'est que ce filtre est sans objet
        if(request.getAttribute("user") == null) {
            chain.doFilter(request, response);
            return;
        }

        TodoDao todoDao = (TodoDao) getServletContext().getAttribute("todoDao");
        String[] url = UrlUtils.getUrlParts(request);

        // S'il faut un attribut pour décider plus tard de l'affichage, par exemple d'une partie de la ressource.
        if (Stream.of(RESOURCES_WITH_LIMITATIONS).anyMatch(pattern -> UrlUtils.matchRequest(request, pattern))) {
            if (url[0].equals("users")) {
                try {
                    boolean isAuthorized = request.getAttribute("user").equals(url[1]);
                    request.setAttribute("authorizedUser", isAuthorized);
                } catch (TokenExpiredException e) {
                    sendErrorWithoutCommited(response, HttpServletResponse.SC_UNAUTHORIZED, "Le token " + e + " à expiré !");
                }
            } else if (url[0].equals("todos")) {
                try {
                    boolean isAuthorized = ((List<Integer>) request.getAttribute("todos")).contains(Integer.parseInt(url[1]));
                    request.setAttribute("authorizedUser", isAuthorized);
                }  catch (TokenExpiredException e) {
                    sendErrorWithoutCommited(response, HttpServletResponse.SC_UNAUTHORIZED, "Le token " + e + " à expiré !");
                }
            }
        }

        // Application du filtre
        if (Stream.of(RESOURCES_WITH_AUTHORIZATION).anyMatch(pattern -> UrlUtils.matchRequest(request, pattern))) {
            switch (url[0]) {
                case "users" -> {
                    try {
                        if (request.getAttribute("user").equals(url[1])) {
                            chain.doFilter(request, response);
                        } else {
                            sendErrorWithoutCommited(response, HttpServletResponse.SC_FORBIDDEN, "Vous n'avez pas accès aux informations de cet utilisateur.");
                        }
                    } catch (TokenExpiredException e) {
                        sendErrorWithoutCommited(response, HttpServletResponse.SC_UNAUTHORIZED, "Le token " + e + " à expiré !");
                    }
                }
                case "todos" -> {
                    try {
                        Todo todo;

                        if (url[1].equals("toggleStatus")) {
                            TodoRequestDto requestDto = (TodoRequestDto) request.getAttribute("dto");
                            todo = todoDao.findByHash(requestDto.getHash());
                        } else {
                            todo = todoDao.findByHash(Integer.parseInt(url[1]));
                        }
                        if (todo.getAssignee() != null && todo.getAssignee().equals(request.getAttribute("user"))) {
                            chain.doFilter(request, response);
                        } else {
                            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Vous n'êtes pas assigné.e ce ce todo !");
                        }
                    } catch (IllegalArgumentException e) {
                        sendErrorWithoutCommited(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
                    } catch (TokenExpiredException e) {
                        sendErrorWithoutCommited(response, HttpServletResponse.SC_UNAUTHORIZED, "Le token " + e + " à expiré !");
                    } catch (NoSuchElementException e) {
                        sendErrorWithoutCommited(response, HttpServletResponse.SC_NOT_FOUND, "Le todo " + url[1] + " n'existe pas.");
                    }
                }
                default -> // On laisse Tomcat générer un 404.
                        chain.doFilter(request, response);
            }
        } else {
            chain.doFilter(request, response);
        }
    }
    private void sendErrorWithoutCommited(HttpServletResponse response, int status, String message) throws IOException {
        if (!response.isCommitted()) {
            response.sendError(status, message);
        }
    }
}
