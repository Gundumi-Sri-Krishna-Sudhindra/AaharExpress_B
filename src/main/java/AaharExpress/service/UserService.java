package AaharExpress.service;

import AaharExpress.model.User;
import AaharExpress.model.Role;
import AaharExpress.model.ERole;
import AaharExpress.repository.UserRepository;
import AaharExpress.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    public List<User> getUsersByRole(String roleName) {
        Optional<Role> roleOptional = roleRepository.findByName(ERole.valueOf(roleName));
        if (roleOptional.isPresent()) {
            Role role = roleOptional.get();
            return userRepository.findByRolesContaining(role);
        }
        return new ArrayList<>();
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
