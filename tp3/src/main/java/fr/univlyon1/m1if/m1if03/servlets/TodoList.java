package fr.univlyon1.m1if.m1if03.servlets;


import fr.univlyon1.m1if.m1if03.classes.TodoOperation;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Cette servlet gère la liste des TODOs.
 * Elle permet actuellement d'afficher la liste et de créer de nouveaux TODOs.
 * Elle devra aussi permettre de modifier l'état d'un TODO_.
 *
 * @author Lionel Médini
 */
@WebServlet(name = "TodoList", value = "/todolist")
public class TodoList extends HttpServlet implements TodoOperation {
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.getRequestDispatcher("todolist.jsp").include(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        switch (request.getParameter("operation")) {
            case "add" -> {
                TodoOperation.add(request, response);
            }
            case "update" -> {
                TodoOperation.update(request, response);
            }
            default -> throw new UnsupportedOperationException("Opération à réaliser non prise en charge.");
        }
        doGet(request, response);
    }
}
