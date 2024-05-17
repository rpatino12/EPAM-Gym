package com.rpatino12.epam.gym.service;

import com.rpatino12.epam.gym.dto.UserLogin;
import com.rpatino12.epam.gym.exception.ResourceNotFoundException;
import com.rpatino12.epam.gym.exception.TraineeNullException;
import com.rpatino12.epam.gym.model.Trainee;
import com.rpatino12.epam.gym.model.User;
import com.rpatino12.epam.gym.repo.TraineeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @InjectMocks
    private TraineeService traineeService;

    @Mock
    private UserService userService;
    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private Trainee trainee;
    private User user;
    private List<Trainee> traineeList;

    @BeforeEach
    public void setUp(){
        user = new User();
        user.setId(1L);
        user.setFirstName("testFirst");
        user.setLastName("testLast");
        user.setUsername(user.getFirstName() + "." + user.getLastName());
        user.setPassword("testPassword");
        user.setIsActive(true);

        trainee = new Trainee();
        trainee.setTraineeId(1L);
        trainee.setAddress("1234 Main Street #1");
        trainee.setDateOfBirth(new Date(System.currentTimeMillis()));
        trainee.setUser(user);

        traineeList = getMockTrainees(2);
    }

    @Test
    @DisplayName("getAll() return all trainees list")
    void getAll(){
        Mockito.doReturn(this.traineeList).when(traineeRepository).findAll();
        List<Trainee> trainees = traineeService.getAll();

        assertNotNull(trainees);
        assertEquals(2, (long) trainees.size());
        assertIterableEquals(this.traineeList, trainees);
    }

    @Test
    @DisplayName("save() with given trainee return UserLogin object")
    void save() {
        Mockito.doReturn(trainee).when(traineeRepository).save(trainee);
        Mockito.doReturn(user).when(userService).registerUser(user);
        Mockito.doReturn(user.getPassword()).when(passwordEncoder).encode(user.getPassword());
        UserLogin userLogin = traineeService.save(trainee);

        assertNotNull(userLogin);
        assertEquals(user.getUsername(), userLogin.getUsername());
        assertEquals(user.getPassword(), userLogin.getPassword());
    }

    @Test
    @DisplayName("Working with null trainee will throw TraineeNullException")
    void throwsTraineeNullException(){
        Trainee traineeNull = null;
        assertThrows(
                TraineeNullException.class,
                () -> traineeService.save(traineeNull),
                "Exception not throw as expected"
        );
        assertThrows(
                TraineeNullException.class,
                () -> traineeService.update(traineeNull, user.getUsername()),
                "Exception not throw as expected"
        );
    }

    @Test
    @DisplayName("No entity found will throw ResourceNotFoundException")
    void throwsResourceNotFoundException(){
        List<Trainee> emptyTraineeList = new ArrayList<>();
        Mockito.doReturn(emptyTraineeList).when(traineeRepository).findAll();
        Mockito.doReturn(user).when(userService).updateUser(user, "notAnUser");
        Mockito.doReturn(Optional.empty()).when(traineeRepository).findTraineeByUserUsername("notAnUser");


        assertThrows(
                ResourceNotFoundException.class,
                () -> traineeService.getAll(),
                "Exception not throw as expected"
        );
        assertThrows(
                ResourceNotFoundException.class,
                () -> traineeService.update(trainee, "notAnUser"),
                "Exception not throw as expected"
        );

    }

    @Test
    @DisplayName("update() with given trainee and username return Trainee object")
    void update() {
        Mockito.when(traineeRepository.save(any(Trainee.class))).thenReturn(trainee);
        Mockito.when(traineeRepository.findTraineeByUserUsername(trainee.getUser().getUsername())).thenReturn(Optional.of(trainee));
        Mockito.doReturn(user).when(userService).updateUser(user, user.getUsername());

        Trainee newTrainee = new Trainee();
        newTrainee.setTraineeId(trainee.getTraineeId());
        newTrainee.setAddress(trainee.getAddress());
        newTrainee.setDateOfBirth(trainee.getDateOfBirth());
        newTrainee.setUser(trainee.getUser());

        newTrainee = traineeService.update(newTrainee, newTrainee.getUser().getUsername());
        assertNotNull(newTrainee);
        assertEquals(newTrainee.getAddress(), trainee.getAddress());
    }

    @Test
    @DisplayName("deleteByUsername() with given username return true")
    void deleteByUsername() {
        Mockito.doNothing().when(traineeRepository).deleteByUserUsername(user.getUsername());
        Mockito.doReturn(Optional.of(trainee)).when(traineeRepository).findTraineeByUserUsername(user.getUsername());
        boolean isTraineeDeleted = traineeService.deleteByUsername(user.getUsername());

        assertTrue(isTraineeDeleted);
    }

    @Test
    @DisplayName("getByUsername() with given username return Optional Trainee object")
    void getByUsername() {
        Mockito.doReturn(Optional.of(trainee)).when(traineeRepository).findTraineeByUserUsername(user.getUsername());
        Optional<Trainee> optionalTrainee = traineeService.getByUsername(user.getUsername());

        assertNotNull(optionalTrainee.get());
        assertEquals("testFirst", optionalTrainee.get().getUser().getFirstName());
    }

    @Test
    @DisplayName("updatePassword() with given username and passwords return update successful")
    void updatePassword() {
        Mockito.doReturn(Optional.of(trainee)).when(traineeRepository).findTraineeByUserUsername(trainee.getUser().getUsername());
        Mockito.doReturn(user).when(userService).updateUserPassword("newPassword", user.getId());

        String updatePasswordResult = traineeService.updatePassword(user.getUsername(), user.getPassword(), "newPassword");
        assertEquals("Password updated", updatePasswordResult);
    }

    @Test
    @DisplayName("updatePassword() when newPassword is the same as oldPassword doesn't update")
    void not_updatePassword() {
        Mockito.doReturn(Optional.of(trainee)).when(traineeRepository).findTraineeByUserUsername(trainee.getUser().getUsername());

        String updatePasswordResult = traineeService.updatePassword(user.getUsername(), user.getPassword(), "testPassword");
        assertEquals("New password cannot be the same as old password", updatePasswordResult);
    }

    @Test
    @DisplayName("updateActiveStatus() with given username and password activate/deactivate trainee")
    void updateActiveStatus() {
        Mockito.doReturn(user).when(userService).updateStatus(false, user.getId());
        Mockito.doReturn(Optional.of(trainee)).when(traineeRepository).findTraineeByUserUsername(user.getUsername());

        String deactivated = traineeService.updateActiveStatus(user.getUsername(), user.getPassword());

        assertEquals("User deactivated", deactivated);
    }

    private List<Trainee> getMockTrainees(int count){
        List<Trainee> trainees = new ArrayList<>(count);
        for (long i = 0; i < count; i++) {
            long id = i + 1;
            User u =  new User();
            u.setId(id);
            u.setFirstName("testFirst" + id);
            u.setLastName("testLast" + id);
            u.setUsername(u.getFirstName() + "." + u.getLastName());
            u.setPassword("testPassword" + id);
            u.setIsActive(true);

            Trainee t = new Trainee();
            t.setTraineeId(id);
            t.setAddress("1234 Main Street #" + id);
            t.setDateOfBirth(new Date(System.currentTimeMillis()));
            t.setUser(u);

            trainees.add(t);
        }
        return trainees;
    }
}