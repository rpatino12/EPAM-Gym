package com.rpatino12.epam.gym.service;

import com.rpatino12.epam.gym.exception.ResourceNotFoundException;
import com.rpatino12.epam.gym.exception.UserNullException;
import com.rpatino12.epam.gym.model.User;
import com.rpatino12.epam.gym.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private User user;
    private List<User> userList;

    @BeforeEach
    public void setUp(){
        user = new User();
        user.setId(1L);
        user.setFirstName("testFirst");
        user.setLastName("testLast");
        user.setUsername(user.getFirstName() + "." + user.getLastName());
        user.setPassword("testPassword");
        user.setIsActive(true);

        userList = getMockUsers(5);
    }

    @Test
    @DisplayName("registerUser() with given user return user object")
    void registerUser() {
        Mockito.when(userRepository.save(any(User.class))).thenReturn(user);
        User userSaved = userService.registerUser(user);

        assertNotNull(userSaved);
        assertEquals(user, userSaved);
    }

    @Test
    @DisplayName("Working with null user will throw UserNullException")
    void throwsUserNullException(){
        User userNull = null;
        assertThrows(
                UserNullException.class,
                () -> userService.registerUser(userNull),
                "Exception not throw as expected"
        );
        assertThrows(
                UserNullException.class,
                () -> userService.updateUser(userNull, user.getUsername()),
                "Exception not throw as expected"
        );
    }

    @Test
    @DisplayName("No entity found will throw ResourceNotFoundException")
    void throwsResourceNotFoundException(){
        Mockito.doReturn(Optional.empty()).when(userRepository).findByUsername("notAnUser");
        Mockito.doReturn(Optional.empty()).when(userRepository).findById(0L);
        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.updateUser(user, "notAnUser"),
                "Exception not throw as expected"
        );
        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.updateUserPassword("newPassword", 0L),
                "Exception not throw as expected"
        );
        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.updateStatus(true, 0L),
                "Exception not throw as expected"
        );
    }

    @Test
    @DisplayName("getUserByUsername() with given username return Optional user object")
    void getUserByUsername() {
        Mockito.doReturn(Optional.of(user)).when(userRepository).findByUsername(user.getUsername());
        Optional<User> user1 = userService.getUserByUsername("testFirst.testLast");

        assertNotNull(user1);
        assertEquals("testFirst", user1.get().getFirstName());
        assertEquals(user, user1.get());
    }

    @Test
    @DisplayName("updateUser() with given user and username return user object")
    void updateUser() {
        Mockito.when(userRepository.save(any(User.class))).thenReturn(user);
        Mockito.doReturn(Optional.of(user)).when(userRepository).findByUsername(user.getUsername());

        User newUser = new User();
        newUser.setId(user.getId());
        newUser.setFirstName("updateFirst");
        newUser.setLastName("updateLast");
        newUser.setUsername(newUser.getFirstName() + "." + newUser.getLastName());
        newUser.setPassword("testPassword");
        newUser.setIsActive(true);
        newUser = userService.updateUser(newUser, user.getUsername());

        assertNotNull(newUser);
        assertEquals(user.getFirstName(), newUser.getFirstName());
    }

    @Test
    @DisplayName("updateUserPassword() with given user id and newPassword return user object")
    void updateUserPassword() {
        Mockito.when(userRepository.save(any(User.class))).thenReturn(user);
        Mockito.doReturn(Optional.of(user)).when(userRepository).findById(user.getId());
        Mockito.when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        User user1 = userService.updateUserPassword("newPassword", user.getId());

        assertNotNull(user1);
        assertEquals(user.getFirstName(), user1.getFirstName());
        assertEquals("encodedPassword", user1.getPassword());
    }

    @Test
    @DisplayName("updateStatus() with given user id and isActive return user object")
    void updateStatus() {
        Mockito.when(userRepository.save(any(User.class))).thenReturn(user);
        Mockito.doReturn(Optional.of(user)).when(userRepository).findById(user.getId());
        User user1 = userService.updateStatus(false, user.getId());

        assertNotNull(user1);
        assertFalse(user1.getIsActive());
    }

    @Test
    @DisplayName("authenticate() with given username and password return true")
    void authenticate() {
        Mockito.doReturn(Optional.of(user)).when(userRepository).findByUsername(user.getUsername());
        Mockito.when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        boolean isAuthenticated = userService.authenticate(user.getUsername(), user.getPassword());

        assertTrue(isAuthenticated);
    }

    @Test
    @DisplayName("authenticate() with wrong username return false")
    void authenticate_user_not_found() {
        Mockito.doReturn(Optional.empty()).when(userRepository).findByUsername("notAnUser");
        boolean isAuthenticated = userService.authenticate("notAnUser", "testPassword");

        assertFalse(isAuthenticated);
    }

    @Test
    @DisplayName("authenticate() with wrong password return false")
    void authenticate_wrong_password() {
        Mockito.doReturn(Optional.of(user)).when(userRepository).findByUsername(user.getUsername());
        boolean isAuthenticated = userService.authenticate(user.getUsername(), "wrongPassword");

        assertFalse(isAuthenticated);
    }

    private List<User> getMockUsers(int count){
        List<User> users = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            User user1 = new User();
            user1.setId((long) i);
            user1.setFirstName("testFirst");
            user1.setLastName("testLast");
            user1.setUsername(user1.getFirstName() + "." + user1.getLastName());
            user1.setPassword("testPassword");
            user1.setIsActive(true);

            users.add(user1);
        }
        return users;
    }
}