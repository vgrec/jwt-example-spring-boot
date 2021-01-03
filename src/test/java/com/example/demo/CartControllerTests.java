package com.example.demo;

import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTests {
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);

    private CartController cartController = new CartController(userRepository, cartRepository, itemRepository);

    @Test
    public void testAddToCard_UserNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(null);

        ResponseEntity<Cart> responseEntity = cartController.addTocart(
                new ModifyCartRequest("unknown_user", 1, 100)
        );
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void testAddToCard_ItemNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(new User());
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<Cart> responseEntity = cartController.addTocart(
                new ModifyCartRequest("username", 1, 100)
        );
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void testAddToCard_Success() {
        Cart emptyCart = new Cart();

        User user = new User();
        user.setUsername("username");
        user.setCart(emptyCart);

        Item item = new Item();
        item.setName("lavilas");
        item.setPrice(new BigDecimal(10));

        when(userRepository.findByUsername(anyString())).thenReturn(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ResponseEntity<Cart> responseEntity = cartController.addTocart(
                new ModifyCartRequest("username", 1, 2)
        );

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        Cart cart = responseEntity.getBody();

        assertNotNull(cart);
        assertEquals(new BigDecimal(20), cart.getTotal());
        assertEquals(Arrays.asList(item, item), cart.getItems());
    }
}
