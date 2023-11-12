<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Users</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    <meta http-equiv="refresh" content="5">
</head>
<body>

<c:set var="todo" value="${model}" scope="request"/>

<h2>Todos ${todo.getHash()}</h2>

<p>${todo.getTitle()}, UserAssigne : ${todo.getAssignee()}, ${todo.getCheckBox()}, ${todo.getCompleted()}</p>

</body>
</html>
