<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>User</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
<c:set var="user" value="${model}" scope="request"/>

<h1>Utilisateur ${user.getLogin()}</h1>
<ul>
    <li>Login : ${user.getLogin()}</li>
    <li>Nom : ${user.getName()}</li>
    <c:if test="${requestScope.authorizedUser}">
    <li>
        Todos:
        <ul>
        <c:forEach items="${user.getAssignedTodos()}" var="todo">
            <li>
                <a href="${pageContext.request.contextPath}/todos/${applicationScope.todoDao.findByHash(todo).getAssignee()}">ID ${todo} ${applicationScope.todoDao.findByHash(todo).title}</a>
            </li>
        </c:forEach>
        </ul>
    </li>
    </c:if>
</ul>
</body>
</html>
