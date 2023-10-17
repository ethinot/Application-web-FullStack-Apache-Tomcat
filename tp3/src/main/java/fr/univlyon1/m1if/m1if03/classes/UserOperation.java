package fr.univlyon1.m1if.m1if03.classes;

import fr.univlyon1.m1if.m1if03.daos.AbstractMapDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Interface that implement methode for find a user in DOA object users.
 *
 */
public interface UserOperation {

    /**
     * Method used for adding a new Todo objet to the todos DAO.
     *
     * @param request the HTTP/HTTPS request
     * @param response the HTTP/HTTPS response we want to build
     * @throws IOException throw exception if IN/OUT outputs are incorrect
     */
    static void returnUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            AbstractMapDao<User> users = (AbstractMapDao<User>) request.getServletContext().getAttribute("users");
            User user = users.findOne((String) request.getSession(false).getAttribute("login"));
            request.setAttribute("user", user);
            String redirectURL = "user.jsp?user=" + user.getLogin();
            request.getRequestDispatcher(redirectURL).include(request, response);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Format de l'index du User incorrect.");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}
