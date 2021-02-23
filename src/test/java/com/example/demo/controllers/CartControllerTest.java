package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

public class CartControllerTest {

    @InjectMocks
    private CartController cartController;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    private Item firstItem;
    private Item secondItem;
    private User user;
    private ModifyCartRequest modifyCartRequest;

    @BeforeEach
    public void setUp() {

        openMocks(this);

        this.user = new User();
        this.user.setId(1L);
        this.user.setUsername("testUser");

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(this.user);

        this.firstItem = new Item();
        this.firstItem.setId(1L);
        this.firstItem.setName("firstTestItem");
        this.firstItem.setPrice(new BigDecimal(7.98));

        this.secondItem = new Item();
        this.secondItem.setId(2L);
        this.secondItem.setName("secondTestItem");
        this.secondItem.setPrice(new BigDecimal(2.43));

        cart.addItem(this.firstItem);
        cart.addItem(this.secondItem);
        this.user.setCart(cart);

        this.modifyCartRequest = new ModifyCartRequest();
        this.modifyCartRequest.setUsername("testUser");
        this.modifyCartRequest.setItemId(2L);
        this.modifyCartRequest.setQuantity(1);

        when(this.itemRepository.findById(2L)).thenReturn(ofNullable(this.secondItem));
        when(this.userRepository.findByUsername("testUser")).thenReturn(this.user);

    }

    @Test
    public void testAddExistingItemToCart() {

        final ResponseEntity<Cart> cartResponseEntity = this.cartController.addToCart(this.modifyCartRequest);
        assertNotNull(cartResponseEntity);
        assertEquals(OK, cartResponseEntity.getStatusCode());

        Cart testCart = cartResponseEntity.getBody();
        assertNotNull(testCart);
        assertTrue(testCart.getItems().contains(this.firstItem));
        assertTrue(testCart.getItems().contains(this.secondItem));
        assertEquals(3, testCart.getItems().size());
        assertSame(this.user, testCart.getUser());
        assertEquals(7.98 + 2.43 * 2, testCart.getTotal().doubleValue(), 0.001);

    }

    @Test
    public void testAddToCartByWrongItemId() {
        this.modifyCartRequest.setItemId(3L);
        final ResponseEntity<Cart> cartResponseEntity = this.cartController.addToCart(this.modifyCartRequest);
        assertNotNull(cartResponseEntity);
        assertEquals(NOT_FOUND, cartResponseEntity.getStatusCode());
    }

    @Test
    public void testAddToCartByWrongUsername() {
        this.modifyCartRequest.setUsername("anotherUsername");
        final ResponseEntity<Cart> cartResponseEntity = this.cartController.addToCart(this.modifyCartRequest);
        assertNotNull(cartResponseEntity);
        assertEquals(NOT_FOUND, cartResponseEntity.getStatusCode());
    }

    @Test
    public void testRemoveExistingItemFromCart() {

        final ResponseEntity<Cart> cartResponseEntity = this.cartController.removeFromCart(this.modifyCartRequest);
        assertNotNull(cartResponseEntity);
        assertEquals(OK, cartResponseEntity.getStatusCode());

        Cart testCart = cartResponseEntity.getBody();
        assertNotNull(testCart);
        assertTrue(testCart.getItems().contains(this.firstItem));
        assertFalse(testCart.getItems().contains(this.secondItem));
        assertEquals(1, testCart.getItems().size());
        assertSame(this.user, testCart.getUser());
        assertEquals(7.98, testCart.getTotal().doubleValue(), 0.001);

    }

    @Test
    public void testRemoveFromCartByWrongItemId() {
        this.modifyCartRequest.setItemId(3L);
        final ResponseEntity<Cart> cartResponseEntity = this.cartController.removeFromCart(this.modifyCartRequest);
        assertNotNull(cartResponseEntity);
        assertEquals(NOT_FOUND, cartResponseEntity.getStatusCode());
    }

    @Test
    public void testRemoveFromCartByWrongUsername() {
        this.modifyCartRequest.setUsername("anotherUsername");
        final ResponseEntity<Cart> cartResponseEntity = this.cartController.removeFromCart(this.modifyCartRequest);
        assertNotNull(cartResponseEntity);
        assertEquals(NOT_FOUND, cartResponseEntity.getStatusCode());
    }

}