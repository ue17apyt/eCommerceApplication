package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

public class OrderControllerTest {

    @InjectMocks
    private OrderController orderController;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    private Cart firstCart;
    private Cart secondCart;
    private User user;
    private List<UserOrder> userOrders = new ArrayList<>();

    @BeforeEach
    public void setUp() {

        openMocks(this);

        this.user = new User();
        this.user.setId(1L);
        this.user.setUsername("testUsername");
        this.user.setPassword("testPassword");

        Item firstItem = new Item();
        firstItem.setId(1L);
        firstItem.setName("firstTestItem");
        firstItem.setPrice(new BigDecimal(3.21));
        firstItem.setDescription("First Item Description");

        Item secondItem = new Item();
        secondItem.setId(2L);
        secondItem.setName("secondTestItem");
        secondItem.setPrice(new BigDecimal(5.43));
        secondItem.setDescription("Second Item Description");

        Item thirdItem = new Item();
        thirdItem.setId(3L);
        thirdItem.setName("thirdTestItem");
        thirdItem.setPrice(new BigDecimal(7.65));
        thirdItem.setDescription("Third Item Description");

        Item fourthItem = new Item();
        fourthItem.setId(4L);
        fourthItem.setName("fourthTestItem");
        fourthItem.setPrice(new BigDecimal(9.87));
        fourthItem.setDescription("Fourth Item Description");

        this.firstCart = new Cart();
        this.firstCart.setId(1L);
        this.firstCart.addItem(firstItem);
        this.firstCart.addItem(secondItem);
        this.firstCart.setUser(this.user);

        this.secondCart = new Cart();
        this.secondCart.setId(2L);
        this.secondCart.addItem(thirdItem);
        this.secondCart.addItem(fourthItem);
        this.secondCart.setUser(this.user);

        this.user.setCart(this.firstCart);

        when(this.userRepository.findByUsername("testUsername")).thenReturn(this.user);
        when(this.orderRepository.findByUser(this.user)).thenReturn(this.userOrders);

    }

    @Test
    public void testSubmitCorrectOrder() {

        final ResponseEntity<UserOrder> userOrderResponseEntity = this.orderController.submit("testUsername");
        assertNotNull(userOrderResponseEntity);
        assertEquals(OK, userOrderResponseEntity.getStatusCode());

        UserOrder userOrder = userOrderResponseEntity.getBody();
        assertNotNull(userOrder);
        assertEquals(2, userOrder.getItems().size());
        assertEquals(this.user, userOrder.getUser());
        assertEquals(3.21 + 5.43, userOrder.getTotal().doubleValue(), 0.001);

    }

    @Test
    public void testSubmitWrongOrder() {
        final ResponseEntity<UserOrder> userOrderResponseEntity =
                this.orderController.submit("anotherUsername");
        assertNotNull(userOrderResponseEntity);
        assertEquals(NOT_FOUND, userOrderResponseEntity.getStatusCode());
    }

    @Test
    public void testGetOrdersForCorrectUser() {

        ResponseEntity<UserOrder> userOrderResponseEntity = this.orderController.submit("testUsername");
        assertNotNull(userOrderResponseEntity);
        UserOrder testUserOrder = userOrderResponseEntity.getBody();
        assertNotNull(testUserOrder);
        this.userOrders.add(testUserOrder);

        this.user.setCart(this.secondCart);
        userOrderResponseEntity = this.orderController.submit("testUsername");
        assertNotNull(userOrderResponseEntity);
        testUserOrder = userOrderResponseEntity.getBody();
        assertNotNull(testUserOrder);
        this.userOrders.add(testUserOrder);

        ResponseEntity<List<UserOrder>> userOrdersResponseEntity =
                this.orderController.getOrdersForUser("testUsername");
        assertNotNull(userOrdersResponseEntity);
        assertEquals(2, userOrdersResponseEntity.getBody().size());
        assertEquals(2, userOrdersResponseEntity.getBody().get(0).getItems().size());
        assertEquals(2, userOrdersResponseEntity.getBody().get(1).getItems().size());
        assertEquals(
                3.21 + 5.43, userOrdersResponseEntity.getBody().get(0).getTotal().doubleValue(), 0.001
        );
        assertEquals(
                7.65 + 9.87,
                userOrdersResponseEntity.getBody().get(1).getTotal().doubleValue(),
                0.001
        );

        this.user.setCart(this.firstCart);

    }

    @Test
    public void testGetOrdersForWrongUser() {
        final ResponseEntity<List<UserOrder>> userOrdersResponseEntity =
                this.orderController.getOrdersForUser("anotherUsername");
        assertNotNull(userOrdersResponseEntity);
        assertEquals(NOT_FOUND, userOrdersResponseEntity.getStatusCode());
    }

}