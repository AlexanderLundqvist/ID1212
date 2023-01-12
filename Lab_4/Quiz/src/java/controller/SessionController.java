package controller;

import integration.DBhandler;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.UserBean;

/**
 * Controller servlet that handles login and logout requests.
 * 
 * @author Alexander Lundqvist & Ramin Shojaeis
 */
@WebServlet(name = "SessionController", urlPatterns = {"/SessionController"})
public class SessionController extends HttpServlet {
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
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String logout = request.getParameter("logout");
        
        // Logout
        if (user != null && logout != null) {
            session.setAttribute("SessionStatus", "LoggedOut");
            response.sendRedirect("index.jsp");
            return;
        }
        // Login
        if (username != null && password != null && validateUser(username, password)) {
            user = database.getUser(username, password);
            
            session.setAttribute("UserBean", user);                
            session.setAttribute("SessionStatus", "LoggedIn");
            response.sendRedirect("home.jsp");
            return;
        }
        else {

            user = new UserBean();
            session.setAttribute("SessionStatus", "Error");
            response.sendRedirect("index.jsp");
            return;
        }
    }
    
    /**
     * Simple user credentials validation against the database.
     * @param username the provided username
     * @param password the provided password
     * @return boolean
     */
    private boolean validateUser(String username, String password) {
        try {
            UserBean user = database.getUser(username, password);
            if (user != null) {
                return true;
            }
            else {
                return false;
            }
        } catch (Exception exception) {
            System.err.println("Debug: " + exception.getMessage());
            exception.printStackTrace();
            return false;
        }
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
