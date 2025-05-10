package AaharExpress.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5175"}, maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {
    @GetMapping("/all")
    public String allAccess() {
        return "Public Content.";
    }
    
    @GetMapping("/customer")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public String customerAccess() {
        return "Customer Content.";
    }
    
    @GetMapping("/restaurant")
    @PreAuthorize("hasRole('RESTAURANT') or hasRole('ADMIN')")
    public String restaurantAccess() {
        return "Restaurant Dashboard.";
    }
    
    @GetMapping("/delivery-agent")
    @PreAuthorize("hasRole('DELIVERY_AGENT') or hasRole('ADMIN')")
    public String deliveryAgentAccess() {
        return "Delivery Agent Dashboard.";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin Dashboard.";
    }
} 