package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.UserBean;
import integration.*;
import static utilities.Debug.printSessionAttributes;

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
        session.setAttribute("SessionStatus", null);
        String username = request.getParameter("Username");
        String password = request.getParameter("Password");
        String logout = request.getParameter("Logout");  
        
        // Logout
        if (logout != null) {
            session.setAttribute("UserBean", null);
            session.setAttribute("SessionStatus", "Logout");
            response.sendRedirect("index.jsp");
            return;
        }
        
        // Login
        if (username != null && password != null) {
            UserBean user = database.getUser(username, password);
            if (user == null) {
                session.setAttribute("SessionStatus", "Wrong");
                response.sendRedirect("index.jsp");
                return;
            }
            else {
                session.setAttribute("UserBean", user);                
                session.setAttribute("SessionStatus", "Login");
                response.sendRedirect("home.jsp");
                return;
            }
        }
        else {
            session.setAttribute("SessionStatus", "Error");
            response.sendRedirect("index.jsp");
            return;
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
