<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Todos</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    <meta http-equiv="refresh" content="5">
</head>
<body>

<h2>Liste des todos</h2>
<ul>
    <c:forEach items="${model}" var="todoHash">
        <li>
            <a href="${request.getContextPath()}/todos/${applicationScope.todoDao.findByHash(todoHash).getAssignee()}">${todoHash}</a>
        </li>
    </c:forEach>
</ul>
</body>
</html>
