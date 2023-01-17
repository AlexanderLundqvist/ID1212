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
    	database = new DBhandler();
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
        ArrayList<QuizBean> quizzes = database.getQuizzes();
        
        // Logout
        String logout = request.getParameter("Logout"); 
        if (logout != null) {
            session.setAttribute("UserBean", null);
            session.setAttribute("SessionStatus", "Logout");
            response.sendRedirect("index.jsp");
            return;
        }
        
        // Login
        String username = request.getParameter("Username");
        String password = request.getParameter("Password");
        if (username != null && password != null) {
            UserBean user = database.getUser(username, password);
            if (user == null) {
                session.setAttribute("SessionStatus", "Wrong");
                response.sendRedirect("index.jsp");
                return;
            }
            else {
                quizzes = database.getQuizzes();
                session.setAttribute("Quizzes", quizzes); 
                session.setAttribute("UserBean", user);                
                session.setAttribute("SessionStatus", "Login");
                response.sendRedirect("home.jsp");
                return;
            }
        }
        else if (username == null || password == null) {
            session.setAttribute("SessionStatus", "Error");
            response.sendRedirect("index.jsp");
            return;
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
            // Ugly solution...
            for (QuizBean quiz : quizzes) {
                if (quiz.getSubject().equals(subject)) {
                    session.setAttribute("ActiveQuiz", quiz);
                    response.sendRedirect("quiz.jsp");
                    return;
                }
            }   
        }
        
        // Handle quiz submit
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
                }
            }
            
            // Update session and database
            ArrayList<Integer> results = user.getResults();
            results.set(quiz.getId(), score);
            user.setResults(results);
            session.setAttribute("UserBean", user);
            database.updateResult(user.getId(), quiz.getId(), score);
            response.sendRedirect("home.jsp");
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
        while (request.getParameter("question" + i) != null) {
            answers.add(request.getParameter("question" + i));
            i++;
        }
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
