package fr.univlyon1.m1if.m1if03.controllers;

import fr.univlyon1.m1if.m1if03.controllers.resources.UserBusiness;
import fr.univlyon1.m1if.m1if03.dao.UserDao;
import fr.univlyon1.m1if.m1if03.dto.user.UserRequestDto;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InvalidNameException;
import javax.naming.NameNotFoundException;
import java.io.IOException;

/**
 * Contrôleur d'opérations métier "users".<br>
 * Concrètement : gère les opérations de login et de logout.
 *
 * @author Lionel Médini
 */
@WebServlet(name = "UserBusinessController", urlPatterns = {"/users/login", "/users/logout"})
public class UserBusinessController extends HttpServlet {
    private UserBusiness userBusiness;
    private UserRequestDto userRequestDto;

    //<editor-fold desc="Méthode de gestion du cycle de vie">
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        UserDao userDao = (UserDao) config.getServletContext().getAttribute("userDao");
        userBusiness = new UserBusiness(userDao);
    }
    //</editor-fold>

    //<editor-fold desc="Méthode de service">
    /**
     * Réalise l'opération demandée en fonction de la fin de l'URL de la requête (<code>/users/login</code> ou <code>/users/logout</code>).
     * Renvoie un code HTTP 204 (No Content) en cas de succès.
     * Sinon, renvoie une erreur HTTP appropriée.
     *
     * @param request  Voir doc...
     * @param response Voir doc...
     * @throws IOException Voir doc...
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getRequestURI().endsWith("login")) {
            userRequestDto = (UserRequestDto) request.getAttribute("dto");
            if (userRequestDto.getLogin() != null && !userRequestDto.getLogin().isEmpty()) {
                try {
                    if (userBusiness.userLogin(userRequestDto.getLogin(), userRequestDto.getPassword(), request)) {
                        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                    } else {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Les login et mot de passe ne correspondent pas.");
                    }
                } catch (IllegalArgumentException ex) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
                } catch (NameNotFoundException e) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "L'utilisateur " + userRequestDto.getLogin() + " n'existe pas.");
                } catch (InvalidNameException ignored) {
                    // Ne doit pas arriver car les logins des utilisateurs sont des Strings
                }
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else if (request.getRequestURI().endsWith("logout")) {
            userBusiness.userLogout(request);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            // Ne devrait pas arriver mais sait-on jamais...
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
