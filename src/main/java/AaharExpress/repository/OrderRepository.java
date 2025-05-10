package AaharExpress.repository;

import AaharExpress.model.Order;
import AaharExpress.model.OrderStatus;
import AaharExpress.model.Restaurant;
import AaharExpress.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
    List<Order> findByRestaurant(Restaurant restaurant);
    List<Order> findByDeliveryAgent(User deliveryAgent);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByRestaurantAndStatus(Restaurant restaurant, OrderStatus status);
    List<Order> findByDeliveryAgentAndStatus(User deliveryAgent, OrderStatus status);
} 