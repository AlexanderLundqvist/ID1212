package controller;

import integration.DBhandler;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.QuestionBean;
import model.QuizBean;
import model.UserBean;

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
        UserBean user = (UserBean) session.getAttribute("UserBean");
        
        // Navigate to quiz
        String subject = request.getParameter("subject");     
        if (subject != null) {
            QuizBean quiz = generateQuiz(subject);
            session.setAttribute("Quiz", quiz);
            response.sendRedirect("quiz.jsp");
            return;
        }
    }
    
    /**
     * Generates a quiz object according to the subject specified.
     * @param subject the chosen subject
     * @return the generated quiz
     */
    private QuizBean generateQuiz(String subject) {
        QuizBean quiz = new QuizBean();
        ArrayList<String[]> requestedQuestions = database.getQuestions(subject);
        ArrayList<QuestionBean> questions = new ArrayList<>();
        for (String[] array : requestedQuestions) {
            QuestionBean question = generateQuestion(array);
            questions.add(question);
        }
        quiz.setQuestions(questions);
        return quiz;
    }
    
    /**
     * Generates a questionBean.
     * @param requestedQuestion the array
     * @return the question
     */
    private QuestionBean generateQuestion(ArrayList<String> requestedQuestion) {
        QuestionBean question = new QuestionBean();
        String text = requestedQuestion[0];
        String[] options = requestedQuestion[1].split("/");
        String[] solution = requestedQuestion[2].split("/");
        question.setId(1);
        question.setQuestion(text);
        question.setOptions(options);
        question.setSolution(solution);
        return question;
    }
    
    
    private void handleGuess() {
        
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
            answers.add(request.getParameter("question" + i++));
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
