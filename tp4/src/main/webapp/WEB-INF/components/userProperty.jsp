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
<head>
    <title>User property</title>
    <p>${user.getLogin()} ${user.getName()} ${user.getAssignedTodos()}</p>
</head>
</body>
</html>
