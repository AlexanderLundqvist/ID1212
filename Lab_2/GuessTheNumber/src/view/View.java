package view;

import java.io.*;
import java.util.Date;
import java.util.Map;

/**
 * This class generates the servers HTTP and HTML response and sends it to the
 * client browser.
 *
 * @author Alexander Lundqvist & Ramin Shojaei
 */
public class View {
    private static final Map<Integer, String> HTTPResponseMessages = Map.of(
            200, "200 OK",
            400, "400 Bad Request",
            404, "404 Not Found",
            405, "405 Method Not Allowed",
            500, "500 Internal Server Error",
            505, "505 HTTP Version Not Supported");
    
    private PrintWriter outgoing;
    
    /**
     * 
     * @param outgoing 
     */
    public View (PrintWriter outgoing) {
        this.outgoing = outgoing;
    }
    
    /**
     * Overloaded method when we don't care about the cookie.
     * @param statusCode the status code
     * @param content the HTML response
     */
    public void sendServerResponse(int statusCode, String content) {
        this.sendServerResponse(statusCode, "", content);
    }
    
    /**
     * Constructs the full HTTP response and sends it to the client via the socket.
     * @param statusCode the status code
     * @param cookie the cookie
     * @param content the HTML response
     */
    public void sendServerResponse(int statusCode, String cookie, String content) {
        String responseLine = "HTTP/1.1 " + HTTPResponseMessages.get(statusCode) + "\r\n";
        String headers = getResponseHeaders(cookie, content.length());
        String body = content + "\r\n\r\n";
        String fullResponse = responseLine + headers + body;
        this.outgoing.write(fullResponse);
        this.outgoing.flush();
        
        //System.out.println(fullResponse); // Display in server terminal for debug
    }
    
    /**
     * Constructs the headers for the response.
     * @param cookie the cookie
     * @param length the length of the response body
     * @return the response header string
     */
    private String getResponseHeaders(String cookie, int length) {
        StringBuilder headers = new StringBuilder();
        Date now = new Date();
        headers.append("Date: ").append(now).append("\r\n");
        headers.append("Server: ID1212 Lab2\r\n");
        headers.append("Content-length: ").append(length).append("\r\n");
        headers.append("Content-Type: text/html; charset=utf-8\r\n");
        if (!cookie.isEmpty()) {
            headers.append("Set-Cookie: sessionID=");
            headers.append(cookie);
            headers.append("; SameSite=None\r\n");
        }
        headers.append("\r\n");
        return headers.toString();
    }
}
