package AaharExpress.controller;

import AaharExpress.model.User;
import AaharExpress.service.UserService;
import AaharExpress.payload.response.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/customer")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5175"}, maxAge = 3600)
@PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
public class CustomerController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard() {
        // This would be replaced with actual customer data
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("orders", "List of customer orders");
        dashboard.put("favorites", "Customer favorite restaurants");
        dashboard.put("recommendations", "Recommended restaurants");
        
        return ResponseEntity.ok(dashboard);
    }
    
    @GetMapping("/profile/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isUserSelf(#id)")
    public ResponseEntity<?> getCustomerProfile(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> {
                    Map<String, Object> profile = new HashMap<>();
                    profile.put("id", user.getId());
                    profile.put("username", user.getUsername());
                    profile.put("fullName", user.getFullName());
                    profile.put("email", user.getEmail());
                    profile.put("mobileNumber", user.getMobileNumber());
                    profile.put("address", user.getAddress());
                    profile.put("memberSince", user.getMemberSince());
                    // Add customer-specific details here
                    
                    return ResponseEntity.ok(profile);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/profile/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isUserSelf(#id)")
    public ResponseEntity<?> updateCustomerProfile(@PathVariable Long id, @RequestBody Map<String, String> updates) {
        return userService.getUserById(id)
                .map(user -> {
                    if (updates.containsKey("fullName")) {
                        user.setFullName(updates.get("fullName"));
                    }
                    
                    if (updates.containsKey("mobileNumber")) {
                        user.setMobileNumber(updates.get("mobileNumber"));
                    }
                    
                    if (updates.containsKey("address")) {
                        user.setAddress(updates.get("address"));
                    }
                    
                    // Update customer-specific details here
                    
                    userService.updateUser(user);
                    return ResponseEntity.ok(new MessageResponse("Customer profile updated successfully!"));
                })
                .orElse(ResponseEntity.notFound().build());
    }
} 