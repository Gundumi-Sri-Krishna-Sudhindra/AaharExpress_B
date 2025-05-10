package AaharExpress.controller;

import AaharExpress.model.User;
import AaharExpress.service.UserService;
import AaharExpress.payload.response.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5175"}, maxAge = 3600)
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard() {
        // This would be replaced with actual admin dashboard data
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("userCount", "Total user count statistics");
        dashboard.put("restaurantCount", "Total restaurant count");
        dashboard.put("customerCount", "Total customer count");
        dashboard.put("deliveryAgentCount", "Total delivery agent count");
        dashboard.put("orderCount", "Total order count");
        dashboard.put("revenue", "Total revenue");
        
        return ResponseEntity.ok(dashboard);
    }
    
    @GetMapping("/users/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/users/customers")
    public ResponseEntity<List<User>> getAllCustomers() {
        List<User> customers = userService.getUsersByRole("ROLE_CUSTOMER");
        return ResponseEntity.ok(customers);
    }
    
    @GetMapping("/users/restaurants")
    public ResponseEntity<List<User>> getAllRestaurants() {
        List<User> restaurants = userService.getUsersByRole("ROLE_RESTAURANT");
        return ResponseEntity.ok(restaurants);
    }
    
    @GetMapping("/users/delivery-agents")
    public ResponseEntity<List<User>> getAllDeliveryAgents() {
        List<User> deliveryAgents = userService.getUsersByRole("ROLE_DELIVERY_AGENT");
        return ResponseEntity.ok(deliveryAgents);
    }
    
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> {
                    userService.deleteUser(id);
                    return ResponseEntity.ok(new MessageResponse("User deleted successfully!"));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUserByAdmin(@PathVariable Long id, @RequestBody Map<String, String> updates) {
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
                    
                    if (updates.containsKey("username")) {
                        user.setUsername(updates.get("username"));
                    }
                    
                    if (updates.containsKey("email")) {
                        user.setEmail(updates.get("email"));
                    }
                    
                    // Admin can update any user details
                    
                    userService.updateUser(user);
                    return ResponseEntity.ok(new MessageResponse("User updated successfully by admin!"));
                })
                .orElse(ResponseEntity.notFound().build());
    }
} 