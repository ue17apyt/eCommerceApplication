package com.example.demo.controllers;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final Logger logger = getLogger(OrderController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @PostMapping("/submit/{username}")
    public ResponseEntity<UserOrder> submit(@PathVariable String username) {
        this.logger.info("TASK: Submit order for a specified username " + username);
        User user = this.userRepository.findByUsername(username);
        if (user == null) {
            this.logger.error("ERROR: User {} is not found. Failure to submit order.", username);
            return notFound().build();
        }
        UserOrder order = UserOrder.createFromCart(user.getCart());
        this.orderRepository.save(order);
        this.logger.info("COMPLETION: Submit order for user {} successfully.", username);
        return ok(order);
    }

    @GetMapping("/history/{username}")
    public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
        this.logger.info("TASK: Get order list for a specified username " + username);
        User user = this.userRepository.findByUsername(username);
        if (user == null) {
            this.logger.error("ERROR: User {} is not found. Failure to get order list.", username);
            return notFound().build();
        }
        this.logger.info("COMPLETION: Get order list for user {} successfully", username);
        return ok(this.orderRepository.findByUser(user));
    }

}