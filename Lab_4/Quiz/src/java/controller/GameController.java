package controller;

import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.*;
import integration.*;
import static utilities.Debug.printSessionAttributes;

/**
 * This controller servlet handles the all game related requests.
 * 
 * @author Alexander Lundqvist & Ramin Shojaei
 */
@WebServlet(name = "GameController", urlPatterns = {"/GameController"})
public class GameController extends HttpServlet {
    private DBhandler database;
    
    @Override
    public void init() {
        try {
            database = new DBhandler();
        } catch (Exception exception) {
            System.err.println("[GameController]: Could not get database: " + exception.getMessage());
            exception.printStackTrace();
        }
    }
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession(true);
        ArrayList<QuizBean> quizzes = (ArrayList<QuizBean>) session.getAttribute("Quizzes");
        if (quizzes == null || quizzes.isEmpty()) {
            quizzes = database.getQuizzes();
            for (QuizBean quiz : quizzes) {
                System.out.println("[GameController]: Quiz id = " + quiz.getId());
                quiz.setQuestions(database.getQuestions(quiz.getId()));
            }
            session.setAttribute("Quizzes", quizzes); 
        }
        
        // Logout
        String logout = request.getParameter("Logout"); 
        if (logout != null) {
            UserBean user = (UserBean) session.getAttribute("UserBean");
            System.out.println("[GameController]: User " + user.getUsername() + " has logged out!");
            session.setAttribute("UserBean", null);
            session.setAttribute("SessionStatus", "Logout");
            response.sendRedirect("index.jsp");
            return;
        }
        
        // Login
        String username = request.getParameter("Username");
        String password = request.getParameter("Password");
        if (username != null && password != null) {
            System.out.println("[GameController]: User " + username + " with password " + password);
            UserBean user = database.getUser(username, password);
            if (user == null) {
                session.setAttribute("SessionStatus", "Wrong");
                response.sendRedirect("index.jsp");
                return;
            }
            else {
                System.out.println("[GameController]: User " + user.getUsername() + " has logged in!");
                session.setAttribute("UserBean", user);                
                session.setAttribute("SessionStatus", "Login");
                response.sendRedirect("home.jsp");
                return;
            }
        }
        
        // Navigate to home from quiz
        String path = request.getParameter("Nav");
        if (path != null && path.equals("Home")) {
            session.setAttribute("ActiveQuiz", null);
            response.sendRedirect("home.jsp");
            return;
        }
        
        // Navigate to quiz
        String subject = request.getParameter("Subject");     
        if (subject != null) {
            System.out.println("[GameController]: Choosen subject = " + subject);
            // Ugly solution...
            for (QuizBean quiz : quizzes) {
                if (quiz.getSubject().equals(subject)) {
                    System.out.println("[GameController]: Active quiz = " + subject);
                    session.setAttribute("ActiveQuiz", quiz);
                    response.sendRedirect("quiz.jsp");
                    return;
                }
            }   
        }
        
        // Handle quiz submit
        String submit = request.getParameter("SubmitQuiz"); 
        if (submit != null) {
            ArrayList<String> guesses = extractAnswers(request);
            if (!guesses.isEmpty()) {
                QuizBean quiz = (QuizBean) session.getAttribute("ActiveQuiz");
                UserBean user = (UserBean) session.getAttribute("UserBean");
                ArrayList<QuestionBean> questions = quiz.getQuestions();
                Integer score = 0;

                for (int i = 0; i < guesses.size(); i++) {
                    ArrayList<String> options = questions.get(i).getOptions();
                    ArrayList<String> answer = questions.get(i).getAnswer();
                    Integer index = options.indexOf(guesses.get(i));
                    if (answer.get(index).equals("1")) {
                        score++;
                        System.out.println("[GameController]: Correct!");
                    }
                    else {
                        System.out.println("[GameController]: Wrong!");
                    }
                }

                // Update session and database
                ArrayList<Integer> newResults = user.getResults();
                newResults.set(quiz.getId()-1, score);
//                ArrayList<Integer> newResults = new ArrayList<>();
//                newResults.add(user.getId());
//                newResults.add(quiz.getId());
//                newResults.add(score);
                user.setResults(newResults);
                session.setAttribute("UserBean", user);
                database.updateResult(user.getId(), quiz.getId(), score);
                System.out.println("[GameController]: Updated user " + user.getId() + " for quiz " + quiz.getId() + " with " + score);
                response.sendRedirect("home.jsp");
            }
        }
    } 
    
    /**
     * Extracts the clients quiz answers from the request.
     * 
     * @param request servlet request
     * @return the quiz answers
     */
    private ArrayList<String> extractAnswers(HttpServletRequest request) {
        ArrayList<String> answers = new ArrayList<>();
        int i = 1;
        while (request.getParameter("Question" + i) != null) {
            System.out.println("[GameController]: Question " + i + " = " + request.getParameter("Question" + i));
            answers.add(request.getParameter("Question" + i));
            i++;
        }
        System.out.println("[GameController]: Answers parsed!");
        return answers;
    } 
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
