<%-- 
    Document   : index
    Author     : Alexander Lundqvist & Ramin Shojaei

    This is the login page for the game.   
--%>

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
        <form id="LoginForm" action="SessionController" method="GET">
            <input name="Username" type="text" placeholder="email" required/>
            <input name="Password" type="password" placeholder="password" required/>
            <input name="Login" type="submit" value="Login"/>
        </form>
        <%-- Conditional rendering based on session attributes --%>
        <%  
            String status = (String) request.getSession().getAttribute("SessionStatus");
            if (status != null && status.equals("Wrong")) {
                out.print("<div class=\"error\"><p>The username and password combination you wrote is invalid!</p></div>");
            }
            else if (status != null && status.equals("Error")) {
                out.print("<div class=\"error\"><p>Something went wrong!</p></div>");
            }
        %>
    </body>
</html>
