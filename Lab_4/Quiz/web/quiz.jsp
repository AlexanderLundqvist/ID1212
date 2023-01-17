<%-- 
    Document   : quiz
    Author     : Alexander Lundqvist & Ramin Shojaei

    This is an active quiz page. The user has to perform the quiz then submit it
    to return to the home page.
--%>


<%@page import="model.QuizBean"%>
<%@page import="model.QuestionBean"%>
<%@page import="java.util.ArrayList"%>
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
        input {margin-right: 10px;}
        .error {color: red;}
        </style>
    </head>
    <body>
        <%
            QuizBean quiz = (QuizBean) request.getSession().getAttribute("ActiveQuiz");
            ArrayList<QuestionBean> questions = quiz.getQuestions();
            
            if (quiz.getSubject() != null) {out.print("<h2>" + quiz.getSubject() + "</h2>");}
            out.print("<form class=\"NavForm\" action=\"GameController\" method=\"GET\">");
            out.print("<input name=\"Nav\" type=\"submit\" value=\"Home\"/>");
            out.print("</form>");
            
            if (quiz == null || questions.isEmpty()) {           
                out.print("<div class=\"error\"><p>Something went wrong!</p></div>");
            }
            else {
                out.print("<form id=\"quiz\" action=\"GameController\" method=\"POST\">");
                for (QuestionBean question : questions) {
                    out.print("<h4>Question " + question.getId() + "</h4>");
                    out.print("<p>" + question.getQuestion() + "</p>");
                    ArrayList<String> options = question.getOptions();
                    for (String option : options) {
                        out.print("<input name=\"question" + question.getId() + "\" type=\"radio\" value=\"" + option + "\" required/>" + option + "<br>");
                    }
                }
                out.print("</form><br>"); 
            }
        %>
    </body>
</html>
