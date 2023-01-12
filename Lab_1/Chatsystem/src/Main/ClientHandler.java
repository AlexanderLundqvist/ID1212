package Main;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.ListIterator;

/**
 * This class represents the clients that connect to the server. It serves two
 * purposes. One, listening for messages from other clients and two, broadcasts
 * the message from the client to all other clients currently connected to the
 * server.
 *
 * @author Alexander Lundqvist & Ramin Shojaei
 * Created: 15.11.2022
 */
public class ClientHandler extends Thread{
    private final String userId;
    private final Socket socket;
    private DataInputStream incoming; // Used for receiving network messages.
    private DataOutputStream outgoing; // Used for sending network messages.
    private static ArrayList<ClientHandler> clientList; // This holds all other active clients
          
    /**
     * Creates an instance of the client handler. 
     * @param socket is the socket of the client
     * @param clientList the list with all active clients
     * @param userId a unique number to create the complete user ID with
     * @throws IOException 
     */
    public ClientHandler(Socket socket, ArrayList<ClientHandler> clientList, int userId) throws IOException {
        this.userId = "User" + userId;
        this.socket = socket;
        this.clientList = clientList;
        this.incoming = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        this.outgoing = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        
    }    
    
    /**
     * Sends the message to the other clients. Uses synchronization so that the
     * chat log behaves the same for every client?
     * @param message is the message to be sent
     */
    public static synchronized void broadcast(String message) {
        synchronized (clientList) {
            ListIterator<ClientHandler> iterator = clientList.listIterator();
            ClientHandler current;
            while (iterator.hasNext()){
                try {
                    current = iterator.next();
                    current.outgoing.writeUTF(message);
                    current.outgoing.flush();
                }
                catch (IOException exception) {
                    System.err.println("There was an error when sending the message: " + exception.getMessage());
                }
            }
        }
    }
    
    /**
     * This thread Listens for incoming messages from the user and then broadcast
     * it to all other users. Handles client disconnect.
     */
    @Override
    public void run() {
        try {
            // Welcome the user and display the username
            String welcome = "Welcome, you are now known as " + this.userId + "!";
            this.outgoing.writeUTF(welcome);
            this.outgoing.flush();
            
            String message;
            while (!this.socket.isClosed()) {
                message = this.incoming.readUTF();
                if (message.equals("/quit")) {
                    break; // Jumps to finally clause
                }
                else {
                    broadcast(this.userId + ": " + message);
                }
            }
        } catch (IOException exception) {
            System.err.println("Client handler error: " + exception.getMessage());
            // exception.printStackTrace();
        } finally {
            clientList.remove(this);
            broadcast(this.userId + " has left the chat...");
            if (!this.socket.isClosed() && this.socket != null) {
                try {
                    this.socket.close();
                } catch (IOException exception) {
                    System.err.println("Could not close client socket: " + exception.getMessage());
                }
            } 
        }
    }
}
