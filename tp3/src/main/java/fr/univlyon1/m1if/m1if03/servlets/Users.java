package fr.univlyon1.m1if.m1if03.servlets;

import fr.univlyon1.m1if.m1if03.classes.Connect;
import fr.univlyon1.m1if.m1if03.classes.UserOperation;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * This Servlet act as a controller for all actions like to users.
 *
 *
 */
@WebServlet(name = "Users", value = "/users")
public class Users extends HttpServlet implements Connect, UserOperation {
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            switch (request.getParameter("operation")) {
                case "add" -> {
                    Connect.connect(request, response);
                }

                default -> throw new UnsupportedOperationException("Opération à réaliser non prise en charge.");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Format de l'index du User incorrect.");
            return;
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        final String actualURL = request.getRequestURI().replace(request.getContextPath(), "");
        switch (actualURL) {
            case "/index.html" -> {
                Connect.disconnect(request, response);
            }
            default -> UserOperation.returnUser(request, response);
        }

    }
}
