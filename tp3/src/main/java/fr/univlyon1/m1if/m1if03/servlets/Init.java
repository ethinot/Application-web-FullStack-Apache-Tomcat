package fr.univlyon1.m1if.m1if03.servlets;

import fr.univlyon1.m1if.m1if03.daos.TodoDao;
import fr.univlyon1.m1if.m1if03.daos.UserDao;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;

/**
 * Servlet that is used to initialize DOA.
 *
 */
@WebServlet(name = "Init", urlPatterns = {"/init"}, loadOnStartup = 1)
public class Init extends HttpServlet {

    /**
     * Function that init the two DAO for storing users and todos.
     *
     * @param config actuel servelte configuration use to pass information to a servlet during initialization.
     * @throws ServletException general exception a servlet can throw when it encounters difficulty.
     */
    public void init(ServletConfig config) throws ServletException {

        super.init(config);

        ServletContext context = config.getServletContext();

        context.setAttribute("users", new UserDao());
        context.setAttribute("todos", new TodoDao());
    }
}
