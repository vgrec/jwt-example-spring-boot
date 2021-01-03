package com.example.demo;

import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static junit.framework.TestCase.assertEquals;

public class UserControllerTests {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CartRepository cartRepository;

    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private UserController userController;

    @Before
    public void setUp() {
        userController = new UserController(userRepository, cartRepository, bCryptPasswordEncoder);
    }

    @Test
    public void testCreateUserFailure() {
        ResponseEntity<User> responseEntity = userController.createUser(getUser());
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }


    private CreateUserRequest getUser() {
        return new CreateUserRequest("lavilas", "bartovski", "cimok");
    }


}
