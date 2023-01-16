package Main;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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

    /**
     * Main method.
     *
     * @param args takes no input arguments
     */
    public static void main(String[] args) throws IOException {
        final String SERVER = "webmail.kth.se";
        final int PORT = 993;
        String username = null;
        String password = null;
        SSLSocket sslSocket = null;
        SSLSocketFactory socketFactory = null;
        BufferedReader incoming = null;
        PrintWriter outgoing = null;
        String login = null;
        final String inbox = "a02 SELECT INBOX"; // inkorg a002
        final String firstMail = "a03 FETCH 1 BODY[TEXT]"; // Hämtar äldsta e-postmeddelandet i inkorgen genom detta kommando.
        final String logout = "999 LOGOUT";

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
            System.out.println("S: " + incoming.readLine());  //Response från Server

            messageWriter(outgoing, login);
            messageReader(incoming);

            messageWriter(outgoing, inbox);
            messageReader(incoming);

            messageWriter(outgoing, firstMail);
            messageReader(incoming);

            messageWriter(outgoing, logout);
            messageReader(incoming);        

        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace();
        } finally {
            try {
                sslSocket.close();
            } catch (IOException exception) {
                System.err.println("Could'd not close the socket: " + exception.getMessage());
                //exception.printStackTrace();
            }
        }
    }

    public static void messageWriter(PrintWriter outgoing, String message) throws IOException {
        outgoing.println(message);
        outgoing.flush();
        System.out.println("C: " + message);
    }

    public static void messageReader(BufferedReader incoming) throws IOException {
        String message;
        while ((message = incoming.readLine()) != null) {
            //System.out.println("S: " + message);
            System.out.println("S: " + new String (message.getBytes("UTF-8"), System.getProperty("native.encoding"))); // Works for some reason...
            if (message.contains("a0")) {
                break;
            }
        }
    }

}
