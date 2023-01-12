package Main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * This class represents a simple multi-threaded chat server. 
 *
 * @author Alexander Lundqvist & Ramin Shojaei
 * Created: 15.11.2022
 */
public class ChatServer {    
    private static final int portNumber = 8420; // Safe to use https://www.speedguide.net/port.php?port=8420
    private static ArrayList<ClientHandler> clientList = new ArrayList<ClientHandler>(); // Holds all active client threads
    private static int userId = 1; // Simple user identifier
     
    /**
     * Main server process.
     * @param args should empty
     */
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            System.out.println("Initializing server...");
            serverSocket = new ServerSocket(portNumber);
            System.out.println("Server has been started and is running on port " + portNumber);
            
            /*
            The thread that listens for new connections and assigning a client
            handler thread to each succesful connection.
            */
            while (true) {
                System.out.println("Listening for connection request...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from: " + clientSocket.getInetAddress());
                System.out.println("Creating handler for this client");
                ClientHandler client = new ClientHandler(clientSocket, clientList, userId);
                userId++;
                clientList.add(client);
                client.start();
            }
            
        } catch (IOException exception) {
            System.err.println("Server error: " + exception.getMessage());
            exception.printStackTrace();
            serverSocket.close();
            System.exit(1);
        }  

    }
}
