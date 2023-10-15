package fr.univlyon1.m1if.m1if03.filters;

import fr.univlyon1.m1if.m1if03.classes.User;
import fr.univlyon1.m1if.m1if03.daos.Dao;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InvalidNameException;
import javax.naming.NameNotFoundException;
import java.io.IOException;

/**
 * Filter of modification.
 * Do not authorize other user than yourself to modifie the username.
 *
 */
@WebFilter(filterName = "Modify", urlPatterns = {"/userlist.jsp"})
public class Modify extends HttpFilter {

    @Override
    public void init(FilterConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        if (request.getMethod().equals("POST")) {
            try {
                if(!request.getParameter("login").equals(request.getSession(false).getAttribute("login"))) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Vous devez vous connecter pour accéder au site." + request.getParameter("login"));
                }
                User user = ((Dao<User>) this.getServletContext().getAttribute("users")).findOne(request.getParameter("login"));
                user.setName(request.getParameter("name"));
            } catch (NullPointerException | InvalidNameException | NameNotFoundException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Login de l'utilisateur vide ou inexistant: " + request.getParameter("login") + ".");
                return;
            }
            // On redirige la totalité de l'interface pour afficher le nouveau nom dans l'interface
            response.sendRedirect("interface.jsp");
        } else {
            chain.doFilter(request, response);
        }
    }
}
