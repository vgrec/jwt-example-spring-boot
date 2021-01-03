package com.example.demo;

import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTests {
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    private UserController userController;

    private static final String ENCODED_PASSWORD = "***encoded_password***";

    @Before
    public void setUp() {
        userController = new UserController(userRepository, cartRepository, bCryptPasswordEncoder);
    }

    @Test
    public void createUserFailure() {
        ResponseEntity<User> responseEntity = userController.createUser(
                createUserRequest("password", "confirm_password")
        );
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void createUserSuccess() {
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn(ENCODED_PASSWORD);

        CreateUserRequest user = createUserRequest("password", "password");
        ResponseEntity<User> response = userController.createUser(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        User createdUser = response.getBody();
        assertNotNull(createdUser);
        assertEquals(user.getUsername(), createdUser.getUsername());
        assertEquals(ENCODED_PASSWORD, createdUser.getPassword());
    }

    @Test
    public void findByUserNameFailure() {
        ResponseEntity<User> response = userController.findByUserName(null);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void findByUserNameSuccess() {
        User user = new User();
        user.setUsername("user");

        when(userRepository.findByUsername(anyString())).thenReturn(user);
        ResponseEntity<User> response = userController.findByUserName(user.getUsername());

        assertEquals(HttpStatus.OK, response.getStatusCode());

        User foundUser = response.getBody();
        assertNotNull(foundUser);
        assertEquals(user.getUsername(), foundUser.getUsername());
    }

    private CreateUserRequest createUserRequest(String password, String confirmPassword) {
        return new CreateUserRequest("lavilas", password, confirmPassword);
    }

}
