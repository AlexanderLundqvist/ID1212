package Main;

import java.io.*;
import java.io.File;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Base64;
import java.util.Date;
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
    private static final String SERVER = "smtp.kth.se";
    private static final int PORT = 587;
    private static String username;
    private static String password;
    private static String recipient;
    private static String subject;
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
            username = file.nextLine();
            password = file.nextLine();
            file.close();
            System.out.println("Login credentials has been processed.");

            Scanner in = new Scanner(System.in);
            System.out.println("Recipient: ");
            recipient = in.nextLine();
            System.out.println("Subject: ");
            subject = in.nextLine();
            System.out.println("Message: ");
            message = in.nextLine() + "\r\n."; // <CRLF>. to end message 
            in.close();
            System.out.println();
            
            /*
            Setting up the I/O for the initial non-ecrypted socket.
            */
            socket = new Socket(SERVER, PORT);
            incoming = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outgoing = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            System.out.println(getTimestamp() + " Starting SMTP mail service...");
            System.out.println(getTimestamp() + " S: " + incoming.readLine()); // Initialize the connection by reading the first line
            
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
            sendMessage(encoder.encodeToString(username.getBytes()), false);
            sendMessage(encoder.encodeToString(password.getBytes()), false);

            /*
            After successful authentication we can start to send the actual message.
            */
            sendMessage("MAIL FROM:<" + username + "@kth.se>", false);
            sendMessage("RCPT TO:<" + recipient + "@kth.se>", false);
            sendMessage("DATA", false);
            sendMessage(getHeaders()+message, false);
            sendMessage("QUIT", false);
            
            System.out.println(getTimestamp() + " SMTP mail service has stopped!");
            sslSocket.close();
            socket.close();
        } catch (Exception exception) {
            System.err.println("Error: " + exception.getMessage());
        }
    }
    
    
    /**
     * Sends a message to the mail server. 
     * @param message the message to be sent
     * @param read specifies if we want to read a specific response
     */
    private static void sendMessage(String message, boolean read) {
        try {
            outgoing.println(message);
            outgoing.flush();
            if (read == true) {
                readResponse(message);
            }
            else {
                System.out.println(getTimestamp() + " C: " + message);
                System.out.println(getTimestamp() + " S: " + incoming.readLine());
            }
        } catch (Exception exception) {
            System.err.println("Error sending message: " + exception.getMessage());
        }
    }
    
    /**
     * Reads the response from the server and prints the message from both client
     * and server.
     */
    private static void readResponse(String message) {
        try {
            String line;
            System.out.println(getTimestamp() + " C: " + message);
            while ((line = incoming.readLine()) != null) {
                if (message.startsWith("EHLO") || message.startsWith("HELO")) {
                    // https://datatracker.ietf.org/doc/html/rfc1830
                    // Uncomment to show the whole EHLO response. 
                    //System.out.println(getTimestamp() + " S: " + line); 
                    if (line.startsWith("250 ")) {
                        System.out.println(getTimestamp() + " S: " + line);
                        break;
                    }
                }
                else if (message.startsWith("STARTTLS")) {
                    if (line.startsWith("220 ")) {
                        System.out.println(getTimestamp() + " S: " + line);
                        break;
                    }
                }
                else if (message.startsWith("AUTH LOGIN")) {
                    if (line.startsWith("235 ") || line.startsWith("334 ")) {
                        System.out.println(getTimestamp() + " S: " + line);
                        break;
                    }
                }
            }
        } catch (IOException exception) {
            System.err.println("Error reading server response: " + exception.getMessage());
        }
    }
    
    /**
     * Gets the email headers.
     * @return the headers
     */
    private static String getHeaders() {
        String headers =
            "Date: " + LocalTime.now() + "\r\n" +
            "From: SMTP User <" + username + "@kth.se>\r\n" +
            "Subject: " + subject + "\r\n" +
            "To: " + recipient + "@kth.se\r\n\r\n";
            
        return headers;
    }
    
    /**
     * A simple function to create a timestamp.
     * @return the timestamp 
     */
    private static String getTimestamp() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String timeStamp = "[" + formatter.format(new Date()) + "]";
        return timeStamp;
    }
    
}
