<%-- 
    Document   : quiz
    Author     : Alexander Lundqvist & Ramin Shojaei

    This is an active quiz page. The user has to perform the quiz then submit it
    to return to the home page.
--%>

<%@page import="java.util.ArrayList"%>
<%@page import="model.UserBean"%>
<%@page import="model.QuestionBean"%>
<%@page import="model.QuizBean"%>
<%  
    UserBean user = (UserBean) request.getSession().getAttribute("UserBean");
    QuizBean quiz = (QuizBean) request.getSession().getAttribute("Quiz");
    ArrayList<QuestionBean> questions = quiz.getQuestions();
    String subject = quiz.getSubject();
%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Quiz</title>
        <!-- Bootstrap latest compiled and minified core CSS -->
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@3.3.7/dist/css/bootstrap.min.css" 
        integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
        <!-- Optional theme -->
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@3.3.7/dist/css/bootstrap-theme.min.css" 
        integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp" crossorigin="anonymous">
        <!-- Project styling -->
        <style>
        body {padding: 10px;}
        input {border-right: 10px;}
        </style>
    </head>
    <body>
        <% out.print("<h2>" + subject + "</h2>"); %>
        <% 
            out.print("<form id=\"quiz\" action=\"GameController\" method=\"POST\">");
            for (int i = 0; i < questions.size(); i++) {
                out.print("<h4>Question " + (i+1) + "</h4>");
                out.print("<p>" + questions.get(i).getQuestion() + "</p>");
                String[] options = questions.get(i).getOptions();
                for (int j = 0; j < options.length; j++) {
                    out.print("<input name=\"question" + (i+1) + "\" type=\"radio\" value=\"" + options[j] + "\"/>" + options[j] + "<br>");
                } 
            }
            out.print("<br><input name=\"submitQuiz\" type=\"submit\"  value=\"Submit\"/>");
            out.print("</form>");
        %>
    </body>
</html>
