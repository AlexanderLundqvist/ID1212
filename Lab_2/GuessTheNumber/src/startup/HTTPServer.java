package startup;

import java.io.*;
import java.net.*;
import controller.RequestHandler;
import model.Model;

/**
 * This class represents a simple HTTP server that runs on localhost.
 * It contains the main method that runs the whole application. The server is 
 * accessible with the URL http://localhost:8080/
 *
 * @author Alexander Lundqvist & Ramin Shojaei
 */
public class HTTPServer {   
    private static final int port = 8080; // Change this if needed
    
    /**
    * Main method used to start the server and application components.
    * 
    * @param args The application does not take any command line arguments.
    */
    public static void main(String[] args) {
        try {
            System.out.println("[HTTPServer]: Initializing server...");
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("[HTTPServer]: Server has been started and is running on port " + port);
            Model model = new Model();
            
            /*
            The thread that listens for new connections and creates new game sessions
            for each individual client. RequestHandler acts like a controller.
            */
            while (true) {
                System.out.println("[HTTPServer]: Listening for connection request...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("[HTTPServer]: Accepted connection from " + clientSocket.getInetAddress());
                Thread client = new Thread(new RequestHandler(model, clientSocket));
                client.start();
            }
            
        } catch (IOException exception) {
            System.err.println("[HTTPServer]: Server error - " + exception.getMessage());
            exception.printStackTrace();
            System.exit(1);
        }
    }
}
