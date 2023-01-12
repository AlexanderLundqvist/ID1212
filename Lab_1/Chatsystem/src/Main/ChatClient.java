package Main;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class represents the client/program/interface that allows the user to 
 * connect and interact with the chat server. Utilizes two threads for sending
 * messages and listening for messages from other clients connected to the chat.
 *
 * @author Alexander Lundqvist & Ramin Shojaei
 * Created: 15.11.2022
 */
public class ChatClient {
    private static final int portNumber = 8420; // Safe to use https://www.speedguide.net/port.php?port=8420
    private static final String hostname = "localhost";
    
    /**
     * Main client process.
     * @param args should be empty
     */
    public static void main(String[] args) throws InterruptedException {
        Socket socket = null;
        try {
            System.out.println("Connecting to the chat...");
            socket = new Socket(hostname, portNumber);
            System.out.println("You are now connected to the chat!");
            System.out.println("Write a message then hit enter to send. Type /quit to exit the application.");
            
            Thread clientReaderThread = new Thread(new ClientReader(socket));
            Thread clientWriterThread = new Thread(new ClientWriter(socket));
            clientReaderThread.start();
            clientWriterThread.start();
            
            // Only clean exit we could think of in case of server shutdown
            clientWriterThread.join();
            clientReaderThread.interrupt();
            System.exit(0);
            
        } catch (UnknownHostException exception) {
            System.err.println("Could not connect to chat server: " + exception.getMessage());
            exception.printStackTrace();
        } catch (IOException exception) {
            System.err.println("I/O error: " + exception.getMessage());
            exception.printStackTrace();
        }
    }  
}

/**
* A runnable listener responsible for reading the user's input and passing it 
* on to the chat server.
*/
class ClientReader implements Runnable {
    private Socket socket;
    private DataOutputStream outgoing; // Used for sending network messages.
    private BufferedReader userInput;
    
    /**
     * Constructor.
     * @param socket is the clients socket
     */
    public ClientReader(Socket socket) {
        this.socket = socket;
        try {
            this.outgoing = new DataOutputStream(new BufferedOutputStream(this.socket.getOutputStream()));
            this.userInput = new BufferedReader(new InputStreamReader(System.in));
        } catch (IOException exception) {
            System.err.println("Error getting output stream: " + exception.getMessage());
            exception.printStackTrace();
        }
    }
    
    @Override
    public void run(){
        try {
            String message;
            while (!this.socket.isClosed() && this.socket != null) {
                message = this.userInput.readLine();
                if (message.isEmpty()) {
                    continue;
                }
                this.outgoing.writeUTF(message);
                this.outgoing.flush();
                if (message.equals("/quit")) {
                    break;
                }
            }
            System.out.println("You have been disconnected from the chat!");
        } catch (IOException exception) {
            //System.err.println("Client input error: " + exception.getMessage());
            //exception.printStackTrace();
        } finally {
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

/**
* A runnable listener responsible for incoming messages from other clients
* currently connected to the chat.
*/
class ClientWriter implements Runnable {
    private Socket socket;
    private DataInputStream incoming; // Used for receiving network messages.
    
    /**
     * Constructor.
     * @param socket is the clients socket 
     */
    public ClientWriter(Socket socket){
        this.socket = socket;
        try {
            this.incoming = new DataInputStream(new BufferedInputStream(this.socket.getInputStream()));
        } catch (IOException exception) {
            System.err.println("Error getting input stream: " + exception.getMessage());
        }
    }
    
    /**
     * A simple function to create a timestamp for chat messages. When assigned
     * here instead of by the handler or server, we get time zone independency.
     * @return the timestamp 
     */
    private static String getTimestamp() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        String timeStamp = "[" + formatter.format(new Date()) + "]";
        return timeStamp;
    }
    
    @Override
    public void run(){
        try {
            String message;
            while (!this.socket.isClosed() && this.socket != null) {
                message = this.incoming.readUTF();
                System.out.println(getTimestamp() + " " + message);
            }
        } catch (EOFException exception) {
            // Unfortunate side effect from DataInputStream is that we have to catch this
        } catch (IOException exception) {
            //System.err.println("Client output error: " + exception.getMessage());
            //exception.printStackTrace();
            System.out.println("Server has closed the connection!");
        } finally {
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

