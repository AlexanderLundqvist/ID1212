package Main;

import java.io.*;
import java.io.File;
import java.net.Socket;
import java.util.Base64;
import java.util.Map;
import java.util.Scanner;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * This class takes a @kth.se mail address, password combination, message and a 
 * reciever email address. It then uses the Simple Mail Transfer Protocol (SMTP) 
 * to connect to the remote email account and send the mail via the mail server 
 * the specified recipient email address.
 * 
 * Reference: <a href="https://en.wikipedia.org/wiki/Simple_Mail_Transfer_Protocol">Wikipedia</a>
 * 
 * Settings for receiving mail.
 * Server: smtp.kth.se
 * Port: 587
 * Protocol: SMTP (STARTTLS)
 * Authentication: Normal password
 *
 * @author Alexander Lundqvist & Ramin Shojaei
 */
public class Sender {
    private static final Map<Integer, String> SMTPresponses = Map.of(
            200, "200 OK",
            400, "400 Bad Request",
            404, "404 Not Found",
            405, "405 Method Not Allowed",
            500, "500 Internal Server Error",
            505, "505 HTTP Version Not Supported");
    
    private static final String SERVER = "smtp.kth.se";
    private static final int PORT = 587;
    private static String username;
    private static String password;
    private static String recipient;
    private static String message;
    private static Socket socket;
    private static BufferedReader incoming;
    private static PrintWriter outgoing;
            
    /**
     * Main method.
     * @param args takes no incoming arguments
     */ 
    public static void main(String[] args) {
        try {
            /* 
            We get the login credentials by reading it from a secret file. Then
            the user can specify the recipient and the message.
            */
            Scanner file = new Scanner(new File("src/resources/Credentials.txt"));
            username = file.nextLine().trim();
            password = file.nextLine().trim();
            file.close();
            System.out.println("Login credentials has been processed.");

            Scanner in = new Scanner(System.in);
            System.out.println("Recipient: ");
            recipient = in.nextLine().trim();
            System.out.println("Message: ");
            message = in.nextLine() + "\r\n.\r\n"; // <CRLF>.<CRLF> to end message 
            in.close();
            System.out.println();
            
            /*
            Setting up the I/O for the initial non-ecrypted socket.
            */
            socket = new Socket(SERVER, PORT);
            incoming = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outgoing = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            System.out.println(incoming.readLine()); // Initialize the connection by reading the first line
            
            /*
            Greet the server through the connection, specifying that
            we want to upgrade to a secure connection.
            */
            sendMessage("EHLO " + SERVER, true);
            sendMessage("STARTTLS", true);

            /*
            Create a new SSL socket and recreate the I/O streams with it.
            */
            SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket sslSocket = (SSLSocket) socketFactory.createSocket(socket, SERVER, PORT, true); 
            incoming = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
            outgoing = new PrintWriter(sslSocket.getOutputStream(), true);

            /*
            Greet the server again after upgrading to SSL/TLS.
            */
            sendMessage("EHLO " + SERVER, true);
            sendMessage("AUTH LOGIN", true);
            
            /*
            Encoding the username and password to base 64 encoding so that it
            works over the SSL socket. Then we send it to the server for authentication.
            */
            Base64.Encoder encoder = Base64.getEncoder();
            String usernameEncode = encoder.encodeToString(username.getBytes());
            String passwordEncode = encoder.encodeToString(password.getBytes());
            sendMessage(usernameEncode, false);
            sendMessage(passwordEncode, false);

            /*
            After successful authentication we can start to send the actual message.
            */
            sendMessage("MAIL FROM:<" + username + "@kth.se>", true);
            sendMessage("RCPT TO:<" + recipient + "@kth.se>", true);
            sendMessage("DATA", true);
            sendMessage(message, true);
            sendMessage("QUIT", true);

            sslSocket.close();
            socket.close();
        } catch (Exception exception) {
            System.err.println("Error: " + exception.getMessage());
        }
    }
    
    
    /**
     * Sends a message to the mail server. 
     * @param message the message to be sent
     * @param read specifies if we want to read the response
     */
    private static void sendMessage(String message, boolean read) {
        try {
            outgoing.println(message);
            outgoing.flush();
            if (read == true) {
                readResponse(message);
            }
            else {
                System.out.println(incoming.readLine());
            }
        } catch (Exception exception) {
            System.err.println("Error sending message: " + exception.getMessage());
        }
    }
    
    /**
     * Reads the response from the server.
     */
    private static void readResponse(String message) {
        try {
            String line;
            System.out.println();
            System.out.println(message);
            while ((line = incoming.readLine()) != null) { 
                //System.out.println(line); 
                if (line.startsWith("220 ") || line.startsWith("235 ") || line.startsWith("250 ") || line.startsWith("334 ") || line.startsWith("354 ")) {
                    System.out.println(line);
                    break;
                }
            }
        } catch (IOException exception) {
            System.err.println("Error reading server response: " + exception.getMessage());
        }
    }
    
}
