package AaharExpress.service;

import AaharExpress.model.User;
import AaharExpress.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private PasswordEncoder encoder;

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    public boolean forgotPassword(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            // Generate a temporary password
            String temporaryPassword = generateTemporaryPassword();
            
            // Update user with new password
            user.setPassword(encoder.encode(temporaryPassword));
            userRepository.save(user);
            
            // Send email with temporary password
            emailService.sendPasswordRecoveryEmail(email, user.getUsername(), temporaryPassword);
            return true;
        }
        
        return false;
    }
    
    private String generateTemporaryPassword() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder password = new StringBuilder();
        Random random = new Random();
        
        // Generate random password of length 10
        for (int i = 0; i < 10; i++) {
            password.append(characters.charAt(random.nextInt(characters.length())));
        }
        
        return password.toString();
    }
}
