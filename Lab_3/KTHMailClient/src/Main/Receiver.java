package Main;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * This class takes a @kth.se mail address and password combination and uses the
 * Internet Message Access Protocol (IMAP) connect to the remote email account
 * and retrieves a mail.
 *
 * Reference:
 * <a href="https://en.wikipedia.org/wiki/Internet_Message_Access_Protocol">Wikipedia</a>
 *
 * Settings for sending mail. Server: webmail.kth.se Port: 993 Protocol: IMAP
 * (SSL/TLS) Authentication: Normal password
 *
 * @author Alexander Lundqvist & Ramin Shojaei
 */
public class Receiver {
    private static final String SERVER = "webmail.kth.se";
    private static final int PORT = 993;
    private static final String inbox = "a02 SELECT INBOX"; // inkorg a002
    private static final String firstMail = "a03 FETCH 1 BODY[TEXT]"; // Hämtar äldsta e-postmeddelandet i inkorgen genom detta kommando.
    private static final String logout = "999 LOGOUT";
    private static String username;
    private static String password;
    private static SSLSocket sslSocket;
    private static SSLSocketFactory socketFactory;
    private static BufferedReader incoming;
    private static PrintWriter outgoing;
    private static String login;
    
    
    /**
     * Main method.
     *
     * @param args takes no input arguments
     */
    public static void main(String[] args) throws IOException {
        try {
            /* 
            We get the login credentials by reading it from a secret file.
             */
            Scanner file = new Scanner(new File("src/resources/Credentials.txt"));
            username = file.nextLine().trim();
            password = file.nextLine().trim();
            file.close();

            // Måste innehålla ett prefix "a" tillsammans med något nummer. Vi har a01 för inloggning.
            login = "a01 LOGIN " + username + " " + password; // loggar in med användarnamn o lösenord

            // Skapar ett Socket factory för SSL Sockets.
            // Vi skapade först en standard socket och slår nu in den i en SSLSocket.
            socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            sslSocket = (SSLSocket) socketFactory.createSocket(SERVER, PORT);
            incoming = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
            outgoing = new PrintWriter(new OutputStreamWriter(sslSocket.getOutputStream()));

            // Referens till detta: https://www.rfc-editor.org/rfc/rfc3501#section-6.4.5
            // Läser in hälsning från servern, från sslSocket
            System.out.println(getTimestamp() + " S: " + incoming.readLine());  //Response från Server
            
            /*
            Finally specify the mail we want to receive.
            */
            sendMessage(login);
            sendMessage(inbox);
            sendMessage(firstMail);
            sendMessage(logout);
            
        } catch (Exception exception) {
            System.err.println("Error: " + exception.getMessage());
            exception.printStackTrace();
        } finally {
            try {
                sslSocket.close();
            } catch (IOException exception) {
                System.err.println("Could'd not close the socket: " + exception.getMessage());
            }
        }
    }
    
    /**
     * Sends a message to the mail server. 
     * @param message the message to be sent
     */
    public static void sendMessage(String message) {
        try {
            outgoing.println(message);
            outgoing.flush();
            if (message.startsWith("a01 LOGIN ")) {
                System.out.println(getTimestamp() + " C: a01 LOGIN " + username + " ************");
            }
            else {
                System.out.println(getTimestamp() + " C: " + message);
            }
            readResponse();
        } catch (Exception exception) {
            System.err.println("Error sending message: " + exception.getMessage());
        }
    }
    
    /**
     * Reads the response from the server and prints it.
     */
    public static void readResponse() {
        try {
            String line;
            while ((line = incoming.readLine()) != null) {
                System.out.println(getTimestamp() + " S: " + line);
                if (line.contains("a0")) {
                    break;
                }
            }
        } catch (IOException exception) {
            System.err.println("Error reading server response: " + exception.getMessage());
        }
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
