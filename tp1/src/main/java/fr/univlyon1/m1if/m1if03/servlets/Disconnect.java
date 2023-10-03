package fr.univlyon1.m1if.m1if03.servlets;

//import fr.univlyon1.m1if.m1if03.classes.User;

import jakarta.servlet.ServletConfig;
//import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;

/**
 * Cette servlet initialise les objets communs à toute l'application,
 * récupère les infos de l'utilisateur pour les placer dans sa session
 * et affiche l'interface du chat (sans modifier l'URL).
 * &Agrave; noter le fait que l'URL à laquelle elle répond ("/todos") n'est pas le nom de la servlet.
 */
@WebServlet(name = "Disconnect", value = "/deconnexion")
public class Disconnect extends HttpServlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
        // Cette instruction doit toujours être au début de la méthode init() pour pouvoir accéder à l'objet config.
        super.init(config);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Récupère l'User dans l'attribut de session et le place dans la map (qui est un attribut de contexte)
        // request.getSession(false) permet de récupérer null si la session n'est pas déjà créée ce qui n'est surement pas le cas pour la déconnection
//        HttpSession session = request.getSession(false);
//        session.invalidate();
//        response.sendRedirect("login.jsp");
//        users.put(user.getLogin(), user);
//        // Change the username on modification
//        String userName = request.getParameter("name");
//        if (!user.getName().equals(userName)) {
//            user.setName(userName);
//        }
//        // Utilise un RequestDispatcher pour "transférer" la requête à un autre objet, en interne du serveur.
//        // Ceci n'est pas une redirection HTTP ; le client n'est pas informé de cette redirection.
//        // Note :
//        //     il existe deux méthodes pour transférer une requête (et une réponse) à l'aide d'un RequestDispatcher : include et forward
//        //     voir les différences ici : https://docs.oracle.com/javaee/6/tutorial/doc/bnagi.html
//        request.getRequestDispatcher("interface.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        session.invalidate(); // "Delete" it by invalidating it.
        response.sendRedirect("index.html");
    }
}
