package AaharExpress.controller;

import AaharExpress.model.*;
import AaharExpress.payload.response.MessageResponse;
import AaharExpress.repository.*;
import AaharExpress.security.services.UserDetailsImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5175"}, maxAge = 3600)
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private MenuItemRepository menuItemRepository;
    
    @Autowired
    private RestaurantRepository restaurantRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getMyOrders() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        Optional<User> userOpt = userRepository.findById(userDetails.getId());
        if (!userOpt.isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("User not found"));
        }
        
        List<Order> orders = orderRepository.findByUser(userOpt.get());
        
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        Optional<User> userOpt = userRepository.findById(userDetails.getId());
        if (!userOpt.isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("User not found"));
        }
        
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (!orderOpt.isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Order not found"));
        }
        
        Order order = orderOpt.get();
        
        // Verify that the order belongs to the user (or add admin check)
        if (!order.getUser().getId().equals(userOpt.get().getId())) {
            return ResponseEntity.badRequest().body(new MessageResponse("You don't have permission to view this order"));
        }
        
        return ResponseEntity.ok(order);
    }
    
    @PostMapping("/place-order")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> placeOrder(@Valid @RequestBody Map<String, Object> orderRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        Optional<User> userOpt = userRepository.findById(userDetails.getId());
        if (!userOpt.isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("User not found"));
        }
        
        // Get restaurant ID from request
        Long restaurantId = Long.parseLong(orderRequest.get("restaurantId").toString());
        Optional<Restaurant> restaurantOpt = restaurantRepository.findById(restaurantId);
        if (!restaurantOpt.isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Restaurant not found"));
        }
        
        Restaurant restaurant = restaurantOpt.get();
        
        // Verify restaurant is open
        if (!restaurant.isOpen()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Restaurant is currently closed"));
        }
        
        // Create new order
        Order order = new Order();
        order.setUser(userOpt.get());
        order.setRestaurant(restaurant);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PLACED);
        order.setDeliveryAddress(orderRequest.get("deliveryAddress").toString());
        
        if (orderRequest.containsKey("deliveryNote")) {
            order.setDeliveryNote(orderRequest.get("deliveryNote").toString());
        }
        
        // Process order items
        List<Map<String, Object>> items = (List<Map<String, Object>>) orderRequest.get("items");
        if (items == null || items.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Order must contain at least one item"));
        }
        
        Order savedOrder = orderRepository.save(order);
        
        for (Map<String, Object> itemRequest : items) {
            Long menuItemId = Long.parseLong(itemRequest.get("menuItemId").toString());
            Integer quantity = Integer.parseInt(itemRequest.get("quantity").toString());
            
            Optional<MenuItem> menuItemOpt = menuItemRepository.findById(menuItemId);
            if (!menuItemOpt.isPresent()) {
                orderRepository.delete(savedOrder);
                return ResponseEntity.badRequest().body(new MessageResponse("Menu item not found: " + menuItemId));
            }
            
            MenuItem menuItem = menuItemOpt.get();
            
            // Verify menu item belongs to the restaurant
            if (!menuItem.getRestaurant().getId().equals(restaurant.getId())) {
                orderRepository.delete(savedOrder);
                return ResponseEntity.badRequest().body(new MessageResponse("Menu item does not belong to the selected restaurant"));
            }
            
            // Verify menu item is available
            if (!menuItem.isAvailable()) {
                orderRepository.delete(savedOrder);
                return ResponseEntity.badRequest().body(new MessageResponse("Menu item is not available: " + menuItem.getName()));
            }
            
            OrderItem orderItem = new OrderItem(menuItem, quantity, menuItem.getPrice());
            
            if (itemRequest.containsKey("specialInstructions")) {
                orderItem.setSpecialInstructions(itemRequest.get("specialInstructions").toString());
            }
            
            orderItem.setOrder(savedOrder);
            orderItemRepository.save(orderItem);
        }
        
        // Calculate total amount
        savedOrder.calculateTotalAmount();
        Order updatedOrder = orderRepository.save(savedOrder);
        
        return ResponseEntity.ok(updatedOrder);
    }
    
    @PostMapping("/cancel/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        Optional<User> userOpt = userRepository.findById(userDetails.getId());
        if (!userOpt.isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("User not found"));
        }
        
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (!orderOpt.isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Order not found"));
        }
        
        Order order = orderOpt.get();
        
        // Verify that the order belongs to the user
        if (!order.getUser().getId().equals(userOpt.get().getId())) {
            return ResponseEntity.badRequest().body(new MessageResponse("You don't have permission to cancel this order"));
        }
        
        // Verify that the order can be cancelled (not already delivered or cancelled)
        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            return ResponseEntity.badRequest().body(new MessageResponse("Order cannot be cancelled in its current state"));
        }
        
        order.setStatus(OrderStatus.CANCELLED);
        Order updatedOrder = orderRepository.save(order);
        
        return ResponseEntity.ok(updatedOrder);
    }
    
    @GetMapping("/restaurants/{restaurantId}/menu")
    public ResponseEntity<?> getRestaurantMenu(@PathVariable Long restaurantId) {
        Optional<Restaurant> restaurantOpt = restaurantRepository.findById(restaurantId);
        if (!restaurantOpt.isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Restaurant not found"));
        }
        
        List<MenuItem> menuItems = menuItemRepository.findByRestaurant(restaurantOpt.get());
        
        return ResponseEntity.ok(menuItems);
    }
    
    @GetMapping("/restaurants")
    public ResponseEntity<?> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantRepository.findAll();
        
        return ResponseEntity.ok(restaurants);
    }
} 