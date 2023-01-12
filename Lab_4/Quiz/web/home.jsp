<%-- 
    Document   : home
    Author     : Alexander Lundqvist & Ramin Shojaei

    This is the landing page. After login, the user can chose a quiz from here
    or logout.  
--%>


<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="model.UserBean"%>
<%@page import="model.QuestionBean"%>
<%@page import="model.QuizBean"%>
<%@page import="integration.DBhandler"%>
<%  
    DBhandler database = new DBhandler();
    String[] subjects = database.getSubjects();
    UserBean user = (UserBean) request.getSession().getAttribute("UserBean");
    QuizBean quiz = (QuizBean) request.getSession().getAttribute("Quiz");
    HashMap<String, Integer> statistics = user.getStatistics();
    
%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Quiz home page</title>
        <!-- Bootstrap latest compiled and minified core CSS -->
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@3.3.7/dist/css/bootstrap.min.css" 
        integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
        <!-- Optional theme -->
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@3.3.7/dist/css/bootstrap-theme.min.css" 
        integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp" crossorigin="anonymous">
        <!-- Project styling -->
        <style>
        body {padding: 10px;}
        .score {color: red;}
        .subject {margin-bottom: 5px;}
        </style>
    </head>
    <body>
        <h2>Home</h2>
        <p>Here you can choose a quiz to do or log out if you are done.</p>
        <form id="quizMenuForm" action="GameController" method="GET">
            <%
                for (int i = 0; i < subjects.length; i++) {
                    out.print("<input name=\"subject\" class=\"subject\" type=\"submit\" value=" + subjects[i] +"><br>");
                }
            %>
        </form>
        <br>
        <%  
            out.print("<h4>Current statistics</h4>");
            for (int i = 0; i < subjects.length; i++) {
                out.print("<p>" + subjects[i] + ": " + 0 + "/" + 3 + "</p>");
            }
        %>
        <br>
        <form id="logoutForm" action="SessionController" method="GET">
            <input name="logout" type="submit"  value="Logout"/>
        </form>
    </body>
</html>
