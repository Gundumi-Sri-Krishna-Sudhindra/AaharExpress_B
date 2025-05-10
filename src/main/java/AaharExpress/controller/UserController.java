package AaharExpress.controller;

import AaharExpress.model.User;
import AaharExpress.service.UserService;
import AaharExpress.payload.response.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5175"}, maxAge = 3600)
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isUserSelf(#id)")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        Optional<User> user = userService.getUserByUsername(username);
        return user.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping("/customers")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANT')")
    public ResponseEntity<List<User>> getAllCustomers() {
        List<User> customers = userService.getUsersByRole("ROLE_CUSTOMER");
        return ResponseEntity.ok(customers);
    }
    
    @GetMapping("/restaurants")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<List<User>> getAllRestaurants() {
        List<User> restaurants = userService.getUsersByRole("ROLE_RESTAURANT");
        return ResponseEntity.ok(restaurants);
    }
    
    @GetMapping("/delivery-agents")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANT')")
    public ResponseEntity<List<User>> getAllDeliveryAgents() {
        List<User> deliveryAgents = userService.getUsersByRole("ROLE_DELIVERY_AGENT");
        return ResponseEntity.ok(deliveryAgents);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isUserSelf(#id)")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody Map<String, String> updates) {
        Optional<User> userOptional = userService.getUserById(id);
        
        if (!userOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        User user = userOptional.get();
        
        if (updates.containsKey("fullName")) {
            user.setFullName(updates.get("fullName"));
        }
        
        if (updates.containsKey("mobileNumber")) {
            user.setMobileNumber(updates.get("mobileNumber"));
        }
        
        if (updates.containsKey("address")) {
            user.setAddress(updates.get("address"));
        }
        
        userService.updateUser(user);
        
        return ResponseEntity.ok(new MessageResponse("User details updated successfully!"));
    }
    
    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> adminUpdateUser(@PathVariable Long id, @RequestBody Map<String, String> updates) {
        Optional<User> userOptional = userService.getUserById(id);
        
        if (!userOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        User user = userOptional.get();
        
        if (updates.containsKey("fullName")) {
            user.setFullName(updates.get("fullName"));
        }
        
        if (updates.containsKey("mobileNumber")) {
            user.setMobileNumber(updates.get("mobileNumber"));
        }
        
        if (updates.containsKey("address")) {
            user.setAddress(updates.get("address"));
        }
        
        // Additional fields that only admin can update
        if (updates.containsKey("username")) {
            user.setUsername(updates.get("username"));
        }
        
        if (updates.containsKey("email")) {
            user.setEmail(updates.get("email"));
        }
        
        userService.updateUser(user);
        
        return ResponseEntity.ok(new MessageResponse("User details updated successfully by admin!"));
    }
}
