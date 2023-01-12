package main;

import java.io.File;
import java.util.*;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * This class is a simple mail program implemented with Java EE (JavaMail). It 
 * takes user input and sends a message to a target recipient on the KTH mail 
 * server. 
 *
 * @author Alexander Lundqvist & Ramin Shojaei
 */
public class Sender {
    
    /**
     * This method sets the properties used to configure the server connection.
     * @return the properties object
     */
    private static Properties setProperties() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.kth.se");
        properties.put("mail.smtp.port", "587");
        return properties;
    }
       
    /**
     * Main method.
     * @param args takes no input arguments
     */    
    public static void main(String[] args) {
        try {
            final String username;
            final String password;
            final String recipient;
            final String subject;
            final String text;
            Properties properties;

            /* 
            First we set the initial variables for the program by getting
            the users input. Then we set the properties used to configure the server 
            connection.
            */
            Scanner file = new Scanner(new File("src/resources/Credentials.txt"));
            username = file.nextLine();
            password = file.nextLine();
            file.close();

            Scanner in = new Scanner(System.in);
            System.out.println("Recipient: ");
            recipient = in.nextLine();
            System.out.println("Message subject: ");
            subject = in.nextLine();
            System.out.println("Type your message: ");
            text = in.nextLine(); // Should be something else since we only read one line now.
            in.close();
            properties = setProperties();
                       
            /*
            We then need to get the session object.
            */
            Session emailSession = Session.getInstance(properties,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                       return new PasswordAuthentication(username, password);
                    }
                }
            );
            
            /*
            If all is good, we then try to send a message to the recipient.
            */
            try {
                /*
                First we create a default MimeMessage with the session that we
                have obtained.
                */
                Message message = new MimeMessage(emailSession);
                
                /*
                Here we set all the relevant header fields for the message.
                */
                message.setFrom(new InternetAddress(username + "@kth.se"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient + "@kth.se"));
                message.setSubject(subject);
                message.setText(text);
                message.setSentDate(new Date());
                
                /* 
                Finally, we send the message.
                */
                Transport.send(message);

                System.out.println("Sent message successfully!");
            } catch (MessagingException exception) {
                System.err.println("Failed to send message...");
                exception.printStackTrace();
            }
            
        } catch (Exception exception) {
            System.err.println("Total program failure.");
            exception.printStackTrace();
        } 
    }   
}
