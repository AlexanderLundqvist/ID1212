package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

/**
 * This class is a simple mail program implemented with Java EE (JavaMail). It 
 * takes user input and retrieves the latest mail from the account.
 *
 * @author Alexander Lundqvist & Ramin Shojaei
 */
public class Receiver {
    
    /**
     * This method sets the properties used to configure the server connection.
     * @return the properties object
     */
    private static Properties setProperties() {
        Properties properties = System.getProperties();
        properties.setProperty("mail.imap.port", "993");
        properties.setProperty("mail.imap.host", "webmail.kth.se");
        properties.setProperty("mail.store.protocol", "imaps");
        properties.setProperty("mail.mime.charset", "utf-8");
        return properties;
    }

    /**
     * Main method.
     * @param args takes no input arguments
     */    
    public static void main(String[] args) {
        final String host = "webmail.kth.se";
        final String username;
        final String password;
        Properties properties;

        try {
            /* 
            First we set the initial variables for the program by getting
            the users input. Then we set the properties used to configure the server 
            connection.
            */
            System.out.println("Message retreiver service has started!");
            Scanner file = new Scanner(new File("src/resources/Credentials.txt"));
            username = file.nextLine();
            password = file.nextLine();
            file.close();
            System.out.println("Login credentials has been processed.");
            properties = setProperties();
            
            Session emailSession = Session.getInstance(properties);
            
            try {
                Store store = emailSession.getStore("imaps");
                store.connect(host, username, password);
                Folder inbox = store.getFolder("INBOX");
                inbox.open(Folder.READ_ONLY);
                Message[] messages = inbox.getMessages();

                Message message = messages[messages.length - 1];
               
                System.out.println();
                System.out.println("Subject: " + MimeUtility.decodeText(message.getSubject()));
                System.out.println("From: " + MimeUtility.decodeText(message.getFrom()[0].toString()));
                MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
                System.out.println("Message: " + mimeMultipart.getBodyPart(0).getContent());
                System.out.println();
                
            } catch (MessagingException exception) {
                System.err.println("Failed to retreive message..." + exception.getMessage());
                exception.printStackTrace();
            }
        } catch (Exception exception) {
            System.err.println("Total program failure." + exception.getMessage());
            exception.printStackTrace();
        }
        
    }
}
