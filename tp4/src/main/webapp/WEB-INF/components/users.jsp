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

<h2>Liste des utilisateurs</h2>
<p>Il y a actuellement ${model.size()} utilisateur(s) :</p>
<ul>
    <c:forEach items="${model}" var="user">
        <li>${user.login} : ${user.name}</a></strong></li>
    </c:forEach>
</ul>
</body>
</html>
