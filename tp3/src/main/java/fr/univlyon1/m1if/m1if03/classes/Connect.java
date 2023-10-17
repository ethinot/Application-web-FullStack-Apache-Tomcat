package fr.univlyon1.m1if.m1if03.classes;

import fr.univlyon1.m1if.m1if03.daos.Dao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.naming.InvalidNameException;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import java.io.IOException;

/**
 * Interface that implement methode for connecting and disconnecting users.
 *
 */
public interface Connect {

    /**
     * Method used to create and add a new user to UsersMap.
     * This method also create the user session.
     *
     * @param request the HTTP/HTTPS request
     * @param response the HTTP/HTTPS response we want to build
     * @throws IOException throw exception if IN/OUT outputs are incorrect
     */
    static void connect(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Dao<User> users = (Dao<User>) request.getServletContext().getAttribute("users");
            // Gestion de la création de l'utilisateur
            try {
                users.add(new User(request.getParameter("login"), request.getParameter("name")));
            } catch (NameAlreadyBoundException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "The key used for add the new user is already used.");
            }
            // Gestion de la session utilisateur
            String login = request.getParameter("login");
            HttpSession session = request.getSession(true);
            session.setAttribute("login", login);
            // On envoie juste le user en question a interface.jsp
            request.setAttribute("user", users.findOne(request.getParameter("login")));
            request.getRequestDispatcher("/WEB-INF/components/interface.jsp").include(request, response);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Format de l'index du User incorrect.");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Method used to disconnect a user and delete him/her from the UsersMap.
     *
     * @param request the HTTP/HTTPS request
     * @param response the HTTP/HTTPS response we want to build
     * @throws IOException throw exception if IN/OUT outputs are incorrect
     */
    static void disconnect(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        String login = null;
        try {
            login = (String) session.getAttribute("login");
            session.invalidate();
            ((Dao<User>) request.getServletContext().getAttribute("users")).deleteById(login);
            response.setHeader("Refresh", "0; URL="+ request.getContextPath() + "/index.html");
        } catch (NameNotFoundException | InvalidNameException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Le login de l'utilisateur courant est erroné : " + login + ".");
        }
    }
}
