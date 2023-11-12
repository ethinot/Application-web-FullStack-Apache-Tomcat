package fr.univlyon1.m1if.m1if03.filters;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import fr.univlyon1.m1if.m1if03.utils.TodosM1if03JwtHelper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Filtre d'authentification.
 * Vérifie qu'une requête est authentifiée, sauf requêtes autorisées.<br>
 * Autorise les requêtes suivantes :
 * <ol>
 *     <li>URLs ne nécessitant pas d'authentification (whitelist), y compris les requêtes d'authentification</li>
 *     <li>Requêtes d'utilisateurs déjà authentifiés</li>
 * </ol>
 * Dans les cas contraires, renvoie un code d'erreur HTTP 401 (Unauthorized).
 *
 * @author Lionel Médini
 */
@WebFilter
public class AuthenticationFilter extends HttpFilter {
    private static final String[] WHITELIST = {"/", "/index.html", "/login.html", "/css/style.css", "/users", "/users/", "/users/login"};

   @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        // Permet de retrouver la fin de l'URL (après l'URL du contexte) -> indépendant de l'URL de déploiement
        String url = request.getRequestURI().replace(request.getContextPath(), "");

        // 1) Laisse passer les URLs ne nécessitant pas d'authentification
        for(String tempUrl: WHITELIST) {
            if(url.equals(tempUrl)) {
                chain.doFilter(request, response);
                return;
            }
        }

        // 2) Traite les requêtes qui doivent être authentifiées
        String authorizationHeader = request.getHeader("Authorization"); // accède au token
        try {
            if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7); // length of "Bearer" for getting the real token
                String login = TodosM1if03JwtHelper.verifyToken(token, request);
                List<Integer> todos = TodosM1if03JwtHelper.getAssigned(token, login);
                if (login != null) {
                    request.setAttribute("user", login);
                    request.setAttribute("todos", todos);
                }
                chain.doFilter(request, response);
                return;
            }
        } catch (NullPointerException e) {
            sendErrorWithoutCommited(response, HttpServletResponse.SC_UNAUTHORIZED, "Le token " + e + " n'existe pas !");
        } catch (TokenExpiredException e) {
            sendErrorWithoutCommited(response, HttpServletResponse.SC_UNAUTHORIZED, "Le token " + e + " à expiré !");
        } catch (JWTVerificationException e) {
            sendErrorWithoutCommited(response, HttpServletResponse.SC_UNAUTHORIZED, "Le token " + e + " est invalide !");
        }

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Vous devez vous connecter pour accéder au site.");
    }
    private void sendErrorWithoutCommited(HttpServletResponse response, int status, String message) throws IOException {
       if (!response.isCommitted()) {
           response.sendError(status, message);
       }
    }
}

