package com.example.demo.controllers;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private User user;
    private CreateUserRequest createUserRequest;

    @BeforeEach
    public void setUp() {

        openMocks(this);

        this.user = new User();
        this.user.setId(1L);
        this.user.setUsername("testUsername");
        this.user.setPassword("testPassword");

        this.createUserRequest = new CreateUserRequest();
        this.createUserRequest.setUsername("testUsername");
        this.createUserRequest.setPassword("testPassword");
        this.createUserRequest.setConfirmedPassword("testPassword");

        when(this.bCryptPasswordEncoder.encode("testPassword")).thenReturn("testIsHashed");
        when(this.userRepository.findById(1L)).thenReturn(ofNullable(this.user));
        when(this.userRepository.findByUsername("testUsername")).thenReturn(this.user);

    }

    @Test
    public void testCreateUser() {

        final ResponseEntity<User> userResponseEntity = this.userController.createUser(this.createUserRequest);
        assertNotNull(userResponseEntity);
        assertEquals(OK, userResponseEntity.getStatusCode());

        User testUser = userResponseEntity.getBody();
        assertNotNull(testUser);
        assertEquals("testUsername", testUser.getUsername());
        assertEquals("testIsHashed", testUser.getPassword());

    }

    @Test
    public void testCreateUserWithoutMatchingPassword() {

        this.createUserRequest.setConfirmedPassword("anotherPassword");

        final ResponseEntity<User> userResponseEntity = this.userController.createUser(this.createUserRequest);
        assertNotNull(userResponseEntity);
        assertEquals(BAD_REQUEST, userResponseEntity.getStatusCode());

    }

    @Test
    public void testCreateUserWithShortPassword() {

        this.createUserRequest.setPassword("passAct");
        this.createUserRequest.setConfirmedPassword("passAct");

        final ResponseEntity<User> userResponseEntity = this.userController.createUser(this.createUserRequest);
        assertNotNull(userResponseEntity);
        assertEquals(BAD_REQUEST, userResponseEntity.getStatusCode());

    }

    @Test
    public void testFindUserByCorrectId() {

        final ResponseEntity<User> userResponseEntity = this.userController.findById(1L);
        assertNotNull(userResponseEntity);
        assertEquals(OK, userResponseEntity.getStatusCode());

        User testUser = userResponseEntity.getBody();
        assertNotNull(testUser);
        assertEquals(1L, testUser.getId());
        assertEquals("testUsername", testUser.getUsername());
        assertEquals("testPassword", testUser.getPassword());

    }

    @Test
    public void testFindUserByWrongId() {
        final ResponseEntity<User> userResponseEntity = this.userController.findById(2L);
        assertNotNull(userResponseEntity);
        assertEquals(NOT_FOUND, userResponseEntity.getStatusCode());
    }

    @Test
    public void testFindUserByCorrectUsername() {

        final ResponseEntity<User> userResponseEntity = this.userController.findByUserName("testUsername");
        assertNotNull(userResponseEntity);
        assertEquals(OK, userResponseEntity.getStatusCode());

        User testUser = userResponseEntity.getBody();
        assertNotNull(testUser);
        assertEquals(1L, testUser.getId());
        assertEquals("testUsername", testUser.getUsername());
        assertEquals("testPassword", testUser.getPassword());

    }

    @Test
    public void testFindUserByWrongUsername() {
        final ResponseEntity<User> userResponseEntity = this.userController.findByUserName("anotherUsername");
        assertNotNull(userResponseEntity);
        assertEquals(NOT_FOUND, userResponseEntity.getStatusCode());
    }

}