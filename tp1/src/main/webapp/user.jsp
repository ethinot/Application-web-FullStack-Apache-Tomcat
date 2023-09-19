<%--
  Created by IntelliJ IDEA.
  User: tomde
  Date: 19/09/2023
  Time: 17:46
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Modification du compte</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<h1>Modification du compte</h1>
<form method="post" action="todos">
    <jsp:useBean id="user" scope="session" type="fr.univlyon1.m1if.m1if03.classes.User" />
    <label for="login">Login :</label>
    <input id="login" name="login" type="text" readonly="readonly" value=" <%= user.getLogin() %>">
    <br/>
    <label for="username">Username : </label>
    <input id="username" name="name" type="text"  value=" <%= user.getName() %>">
</form>
</body>
</html>
