package Main;

import java.io.*;
import java.net.Socket;
import java.util.Base64;
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

    /**
     * Main method.
     * @param args takes no incoming arguments
     */ 
    public static void main(String[] args) throws IOException, InterruptedException {
        final String SERVER = "smtp.kth.se";
        final int PORT = 587;
        String username;
        String password;
        String recipient;
        String subject;
        String message;
       
        /* 
        We get the login credentials by reading it from a secret file.
        */
        Scanner file = new Scanner(new File("src/resources/Credentials.txt"));
        username = file.nextLine().trim();
        password = file.nextLine().trim();
        file.close();
        System.out.println("Login credentials has been processed.");
                
        Scanner in = new Scanner(System.in);
        System.out.println("Recipient: ");
        recipient = in.nextLine();
        System.out.println("Message subject: ");
        subject = in.nextLine();
        System.out.println("Type your message: ");
        message = in.nextLine(); // Should be something else since we only read one line now.
        in.close();

        // Skapa en ett objekt i form av en okrypterad socket
        // Sätter upp Socketens incoming- och outputstream
        Socket socket = new Socket(SERVER, PORT);
        BufferedReader incoming = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter outgoing = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

        //Läser in första meddelandet från Servern
        String response = incoming.readLine();  //Läser in ett medelande och sen skriver ut (outgoing) det.
        System.out.println("Server response: " + response);

        // Initierar SMTP konversation med servern. EHLO stödjer ESMTP.
        outgoing.println("EHLO " + SERVER);
        outgoing.flush();
        messageReader(incoming);

        // StartTLS är ett protokollkommando
        // Används för att informera servern om att klienten vill uppgradera från osäker till säker anslutning med TLS eller SSL.
        outgoing.println("STARTTLS");
        outgoing.flush();
        message = incoming.readLine();

        // Skickar bara lösenordet utifall att det är en säker anslutning
        if (message.contains("220")) {
            System.out.println(message);
        }
        else {
            System.out.println(message);
            System.out.println("Connection failed");
            System.exit(1);
        }


        // Skapar ett Socket factory för SSL Sockets.
        SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        // Vi skapade först en standard socket och slår nu in den i en SSLSocket.
        SSLSocket sslSocket = (SSLSocket) socketFactory.createSocket(socket, SERVER, PORT, true); 
        
        incoming = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
        outgoing = new PrintWriter(sslSocket.getOutputStream(), true);

        // EHLO commanded skrivs in nu igen efter den säkra anslutningen har skett
        outgoing.println("EHLO " + SERVER);
        System.out.println(incoming.readLine());
        //messageReader(incoming);

        // Försöker logga in (Authentication Login)
        outgoing.println("AUTH LOGIN");
        System.out.println(incoming.readLine());
        //messageReader(incoming);

        //Encodear till Base 64 format, det är en metod för att binärt data ska kunna kodas med 7 bitars ASCII-tecken
        Base64.Encoder encoder = Base64.getEncoder();
        String usernameEncode = encoder.encodeToString(username.getBytes());
        String passwordEncode = encoder.encodeToString(password.getBytes());
        
        // Skickar iväg det encodeade användarnamnet och lösenordet
        messageWriter(outgoing, usernameEncode);
        System.out.println(incoming.readLine());
        //messageReader(incoming);
        
        messageWriter(outgoing, passwordEncode);
        System.out.println(incoming.readLine());
        //messageReader(incoming);

        // Kommandot MAIL FROM initierar en överföring av e-post.
        messageWriter(outgoing, "MAIL FROM:<" + username + "@kth.se>");
        System.out.println(incoming.readLine());
        //messageReader(incoming);
        
        // RCPT TO specificerar E-mail addressen av mottagaren
        // Vill man skicka det till flera kan man skapa flera RCPT TO under, det går bra
        //Man borde få ut 250 OK
        messageWriter(outgoing, "RCPT TO:<" + recipient + "@kth.se>");
        System.out.println(incoming.readLine());
        //messageReader(incoming);
        
        // Kommandot Data definierar informationen som datatexten har i meddelandet.
        messageWriter(outgoing, "DATA");
        System.out.println(incoming.readLine());
        //messageReader(incoming);
        
        messageWriter(outgoing, message);
        System.out.println(incoming.readLine());
        //messageReader(incoming);
        
        //Borde inte komma fler meddelanden efter vi nått 221.
        outgoing.println("QUIT");
        System.out.println(incoming.readLine());
        //messageReader(incoming);
        
        sslSocket.close();
        socket.close();
    }
    
    public static void messageWriter(PrintWriter outgoing, String message) {
        outgoing.println(message);
        //outgoing.flush();
        System.out.println(message);
    }
    
    //Skriver ut det resulterande svaret från servern
    public static void messageReader(BufferedReader input) throws IOException {
        String message;
        while ((message = input.readLine()) != null) {
            System.out.println("Server response: " + message);
            if ( message.contains("220 ") || message.contains("250 ") || message.contains("334 "))
                break;
        }
    }
}
