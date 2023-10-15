package fr.univlyon1.m1if.m1if03.servlets;

import fr.univlyon1.m1if.m1if03.classes.User;

import fr.univlyon1.m1if.m1if03.daos.Dao;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.naming.InvalidNameException;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import java.io.IOException;

/**
 * Cette servlet initialise les objets communs à toute l'application,
 * récupère les infos de l'utilisateur pour les placer dans sa session
 * et affiche l'interface du chat.
 *
 * @author Lionel Médini
 */
@WebServlet(name = "Connect", urlPatterns = {"/connect"})
public class Connect extends HttpServlet {
    // Elles seront stockées dans le contexte applicatif pour pouvoir être accédées par tous les objets de l'application :

    // DAO d'objets User
    private Dao<User> users;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            Dao<User> users = (Dao<User>) this.getServletContext().getAttribute("users");
            switch (request.getParameter("operation")) {
                case "add" -> {
                    // Gestion de la création de l'utilisateur
                    User newUser = new User(request.getParameter("login"), request.getParameter("name"));
                    try {
                        users.add(new User(request.getParameter("login"), request.getParameter("name")));
                    } catch (NameAlreadyBoundException e) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "The key used for add the new user is already used.");
                    }
                    // Gestion de la session utilisateur
                    String login = request.getParameter("login");
                    HttpSession session = request.getSession(true);
                    session.setAttribute("login", login);
                    response.sendRedirect("interface.jsp");
                }
                case "del" -> {
                    doGet(request, response);
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
        HttpSession session = request.getSession(false);
        String login = null;
        try {
            login = (String) session.getAttribute("login");
            session.invalidate();
            ((Dao<User>) this.getServletContext().getAttribute("users")).deleteById(login);
            response.sendRedirect("index.html");
        } catch (NameNotFoundException | InvalidNameException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Le login de l'utilisateur courant est erroné : " + login + ".");
        }
    }
}
