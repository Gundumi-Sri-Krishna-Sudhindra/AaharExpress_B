package AaharExpress.controller;

import AaharExpress.model.MenuItem;
import AaharExpress.model.Restaurant;
import AaharExpress.model.User;
import AaharExpress.payload.response.MessageResponse;
import AaharExpress.repository.MenuItemRepository;
import AaharExpress.repository.RestaurantRepository;
import AaharExpress.repository.UserRepository;
import AaharExpress.security.services.UserDetailsImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/restaurants")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5175", "https://aahar-express-f.vercel.app"}, maxAge = 3600)
public class RestaurantController {

    @Autowired
    private RestaurantRepository restaurantRepository;
    
    @Autowired
    private MenuItemRepository menuItemRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/my-restaurant")
    @PreAuthorize("hasRole('RESTAURANT')")
    public ResponseEntity<?> getMyRestaurant() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        Optional<User> userOpt = userRepository.findById(userDetails.getId());
        if (!userOpt.isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("User not found"));
        }
        
        Optional<Restaurant> restaurantOpt = restaurantRepository.findByOwner(userOpt.get());
        if (!restaurantOpt.isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Restaurant not found for this user"));
        }
        
        return ResponseEntity.ok(restaurantOpt.get());
    }
    
    @PostMapping("/create")
    @PreAuthorize("hasRole('RESTAURANT')")
    public ResponseEntity<?> createRestaurant(@Valid @RequestBody Restaurant restaurantRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        Optional<User> userOpt = userRepository.findById(userDetails.getId());
        if (!userOpt.isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("User not found"));
        }
        
        // Check if user already has a restaurant
        Optional<Restaurant> existingRestaurant = restaurantRepository.findByOwner(userOpt.get());
        if (existingRestaurant.isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("You already have a restaurant"));
        }
        
        Restaurant restaurant = new Restaurant(
            restaurantRequest.getName(),
            restaurantRequest.getDescription(),
            restaurantRequest.getAddress(),
            restaurantRequest.getPhoneNumber(),
            restaurantRequest.getCuisine()
        );
        
        restaurant.setOwner(userOpt.get());
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        
        return ResponseEntity.ok(savedRestaurant);
    }
    
    @PutMapping("/update")
    @PreAuthorize("hasRole('RESTAURANT')")
    public ResponseEntity<?> updateRestaurant(@Valid @RequestBody Restaurant restaurantRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        Optional<User> userOpt = userRepository.findById(userDetails.getId());
        if (!userOpt.isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("User not found"));
        }
        
        Optional<Restaurant> restaurantOpt = restaurantRepository.findByOwner(userOpt.get());
        if (!restaurantOpt.isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Restaurant not found for this user"));
        }
        
        Restaurant restaurant = restaurantOpt.get();
        restaurant.setName(restaurantRequest.getName());
        restaurant.setDescription(restaurantRequest.getDescription());
        restaurant.setAddress(restaurantRequest.getAddress());
        restaurant.setPhoneNumber(restaurantRequest.getPhoneNumber());
        restaurant.setCuisine(restaurantRequest.getCuisine());
        restaurant.setOpen(restaurantRequest.isOpen());
        
        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);
        
        return ResponseEntity.ok(updatedRestaurant);
    }
    
    @GetMapping("/menu")
    @PreAuthorize("hasRole('RESTAURANT')")
    public ResponseEntity<?> getMenu() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        Optional<User> userOpt = userRepository.findById(userDetails.getId());
        if (!userOpt.isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("User not found"));
        }
        
        Optional<Restaurant> restaurantOpt = restaurantRepository.findByOwner(userOpt.get());
        if (!restaurantOpt.isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Restaurant not found for this user"));
        }
        
        List<MenuItem> menuItems = menuItemRepository.findByRestaurant(restaurantOpt.get());
        
        return ResponseEntity.ok(menuItems);
    }
    
    @PostMapping("/menu/add")
    @PreAuthorize("hasRole('RESTAURANT')")
    public ResponseEntity<?> addMenuItem(@Valid @RequestBody MenuItem menuItemRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        Optional<User> userOpt = userRepository.findById(userDetails.getId());
        if (!userOpt.isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("User not found"));
        }
        
        Optional<Restaurant> restaurantOpt = restaurantRepository.findByOwner(userOpt.get());
        if (!restaurantOpt.isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Restaurant not found for this user"));
        }
        
        MenuItem menuItem = new MenuItem(
            menuItemRequest.getName(),
            menuItemRequest.getDescription(),
            menuItemRequest.getPrice(),
            menuItemRequest.getCategory()
        );
        
        menuItem.setImageUrl(menuItemRequest.getImageUrl());
        menuItem.setAvailable(menuItemRequest.isAvailable());
        menuItem.setRestaurant(restaurantOpt.get());
        
        MenuItem savedMenuItem = menuItemRepository.save(menuItem);
        
        return ResponseEntity.ok(savedMenuItem);
    }
    
    @PutMapping("/menu/update/{id}")
    @PreAuthorize("hasRole('RESTAURANT')")
    public ResponseEntity<?> updateMenuItem(@PathVariable Long id, @Valid @RequestBody MenuItem menuItemRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        Optional<User> userOpt = userRepository.findById(userDetails.getId());
        if (!userOpt.isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("User not found"));
        }
        
        Optional<Restaurant> restaurantOpt = restaurantRepository.findByOwner(userOpt.get());
        if (!restaurantOpt.isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Restaurant not found for this user"));
        }
        
        Optional<MenuItem> menuItemOpt = menuItemRepository.findById(id);
        if (!menuItemOpt.isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Menu item not found"));
        }
        
        // Verify that the menu item belongs to the user's restaurant
        MenuItem menuItem = menuItemOpt.get();
        if (!menuItem.getRestaurant().getId().equals(restaurantOpt.get().getId())) {
            return ResponseEntity.badRequest().body(new MessageResponse("You don't have permission to update this menu item"));
        }
        
        menuItem.setName(menuItemRequest.getName());
        menuItem.setDescription(menuItemRequest.getDescription());
        menuItem.setPrice(menuItemRequest.getPrice());
        menuItem.setCategory(menuItemRequest.getCategory());
        menuItem.setImageUrl(menuItemRequest.getImageUrl());
        menuItem.setAvailable(menuItemRequest.isAvailable());
        
        MenuItem updatedMenuItem = menuItemRepository.save(menuItem);
        
        return ResponseEntity.ok(updatedMenuItem);
    }
    
    @DeleteMapping("/menu/delete/{id}")
    @PreAuthorize("hasRole('RESTAURANT')")
    public ResponseEntity<?> deleteMenuItem(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        Optional<User> userOpt = userRepository.findById(userDetails.getId());
        if (!userOpt.isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("User not found"));
        }
        
        Optional<Restaurant> restaurantOpt = restaurantRepository.findByOwner(userOpt.get());
        if (!restaurantOpt.isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Restaurant not found for this user"));
        }
        
        Optional<MenuItem> menuItemOpt = menuItemRepository.findById(id);
        if (!menuItemOpt.isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Menu item not found"));
        }
        
        // Verify that the menu item belongs to the user's restaurant
        MenuItem menuItem = menuItemOpt.get();
        if (!menuItem.getRestaurant().getId().equals(restaurantOpt.get().getId())) {
            return ResponseEntity.badRequest().body(new MessageResponse("You don't have permission to delete this menu item"));
        }
        
        menuItemRepository.delete(menuItem);
        
        return ResponseEntity.ok(new MessageResponse("Menu item deleted successfully"));
    }
} 