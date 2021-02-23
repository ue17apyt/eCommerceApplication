package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.stream.IntStream;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    public final Logger logger = getLogger(CartController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ItemRepository itemRepository;

    @PostMapping("/addToCart")
    public ResponseEntity<Cart> addToCart(@RequestBody ModifyCartRequest request) {
        this.logger.info("TASK: Add items to cart for a specified user");
        User user = this.userRepository.findByUsername(request.getUsername());
        if (user == null) {
            this.logger.error("ERROR: User {} is not found. Failure to add items to cart.", request.getUsername());
            return status(NOT_FOUND).build();
        }
        Optional<Item> item = this.itemRepository.findById(request.getItemId());
        if (!item.isPresent()) {
            this.logger.error("ERROR: Item {} is not found. Failure to add the item.", request.getItemId());
            return status(NOT_FOUND).build();
        }
        Cart cart = user.getCart();
        IntStream.range(0, request.getQuantity()).forEach(i -> cart.addItem(item.get()));
        this.cartRepository.save(cart);
        this.logger.info("COMPLETION: Add items to cart for user {} successfully.", request.getUsername());
        return ok(cart);
    }

    @PostMapping("/removeFromCart")
    public ResponseEntity<Cart> removeFromCart(@RequestBody ModifyCartRequest request) {
        this.logger.info("TASK: Remove items from cart for a specified user");
        User user = this.userRepository.findByUsername(request.getUsername());
        if (user == null) {
            this.logger.error("ERROR: User {} is not found. Failure to remove items from cart.", request.getUsername());
            return status(NOT_FOUND).build();
        }
        Optional<Item> item = this.itemRepository.findById(request.getItemId());
        if (!item.isPresent()) {
            this.logger.error("ERROR: Item {} is not found. Failure to remove the item.", request.getItemId());
            return status(NOT_FOUND).build();
        }
        Cart cart = user.getCart();
        IntStream.range(0, request.getQuantity()).forEach(i -> cart.removeItem(item.get()));
        this.cartRepository.save(cart);
        this.logger.info("COMPLETION: Remove items from cart for user {} successfully.", request.getUsername());
        return ok(cart);
    }

}