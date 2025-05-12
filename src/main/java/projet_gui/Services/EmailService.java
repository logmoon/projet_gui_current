package projet_gui.Services;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Service class to handle email operations including password recovery emails.
 */
public class EmailService {
    
    private static EmailService instance;

    private final String username;
    private final String password;
    private final String host;
    private final String port;
    private final boolean debug;

    public static synchronized EmailService getInstance() {
        if (instance == null) {
            instance = new EmailService();
        }
        return instance;
    }
    
    /**
     * Constructor that loads email configuration from .env file
     */
    public EmailService() {
        // Load environment variables from .env file
        Dotenv dotenv = Dotenv.load();
        
        this.username = dotenv.get("EMAIL_USERNAME");
        this.password = dotenv.get("EMAIL_PASSWORD");
        this.host = dotenv.get("EMAIL_HOST", "smtp.gmail.com");
        this.port = dotenv.get("EMAIL_PORT", "587");
        this.debug = Boolean.parseBoolean(dotenv.get("EMAIL_DEBUG", "false"));
        
        if (username == null || password == null) {
            throw new IllegalStateException("Email credentials not found in environment variables. "
                    + "Make sure EMAIL_USERNAME and EMAIL_PASSWORD are set in your .env file.");
        }
    }
    
    /**
     * Send a password recovery email with a reset token
     * 
     * @param recipientEmail The recipient's email address
     * @param resetToken The password reset token
     * @return true if email sent successfully, false otherwise
     */
    public boolean sendPasswordRecoveryEmail(String recipientEmail, String resetToken) {
        // Subject and body for password recovery email
        String subject = "Password Recovery - Your App Name";
        String body = "Hello,\n\n"
                + "You requested a password reset for your account. "
                + "Please use the following token to reset your password:\n\n"
                + resetToken + "\n\n"
                + "If you didn't request this, please ignore this email.\n\n"
                + "Regards,\nFarmerAssist";
        
        return sendEmail(recipientEmail, subject, body);
    }
    
    /**
     * General method to send an email
     * 
     * @param to The recipient's email address
     * @param subject The email subject
     * @param body The email body
     * @return true if email sent successfully, false otherwise
     */
    public boolean sendEmail(String to, String subject, String body) {
        // Get system properties
        Properties props = new Properties();
        
        // Setup mail server properties
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        
        // Add TLS protocol configuration to fix SSL handshake issue
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.trust", host);
        
        // Enable debugging if configured
        if (debug) {
            props.put("mail.debug", "true");
        }
        
        try {
            // Create session with authenticator
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
            
            // Create a message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);
            
            // Send the message
            Transport.send(message);
            
            System.out.println("Password recovery email sent successfully to: " + to);
            return true;
            
        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}