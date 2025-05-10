package AaharExpress.controller;

import AaharExpress.model.User;
import AaharExpress.repository.UserRepository;
import AaharExpress.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5175", "https://aahar-express-f.vercel.app"}, maxAge = 3600)
public class DashboardController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getDashboard() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        Map<String, Object> response = new HashMap<>();
        response.put("username", userDetails.getUsername());
        response.put("email", userDetails.getEmail());
        response.put("fullName", userDetails.getFullName());
        
        // Get user roles
        String roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        
        // Provide specific dashboard based on role
        if (roles.contains("ROLE_ADMIN")) {
            return getAdminDashboard(userDetails, response);
        } else if (roles.contains("ROLE_RESTAURANT")) {
            return getRestaurantDashboard(userDetails, response);
        } else if (roles.contains("ROLE_DELIVERY_AGENT")) {
            return getDeliveryDashboard(userDetails, response);
        } else {
            return getUserDashboard(userDetails, response);
        }
    }
    
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAdminDashboard() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Map<String, Object> response = new HashMap<>();
        
        return getAdminDashboard(userDetails, response);
    }
    
    @GetMapping("/restaurant")
    @PreAuthorize("hasRole('RESTAURANT')")
    public ResponseEntity<?> getRestaurantDashboard() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Map<String, Object> response = new HashMap<>();
        
        return getRestaurantDashboard(userDetails, response);
    }
    
    @GetMapping("/delivery")
    @PreAuthorize("hasRole('DELIVERY_AGENT')")
    public ResponseEntity<?> getDeliveryDashboard() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Map<String, Object> response = new HashMap<>();
        
        return getDeliveryDashboard(userDetails, response);
    }
    
    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getUserDashboard() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Map<String, Object> response = new HashMap<>();
        
        return getUserDashboard(userDetails, response);
    }
    
    private ResponseEntity<?> getAdminDashboard(UserDetailsImpl userDetails, Map<String, Object> response) {
        // Admin dashboard - can see all users, restaurants, orders, etc.
        response.put("dashboard", "admin");
        response.put("features", new String[]{
            "Manage Users", 
            "Manage Restaurants", 
            "Manage Delivery Agents", 
            "View All Orders",
            "System Analytics",
            "Content Management"
        });
        
        // Add user statistics that only admins can see
        response.put("totalUserCount", userRepository.count());
        
        return ResponseEntity.ok(response);
    }
    
    private ResponseEntity<?> getRestaurantDashboard(UserDetailsImpl userDetails, Map<String, Object> response) {
        // Restaurant dashboard - can manage menu items, see orders for their restaurant
        response.put("dashboard", "restaurant");
        response.put("features", new String[]{
            "Menu Management", 
            "Order Management", 
            "Restaurant Profile", 
            "Analytics",
            "Customer Reviews"
        });
        
        // In a real application, you would fetch restaurant-specific data here
        // For example, get the restaurant's menu items, open orders, etc.
        response.put("pendingOrders", 0); // Placeholder
        
        return ResponseEntity.ok(response);
    }
    
    private ResponseEntity<?> getDeliveryDashboard(UserDetailsImpl userDetails, Map<String, Object> response) {
        // Delivery dashboard - can see assigned orders and delivery locations
        response.put("dashboard", "delivery");
        response.put("features", new String[]{
            "Active Deliveries", 
            "Delivery History", 
            "Route Map", 
            "Earnings",
            "Profile Settings"
        });
        
        // In a real application, you would fetch delivery agent specific data here
        // For example, get the delivery agent's assigned orders, completed deliveries, etc.
        response.put("activeDeliveries", 0); // Placeholder
        
        return ResponseEntity.ok(response);
    }
    
    private ResponseEntity<?> getUserDashboard(UserDetailsImpl userDetails, Map<String, Object> response) {
        // Regular user dashboard - can browse restaurants, place orders, view order history
        response.put("dashboard", "user");
        response.put("features", new String[]{
            "Restaurant Browser", 
            "Place Order", 
            "Order History", 
            "Favorite Restaurants",
            "Profile Settings"
        });
        
        // In a real application, you would fetch user-specific data here
        // For example, get the user's recent orders, favorite restaurants, etc.
        response.put("recentOrders", 0); // Placeholder
        
        return ResponseEntity.ok(response);
    }
} 