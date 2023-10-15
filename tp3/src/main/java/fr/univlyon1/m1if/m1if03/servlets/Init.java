package fr.univlyon1.m1if.m1if03.servlets;

import fr.univlyon1.m1if.m1if03.classes.Todo;
import fr.univlyon1.m1if.m1if03.classes.User;
import fr.univlyon1.m1if.m1if03.daos.Dao;
import fr.univlyon1.m1if.m1if03.daos.TodoDao;
import fr.univlyon1.m1if.m1if03.daos.UserDao;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;

@WebServlet(name = "Init", urlPatterns = {"/    "}, loadOnStartup = 1)
public class Init extends HttpServlet {

    public void init(ServletConfig config) throws ServletException {

        super.init(config);

        ServletContext context = config.getServletContext();

        context.setAttribute("users", new UserDao());
        context.setAttribute("todos", new TodoDao());
    }
}
