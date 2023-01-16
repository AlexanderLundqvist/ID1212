<%-- 
    Document   : home
    Author     : Alexander Lundqvist & Ramin Shojaei

    This is the landing page. After login, the user can chose a quiz from here
    or logout.  
--%>

<%@page import="model.UserBean"%>
<%@page import="java.util.ArrayList"%>
<%@page import="model.QuizBean"%>
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
        .subject {margin-bottom: 5px;}
        .score {color: red;}
        .error {color: red;}
        </style>
    </head>
    <body>
        <h2>Home</h2>
        <p>Here you can choose a quiz to do or log out if you are done.</p>
        <% 
            ArrayList<QuizBean> quizzes = (ArrayList<QuizBean>) request.getSession().getAttribute("Quizzes");
            if (quizzes == null || quizzes.isEmpty()) {
                out.print("<div class=\"error\"><p>Something went wrong!</p></div>");
            }
            else {
                out.print("<form id=\"quizMenuForm\" action=\"GameController\" method=\"GET\">");
                for (QuizBean quiz : quizzes) {
                    String subject = quiz.getSubject();
                    out.print("<input name=\"Subject\" class=\"subject\" type=\"submit\" value=" + subject +"><br>");
                }
                out.print("</form><br>");
                
                UserBean user = (UserBean) request.getSession().getAttribute("UserBean");
                ArrayList<Integer> results = user.getResults();
                out.print("<h4>Current statistics</h4>");
                for (int i = 0; i < results.size(); i++) {
                    out.print("<p>" + quizzes.get(i).getSubject() + ": " + 0 + "/" + quizzes.get(i).getQuestions().size() + "</p>");
                }
            }
        %>
        <br>
        <form id="LogoutForm" action="SessionController" method="GET">
            <input name="Logout" type="submit"  value="Logout"/>
        </form>
    </body>
</html>
