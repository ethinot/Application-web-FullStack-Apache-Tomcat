package fr.univlyon1.m1if.m1if03.controllers;

import fr.univlyon1.m1if.m1if03.controllers.resources.TodoBusiness;
import fr.univlyon1.m1if.m1if03.dao.TodoDao;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * Contrôleur d'opérations métier "users".<br>
 * Concrètement : gère les opérations de login et de logout.
 *
 * @author Lionel Médini
 */
@WebServlet(name = "TodoBusinessController", urlPatterns = {"/todos/toggleStatus"})
public class TodoBusinessController extends HttpServlet {

    private TodoBusiness todoBusiness;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        TodoDao todoDao = (TodoDao) config.getServletContext().getAttribute("todoDao");
        todoBusiness = new TodoBusiness(todoDao);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getRequestURI().endsWith("toggleStatus")) {
            try {
                int todoHash = Integer.parseInt(request.getParameter("hash"));
                todoBusiness.modifyStatus(todoHash);
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } catch (NumberFormatException ex1) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "L'id du todo est syntaxiquement incorrect.");
            } catch (IllegalArgumentException ex2) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex2.getMessage());
            } catch (NoSuchElementException ex3) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Le todo avec cet id n'existe pas.");
            }
        } else {
            // Ne devrait pas arriver mais sait-on jamais...
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
