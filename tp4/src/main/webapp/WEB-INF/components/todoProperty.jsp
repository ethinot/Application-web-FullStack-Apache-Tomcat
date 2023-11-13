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
<c:set var="todo" value="${model}" scope="request"/>
<head>
    <title>Todo property</title>
    <p>${todo.getHash()} , ${todo.getTitle()}, UserAssigne : <a href="${pageContext.request.contextPath}/users/${todo.getAssignee()}">${todo.getAssignee()}</a>, ${todo.getCheckBox()}, ${todo.getCompleted()}</p>
</head>
</body>
</html>
