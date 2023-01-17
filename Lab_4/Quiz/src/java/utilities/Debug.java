package utilities;

import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.*;
import controller.*;
import integration.*;


/**
 * Utility class that contains various debugging functions.
 * 
 * @author Alexander Lundqvist
 */
public class Debug {
    
    /**
     * This class should not be instantiated.
     */
    private Debug() {
    }

    /**
     * Prints out all attributes and their values for the given session.
     * @param session
     */
    public static void printSessionAttributes(HttpSession session) {
        Enumeration<String> attributes = session.getAttributeNames();
        while (attributes.hasMoreElements()) {
            String attribute = attributes.nextElement();
            System.out.println(attribute + ": " + session.getAttribute(attribute));
        }
    }
}
