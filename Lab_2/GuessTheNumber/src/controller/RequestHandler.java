package controller;

import java.io.*;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.Base64;
import model.GameSession;
import model.Model;
import view.View;
import static view.HTML.*;


/**
 * This class represents a controller for the application. Each new client gets 
 * assigned a RequestHandler object. It manages incoming HTTP requests from the 
 * client browser, checks if the request is valid and if there are cookies present.
 * It then fetches the appropriate data from the model and relays it to the view.
 *
 * @author Alexander Lundqvist & Ramin Shojaei
 */
public class RequestHandler implements Runnable{
    private Model model;
    private View view;
    private Socket socket;
    private BufferedReader incoming;
    private PrintWriter outgoing;
            
    /**
     * Initializes an instance of the RequestHandler "Controller" 
     * @param model the application model
     * @param socket is the client socket
     */
    public RequestHandler(Model model, Socket socket) {
        this.socket = socket;
        this.model = model;
        try {
            this.incoming = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.outgoing = new PrintWriter(this.socket.getOutputStream());
        } catch (IOException exception) {
            System.err.println("[RequestHandler]: There was an error creating the handler - " + exception.getMessage());
            closeConnection(this.socket);
        }
        this.view = new View(this.outgoing);
    }
    
    @Override
    public void run() {
        try {
            Request request = new Request(this.incoming);
            int statusCode = request.validateRequestLine();
            String method = request.getMethod();
            
            // Guard for anything else than HTTP OK
            if (statusCode != 200) {
                handleError(statusCode);
                request.print();
                closeConnection(this.socket);
                return;
            }

            // Handle the allowed HTTP methods
            switch (method) {
                case "GET":
                    handleGETrequest(request);
                    break;
                case "POST":
                    handleError(405);
                    System.out.println("[RequestHandler]: POST not implemented!");
                    break;
                default:
                    handleError(500);
                    System.out.println("[RequestHandler]: I should not print!");
                    break;
            }
            request.print(); // Debug   
        } catch (Exception exception) {
            handleError(500);
            closeConnection(this.socket);
            exception.printStackTrace();
        } finally {
            System.out.println("[RequestHandler]: Request has been processed!");
            closeConnection(this.socket);
        }
    }
    
    /**
     * Handles HTTP GET requests.
     * @param request the HTTP request
     */
    private void handleGETrequest(Request request) {
        String cookie = request.getCookie();
        GameSession currentSession = this.model.getSession(cookie);
        
        // No cookie, new client!
        if (cookie.isEmpty()) {
            System.out.println("[RequestHandler]: No cookie, creating new cookie and session!");
            String newCookie = generateCookie();
            this.model.addSession(newCookie);
            this.view.sendServerResponse(200, newCookie, String.format(HTML_TEMPLATE, BODY_INITIAL));
            return;
        }
        
        // Cookie exists, but there is no session
        if (currentSession == null) {
                System.out.println("[RequestHandler]: Cookie exists, but no session!");
                this.model.addSession(cookie);
                this.view.sendServerResponse(200, String.format(HTML_TEMPLATE, BODY_INITIAL));
                return;
        }
        
        // Cookie and session exists, try to get the client guess
        System.out.println("[RequestHandler]: Cookie exists, using existing session!");
        String guess = extractGuess(request.getURI());
        if (guess.equals("MALFORMED") || guess.equals("NOT IN RANGE") || guess.equals("NOT A NUMBER")) {
            sendToView(currentSession.getAmountOfGuesses(), BODY_WRONG_INPUT);
            return;
        }
        
        // Finally handle the game logic
        String result = currentSession.Guess(Integer.parseInt(guess));
        switch (result) {
            case "HIGHER":
                sendToView(currentSession.getAmountOfGuesses(), BODY_TOO_LOW);
                break;
            case "LOWER":
                sendToView(currentSession.getAmountOfGuesses(), BODY_TOO_HIGH);
                break;
            case "CORRECT":
                sendToView(currentSession.getAmountOfGuesses(), BODY_CORRECT);
                this.model.removeSession(cookie);
                //currentSession.restart();
                break;
        }
    }
    
    /**
     * Parse the requested resource to find the guess that the client has made
     * or send an error code.
     * @param URI the requested resource
     * @return the result
     */
    private String extractGuess(String URI) {
        String guess;
        String[] value = URI.split("=");
        try {
            if (value.length != 2 && URI.startsWith("/?guess")) {
                guess = "MALFORMED";
            }
            else if (Integer.parseInt(value[1]) < 1 || Integer.parseInt(value[1]) > 100) {
                guess = "NOT IN RANGE";
            }
            else {
                guess = value[1]; 
            }
        } catch (NumberFormatException exception){
            guess = "NOT A NUMBER";
        }
        return guess;
    }
    
    /**
     * Used for HTTP requests with status 200 OK.
     * Sends the game data and HTML template to the view.
     * @param template the HTML template
     */
    private void sendToView(int amountOfGuesses, String template) {
        String body = String.format(template, amountOfGuesses);
        this.view.sendServerResponse(200, "", String.format(HTML_TEMPLATE, body));
    }
    
    /**
     * Handles any of the predefined HTTP error status codes.
     * @param statusCode the HTTP status code
     */
    private void handleError(int statusCode) {
        String body = String.format(HTML_ERROR_TEMPLATE, statusCode, Integer.toString(statusCode));
        this.view.sendServerResponse(statusCode, body);
    }
    
    /**
     * Closes the socket.
     * @param socket is the socket
     */
    private void closeConnection(Socket socket) {
        try {
           socket.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
    
    /**
     * This method creates a URL-safe base64-encoded 160-bit random value token
     * that can be used as a cookie.
     * Refer to <a href="https://neilmadden.blog/2018/08/30/moving-away-from-uuids/">Neil Madden</a>
     * @return the cookie string
     */
    public static String generateCookie() {
        SecureRandom random = new SecureRandom();
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        
        byte[] buffer = new byte[20];
        random.nextBytes(buffer);
        return encoder.encodeToString(buffer);
    }
}
