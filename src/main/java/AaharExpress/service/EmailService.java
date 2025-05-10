package AaharExpress.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    public void sendPasswordRecoveryEmail(String toEmail, String username, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@aaharexpress.com");
        message.setTo(toEmail);
        message.setSubject("AaharExpress - Password Recovery");
        message.setText("Dear " + username + ",\n\n" +
                "We received a request to recover your password. A temporary password has been generated for your account:\n\n" +
                "Username: " + username + "\n" +
                "Temporary Password: " + password + "\n\n" +
                "Please use this temporary password to log in to your account. For security reasons, we strongly recommend changing your password immediately after logging in.\n\n" +
                "If you did not request this password reset, please contact our support team immediately.\n\n" +
                "Best regards,\n" +
                "AaharExpress Team");
        
        mailSender.send(message);
    }
    
    public void sendWelcomeEmail(String toEmail, String username, String fullName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@aaharexpress.com");
        message.setTo(toEmail);
        message.setSubject("Welcome to AaharExpress!");
        
        // Create personalized welcome message
        String greeting = (fullName != null && !fullName.isEmpty()) ? fullName : username;
        
        message.setText("Dear " + greeting + ",\n\n" +
                "Welcome to AaharExpress! We're excited to have you on board.\n\n" +
                "Your account has been successfully created with username: " + username + "\n\n" +
                "With AaharExpress, you can:\n" +
                "• Order delicious food from local restaurants\n" +
                "• Track your delivery in real-time\n" +
                "• Save your favorite restaurants and meals\n" +
                "• Get special offers and discounts\n\n" +
                "If you have any questions or need assistance, feel free to contact our support team.\n\n" +
                "Happy ordering!\n\n" +
                "Best regards,\n" +
                "The AaharExpress Team");
        
        mailSender.send(message);
    }
} 