package com.example.demo;

import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTests {
    private UserRepository userRepository = mock(UserRepository.class);
    private OrderRepository orderRepository = mock(OrderRepository.class);

    private OrderController orderController = new OrderController(userRepository, orderRepository);

    @Test
    public void submitOrder_UserNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(null);
        ResponseEntity<UserOrder> responseEntity = orderController.submit("unknown_user");

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void submitOrder_Success() {
        User user = createUser();

        when(userRepository.findByUsername(anyString())).thenReturn(user);
        ResponseEntity<UserOrder> responseEntity = orderController.submit(user.getUsername());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        UserOrder order = responseEntity.getBody();
        assertNotNull(order);
        assertEquals(user.getCart().getItems(), order.getItems());
        assertEquals(user.getCart().getTotal(), order.getTotal());
        assertEquals(user.getUsername(), order.getUser().getUsername());
    }

    @Test
    public void getOrdersForUser_UserNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(null);
        ResponseEntity<UserOrder> responseEntity = orderController.submit("unknown_user");

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void getOrdersForUser_Success() {
        List<UserOrder> orders = createUserOrder();

        when(userRepository.findByUsername(anyString())).thenReturn(new User());
        when(orderRepository.findByUser(any(User.class))).thenReturn(orders);

        ResponseEntity<List<UserOrder>> responseEntity = orderController.getOrdersForUser("username");

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        List<UserOrder> orderList = responseEntity.getBody();
        assertNotNull(orderList);

        assertEquals(orders.size(), orderList.size());
        assertEquals(orders.get(0).getId(), orderList.get(0).getId());
        assertEquals(orders.get(0).getUser().getId(), orderList.get(0).getUser().getId());
    }

    private Item createItem() {
        Item item = new Item();
        item.setId(1L);
        item.setPrice(new BigDecimal(10));
        item.setName("Pizza");
        item.setDescription("Very tasty Pizza, with Ananas");

        return item;
    }

    private User createUser() {
        Item item = createItem();

        Cart cart = new Cart();
        cart.addItem(item);

        User user = new User();
        user.setId(1L);
        user.setUsername("lavilas");
        user.setCart(cart);
        cart.setUser(user);
        return user;
    }

    private List<UserOrder> createUserOrder() {
        Item item = createItem();
        User user = new User();
        user.setId(1L);

        UserOrder order = new UserOrder();
        order.setId(10L);
        order.setItems(Collections.singletonList(item));
        order.setUser(user);
        return Collections.singletonList(order);
    }
}
