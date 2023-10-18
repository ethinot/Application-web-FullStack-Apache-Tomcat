package fr.univlyon1.m1if.m1if03.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
        String url = request.getRequestURI().replace(request.getContextPath(), "");

        String login = request.getParameter("login");

        if (url.equals("/user.jsp") && request.getMethod().equals("POST") && login != null && !login.isEmpty()) {
            if (!request.getParameter("login").equals(request.getSession(false).getAttribute("login"))) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, new StringBuilder()
                        .append("Vous n'avez pas le droit de modifier une autre utilisateur que vous ")
                        .append(request.getParameter("login")).toString());
                return;
            }
            chain.doFilter(request, response);
            return;
        // Let pass GET request to display userlist for authenticated users
        } else if (request.getSession(false) != null) {
            chain.doFilter(request, response);
            return;
        }
    }
}
