package AaharExpress.controller;

import AaharExpress.model.Order;
import AaharExpress.model.OrderStatus;
import AaharExpress.model.User;
import AaharExpress.payload.response.MessageResponse;
import AaharExpress.repository.OrderRepository;
import AaharExpress.repository.UserRepository;
import AaharExpress.security.services.UserDetailsImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/delivery")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5175", "https://aahar-express-f.vercel.app"}, maxAge = 3600)
public class DeliveryController {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/available-orders")
    @PreAuthorize("hasRole('DELIVERY_AGENT')")
    public ResponseEntity<?> getAvailableOrders() {
        // Get orders that are ready for pickup but don't have a delivery agent assigned
        List<Order> availableOrders = orderRepository.findByStatus(OrderStatus.READY_FOR_PICKUP).stream()
                .filter(order -> order.getDeliveryAgent() == null)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(availableOrders);
    }
    
    @GetMapping("/my-deliveries")
    @PreAuthorize("hasRole('DELIVERY_AGENT')")
    public ResponseEntity<?> getMyDeliveries() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        Optional<User> userOpt = userRepository.findById(userDetails.getId());
        if (!userOpt.isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("User not found"));
        }
        
        List<Order> activeDeliveries = orderRepository.findByDeliveryAgentAndStatus(
                userOpt.get(), OrderStatus.OUT_FOR_DELIVERY);
        
        return ResponseEntity.ok(activeDeliveries);
    }
    
    @GetMapping("/delivery-history")
    @PreAuthorize("hasRole('DELIVERY_AGENT')")
    public ResponseEntity<?> getDeliveryHistory() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        Optional<User> userOpt = userRepository.findById(userDetails.getId());
        if (!userOpt.isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("User not found"));
        }
        
        List<Order> completedDeliveries = orderRepository.findByDeliveryAgentAndStatus(
                userOpt.get(), OrderStatus.DELIVERED);
        
        return ResponseEntity.ok(completedDeliveries);
    }
    
    @PostMapping("/accept-order/{orderId}")
    @PreAuthorize("hasRole('DELIVERY_AGENT')")
    public ResponseEntity<?> acceptOrder(@PathVariable Long orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        Optional<User> userOpt = userRepository.findById(userDetails.getId());
        if (!userOpt.isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("User not found"));
        }
        
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (!orderOpt.isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Order not found"));
        }
        
        Order order = orderOpt.get();
        
        // Verify order is in the right state
        if (order.getStatus() != OrderStatus.READY_FOR_PICKUP) {
            return ResponseEntity.badRequest().body(new MessageResponse("Order is not ready for pickup"));
        }
        
        // Verify order doesn't already have a delivery agent
        if (order.getDeliveryAgent() != null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Order already has a delivery agent assigned"));
        }
        
        // Assign the delivery agent to the order
        order.setDeliveryAgent(userOpt.get());
        order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
        order.setEstimatedDeliveryTime(LocalDateTime.now().plusMinutes(30)); // Example: 30 min delivery time
        
        Order updatedOrder = orderRepository.save(order);
        
        return ResponseEntity.ok(updatedOrder);
    }
    
    @PostMapping("/complete-delivery/{orderId}")
    @PreAuthorize("hasRole('DELIVERY_AGENT')")
    public ResponseEntity<?> completeDelivery(@PathVariable Long orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        Optional<User> userOpt = userRepository.findById(userDetails.getId());
        if (!userOpt.isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("User not found"));
        }
        
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (!orderOpt.isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Order not found"));
        }
        
        Order order = orderOpt.get();
        
        // Verify order is in the right state
        if (order.getStatus() != OrderStatus.OUT_FOR_DELIVERY) {
            return ResponseEntity.badRequest().body(new MessageResponse("Order is not out for delivery"));
        }
        
        // Verify the delivery agent is assigned to this order
        if (order.getDeliveryAgent() == null || !order.getDeliveryAgent().getId().equals(userOpt.get().getId())) {
            return ResponseEntity.badRequest().body(new MessageResponse("You are not assigned to this delivery"));
        }
        
        // Complete the delivery
        order.setStatus(OrderStatus.DELIVERED);
        order.setActualDeliveryTime(LocalDateTime.now());
        
        Order updatedOrder = orderRepository.save(order);
        
        return ResponseEntity.ok(updatedOrder);
    }
    
    @GetMapping("/stats")
    @PreAuthorize("hasRole('DELIVERY_AGENT')")
    public ResponseEntity<?> getDeliveryStats() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        Optional<User> userOpt = userRepository.findById(userDetails.getId());
        if (!userOpt.isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("User not found"));
        }
        
        List<Order> allDeliveries = orderRepository.findByDeliveryAgent(userOpt.get());
        List<Order> completedDeliveries = allDeliveries.stream()
                .filter(order -> order.getStatus() == OrderStatus.DELIVERED)
                .collect(Collectors.toList());
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDeliveries", completedDeliveries.size());
        stats.put("activeDeliveries", allDeliveries.stream()
                .filter(order -> order.getStatus() == OrderStatus.OUT_FOR_DELIVERY)
                .count());
        
        // This could be expanded with more stats like average delivery time, earnings, etc.
        
        return ResponseEntity.ok(stats);
    }
} 