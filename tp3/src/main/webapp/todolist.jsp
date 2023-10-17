<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="fr.univlyon1.m1if.m1if03.daos.TodoDao" %>
<%@ page import="fr.univlyon1.m1if.m1if03.classes.Todo" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>TODOs</title>
    <link rel="stylesheet" href="css/style.css">
    <meta http-equiv="refresh" content="5">
</head>
<body>
<h2>Liste des TODOs</h2>
<table>
    <tr>
        <th></th>
        <th>Titre</th>
        <th>Utilisateur assigné</th>
    </tr>
    <c:forEach items="${requestScope.todos}" var="todo">
    <form method="POST" action="todolist">
        <tr id="${todo.hashCode()}">
            <td>${todo.isCompleted() ? "&#x2611;" : "&#x2610;"}</td>
            <td><em>${todo.getTitle()}</em></td>
            <td>
                <c:if test="${todo.getUserAssigne() != null}"><a href="users?user=${todo.getUserAssigne()}">${todo.getUserAssigne()}</a></c:if>
                <c:if test="${!todo.isCompleted() && todo.getUserAssigne() != sessionScope.login}">
                    <input type='submit' name='assign' value='Choisir cette tâche'>&nbsp;
                </c:if>
            </td>
            <td>
                <!--TODO Implement this function -->
                <input type='submit' name='toggle' value='${todo.isCompleted() ? "Not done!" : "Done!"}'>
            </td>
        </tr>
        <input type='hidden' name='operation' value='update'>
        <input type='hidden' name='index' value='${requestScope.todos.indexOf(todo)}'>
    </form>
    </c:forEach>
</table>
<script>
    if(location.hash) {
        document.getElementById(location.hash.substring(1)).style = "color: red";
    }
</script>
</body>
</html>