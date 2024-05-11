package com.rpatino12.epam.gym.service;

import com.rpatino12.epam.gym.dto.UserLogin;
import com.rpatino12.epam.gym.exception.ResourceNotFoundException;
import com.rpatino12.epam.gym.exception.TrainerNullException;
import com.rpatino12.epam.gym.feignclients.TrainerFeignClient;
import com.rpatino12.epam.gym.model.Trainer;
import com.rpatino12.epam.gym.model.TrainingType;
import com.rpatino12.epam.gym.model.TrainingTypes;
import com.rpatino12.epam.gym.model.User;
import com.rpatino12.epam.gym.repo.TrainerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @InjectMocks
    private TrainerService trainerService;

    @Mock
    private UserService userService;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private TrainerFeignClient trainerFeignClient;

    private Trainer trainer;
    private User user;
    private TrainingType specialization;
    private List<Trainer> trainerList;

    @BeforeEach
    public void setUp(){
        user = new User();
        user.setId(1L);
        user.setFirstName("trainerFirst");
        user.setLastName("trainerLast");
        user.setUsername(user.getFirstName() + "." + user.getLastName());
        user.setPassword("trainerPassword");
        user.setIsActive(true);

        specialization = new TrainingType();
        specialization.setTrainingTypeId(1L);
        specialization.setTrainingTypeName(TrainingTypes.Fitness);

        trainer = new Trainer();
        trainer.setTrainerId(1L);
        trainer.setSpecialization(specialization);
        trainer.setUser(user);

        trainerList = getMockTrainers(4);
    }

    @Test
    @DisplayName("save() with given trainer return UserLogin object")
    void save() {
        Mockito.doReturn(trainer).when(trainerRepository).save(trainer);
        Mockito.doReturn(user).when(userService).registerUser(user);
        UserLogin userLogin = trainerService.save(trainer);

        assertNotNull(userLogin);
        assertEquals(user.getUsername(), userLogin.getUsername());
        assertEquals(user.getPassword(), userLogin.getPassword());
    }

    @Test
    @DisplayName("Working with null trainer will throw TrainerNullException")
    void throwsTrainerNullException(){
        Trainer trainerNull = null;
        assertThrows(TrainerNullException.class,
                () -> trainerService.save(trainerNull),
                "Exception not throw as expected"
        );
        assertThrows(TrainerNullException.class,
                () -> trainerService.update(trainerNull, user.getUsername()),
                "Exception not throw as expected"
        );
    }

    @Test
    @DisplayName("No entity found will throw ResourceNotFoundException")
    void throwsResourceNotFoundException(){
        List<Trainer> emptyTrainerList = new ArrayList<>();
        Mockito.doReturn(emptyTrainerList).when(trainerRepository).findAll();
        Mockito.doReturn(user).when(userService).updateUser(user, "notAnUser");
        Mockito.doReturn(Optional.empty()).when(trainerRepository).findTrainerByUserUsername("notAnUser");

        assertThrows(
                ResourceNotFoundException.class,
                () -> trainerService.getAll(),
                "Exception not throw as expected"
        );
        assertThrows(
                ResourceNotFoundException.class,
                () -> trainerService.update(trainer, "notAnUser"),
                "Exception not throw as expected"
        );
    }

    @Test
    @DisplayName("update() with given trainer and username return Trainer object")
    void update() {
        Mockito.when(trainerRepository.save(any(Trainer.class))).thenReturn(trainer);
        Mockito.when(trainerRepository.findTrainerByUserUsername(trainer.getUser().getUsername())).thenReturn(Optional.of(trainer));
        Mockito.doReturn(user).when(userService).updateUser(user, user.getUsername());

        User newUser = user;
        newUser.setFirstName("Ricardo");

        Trainer newTrainer = new Trainer();
        newTrainer.setTrainerId(trainer.getTrainerId());
        newTrainer.setSpecialization(trainer.getSpecialization());
        newTrainer.setUser(trainer.getUser());

        newTrainer = trainerService.update(newTrainer, trainer.getUser().getUsername());

        assertNotNull(newTrainer);
        assertEquals(newTrainer.getSpecialization(), specialization);
        assertEquals("Ricardo", newTrainer.getUser().getFirstName());
    }

    @Test
    @DisplayName("getAll() return all trainers list")
    void getAll() {
        Mockito.doReturn(this.trainerList).when(trainerRepository).findAll();
        List<Trainer> trainers = trainerService.getAll();

        assertNotNull(trainers);
        assertEquals(4, trainers.size());
        assertIterableEquals(this.trainerList, trainers);
    }

    @Test
    @DisplayName("getByUsername() with given username return Optional Trainer object")
    void getByUsername() {
        Mockito.doReturn(Optional.of(trainer)).when(trainerRepository).findTrainerByUserUsername(user.getUsername());
        Optional<Trainer> optionalTrainer = trainerService.getByUsername(trainer.getUser().getUsername());

        assertNotNull(optionalTrainer.get());
        assertEquals("trainerFirst", optionalTrainer.get().getUser().getFirstName());
        assertEquals(optionalTrainer.get(), trainer);
    }

    @Test
    @DisplayName("updatePassword() with given username and passwords return update successful")
    void updatePassword() {
        Mockito.doReturn(Optional.of(trainer)).when(trainerRepository).findTrainerByUserUsername(user.getUsername());
        Mockito.doReturn(user).when(userService).updateUserPassword("securePassword", user.getId());

        String updatePasswordResult = trainerService.updatePassword(user.getUsername(), user.getPassword(), "securePassword");
        assertEquals("Password updated", updatePasswordResult);
    }

    @Test
    @DisplayName("updatePassword() when newPassword is the same as oldPassword doesn't update")
    void not_updatePassword() {
        Mockito.doReturn(Optional.of(trainer)).when(trainerRepository).findTrainerByUserUsername(user.getUsername());

        String updatePasswordResult = trainerService.updatePassword(user.getUsername(), user.getPassword(), "trainerPassword");
        assertEquals("New password cannot be the same as old password", updatePasswordResult);
    }

    @Test
    @DisplayName("updateActiveStatus() with given username and password activate/deactivate trainer")
    void updateActiveStatus() {
        Mockito.doReturn(user).when(userService).updateStatus(false, user.getId());
        Mockito.doReturn(Optional.of(trainer)).when(trainerRepository).findTrainerByUserUsername(user.getUsername());

        String deactivated = trainerService.updateActiveStatus(user.getUsername(), user.getPassword());

        assertEquals("User deactivated", deactivated);
    }

    private List<Trainer> getMockTrainers(int count){
        List<Trainer> trainers = new ArrayList<>(count);
        for (long i = 0; i < count; i++) {
            long id = i + 1;
            User u =  new User();
            u.setId(id);
            u.setFirstName("trainerFirst" + id);
            u.setLastName("trainerLast" + id);
            u.setUsername(u.getFirstName() + "." + u.getLastName());
            u.setPassword("trainerPassword" + id);
            u.setIsActive(true);

            TrainingType s = new TrainingType();
            s.setTrainingTypeId(1L);
            s.setTrainingTypeName(TrainingTypes.Fitness);

            Trainer t = new Trainer();
            t.setTrainerId(id);
            t.setSpecialization(s);
            t.setUser(u);

            trainers.add(t);
        }
        return trainers;
    }
}