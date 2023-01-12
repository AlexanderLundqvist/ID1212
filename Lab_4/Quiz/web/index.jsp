<%-- 
    Document   : index
    Author     : Alexander Lundqvist & Ramin Shojaei

    This is the login page for the game.   
--%>

<% String status = (String) request.getSession().getAttribute("SessionStatus"); %>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Quiz login</title>
        <!-- Bootstrap latest compiled and minified core CSS -->
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@3.3.7/dist/css/bootstrap.min.css" 
        integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
        <!-- Optional theme -->
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@3.3.7/dist/css/bootstrap-theme.min.css" 
        integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp" crossorigin="anonymous">
        <!-- Project styling -->
        <style>
        body {padding: 10px;}
        .error {color: red;}
        </style>
    </head>
    <body>
        <h2>ID1212: Lab 4 Quiz</h2>
        <h4>Welcome! Login to start a quiz.</h4>
        <%-- submit calls the SessionController to handle the login attempt --%>
        <form id="loginForm" action="SessionController" method="POST">
            <input name="username" type="text" placeholder="email" required/>
            <input name="password" type="password" placeholder="password" required/>
            <input name="login" type="submit"  value="Login"/>
        </form>
        <%-- Conditional rendering based on session attributes --%>
        <%  
            if (status != null && status.equals("Error")) {
                out.print("<div class=\"error\"><p>The username and password combination you wrote is invalid!</p></div>");
            }
        %>
    </body>
</html>
