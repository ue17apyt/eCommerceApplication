package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final Logger logger = getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/id/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        this.logger.info("TASK: Find user by a specified ID " + id);
        Optional<User> optionalUser = this.userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            this.logger.error("ERROR: User {} is not found. Failure to find the user.", id);
            return notFound().build();
        }
        this.logger.info("COMPLETION: Find user {} successfully.", id);
        return ok(optionalUser.get());
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> findByUserName(@PathVariable String username) {
        this.logger.info("TASK: Find user by a specified username " + username);
        User user = this.userRepository.findByUsername(username);
        if (user == null) {
            this.logger.error("ERROR: User {} is not found. Failure to find the user.", username);
            return notFound().build();
        }
        this.logger.info("COMPLETION: Find user {} successfully.", username);
        return ok(user);
    }

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
        this.logger.info("TASK: Create a user");
        User user = new User();
        user.setUsername(createUserRequest.getUsername());
        this.logger.info("STEP: Username is set with {}", createUserRequest.getUsername());
        Cart cart = new Cart();
        this.cartRepository.save(cart);
        user.setCart(cart);
        if (createUserRequest.getPassword().length() < 8 ||
                !createUserRequest.getPassword().equals(createUserRequest.getConfirmedPassword())
        ) {
            this.logger.error(
                    "ERROR: Non-compliant password. Failure to create user {}.", createUserRequest.getUsername()
            );
            return badRequest().build();
        }
        user.setPassword(this.bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
        this.logger.info("COMPLETION: Create user {} successfully.", createUserRequest.getUsername());
        this.userRepository.save(user);
        return ok(user);
    }

}