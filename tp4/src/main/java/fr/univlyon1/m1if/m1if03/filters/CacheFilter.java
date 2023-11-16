package fr.univlyon1.m1if.m1if03.filters;

import fr.univlyon1.m1if.m1if03.dao.TodoDao;
import fr.univlyon1.m1if.m1if03.dao.UserDao;
import fr.univlyon1.m1if.m1if03.model.Todo;
import fr.univlyon1.m1if.m1if03.model.User;
import fr.univlyon1.m1if.m1if03.utils.UrlUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.NameNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import java.text.DateFormat;


/**
 * A cache filter for storing list of Todos for now.
 *
 */
@WebFilter(filterName = "CacheFilter")
public class CacheFilter extends HttpFilter {

    // Map qui stocke les dates des dernières modifications des todos identifiés par le ID
    // private Map<String, Date> todosHistory;
    private Map<String, String> eTags;
    private final Date lastDate = new Date();

    private TodoDao todoDao;
    private UserDao userDao;

    @Override
    public void init(FilterConfig config) throws ServletException {
        super.init(config);
        //todosHistory = new HashMap<>();
        eTags = new HashMap<>();
        //todosHistory.put("todos", new Date());
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String[] url = UrlUtils.getUrlParts(request);
        String path = request.getRequestURI();
        todoDao = (TodoDao) this.getServletContext().getAttribute("todoDao");
        userDao = (UserDao) this.getServletContext().getAttribute("userDao");

        switch (url.length) {
            case 1 -> { // case for /todos
                switch (request.getMethod()) {
                    case "GET" -> {
                        if (url[0].equals("todos")) {
                            String ifModifiedSince = request.getHeader("If-Modified-Since");
                            if (ifModifiedSince != null && ifModifiedSince.equals(DateFormat.getDateTimeInstance().format(lastDate))) {
                                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                            }
                        }
                        response.setHeader("Last-Modified", DateFormat.getDateTimeInstance().format(lastDate));
                        chain.doFilter(request, response);
                    }
                    case "POST", "PUT", "DELETE" -> {
                        if (url[0].equals("todos")) {
                            chain.doFilter(request, response);
                            lastDate.setTime(new Date().getTime());
                        }
                    }
                    default -> {
                        response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                    }
                }
            }
            case 2 -> { // case for /todos/todoId, /users/userId and /user/login
                switch (request.getMethod()) {
                    case "GET" -> {
                        if (url[0].equals("todos")) {
                            String ifModifiedSince = request.getHeader("If-Modified-Since");
                            if (ifModifiedSince != null && ifModifiedSince.equals(DateFormat.getDateTimeInstance().format(lastDate))) {
                                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                            }
                            response.setHeader("Last-Modified", DateFormat.getDateTimeInstance().format(lastDate));
                            chain.doFilter(request, response);
                        }

                        if (url[0].equals("users")) {
                            String userLogin = url[1];
                            String ifNoneMatch = request.getHeader("If-None-Match");
                            String eTag = createETag(userLogin);
                            response.setHeader("ETag", eTag);
                            if (ifNoneMatch != null && ifNoneMatch.equals(eTag)) {
                                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                            }
                        }
                        chain.doFilter(request, response);
                    }
                    case "POST", "PUT", "DELETE" -> {
                        if (url[0].equals("todos")) {
                            if (url[0].equals("todos")) {
                                chain.doFilter(request, response);
                                lastDate.setTime(new Date().getTime());
                            }
                        }
                        if (url[0].equals("users")) {
                            String user = url[1];
                            updateETag(user);
                        }
                    }
                    default -> {
                        response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                    }
                }
            }
            default -> {

            }
        }
        chain.doFilter(request, response);
    }

    private String getETag(String user) {
        String eTag = eTags.get(user);
        if (eTag == null) {
            eTag = createETag(user);
            eTags.put(user, eTag);
        }
        return eTag;
    }

    private String createETag(String user){
        List<Todo> userTodo = todoDao.findByAssignee(user);
        User userLogin = null;
        String eTag = null;
        try {
            userLogin = userDao.findByLogin(user);
        } catch (NameNotFoundException e) {

        }

        StringBuilder userTodoString = new StringBuilder();
        for (Todo todo : userTodo) {
            userTodoString.append(todo.hashCode()).append(" ").append(todo.getCheckBox());
        }

        if (userLogin != null) {
            eTag = userLogin.getName().hashCode()+user.hashCode()+userTodo.toString().hashCode() + userTodoString.toString();
        }
        return eTag;

    }

    private void updateETag(String user) {
        eTags.put(user, createETag(user));
    }
}





