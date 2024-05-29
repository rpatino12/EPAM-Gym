package com.rpatino12.epam.gym.service;

import com.rpatino12.epam.gym.dto.TrainerMonthlySummary;
import com.rpatino12.epam.gym.dto.UserLogin;
import com.rpatino12.epam.gym.exception.ResourceNotFoundException;
import com.rpatino12.epam.gym.exception.TrainerNullException;
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
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @InjectMocks
    private TrainerService trainerService;

    @Mock
    private UserService userService;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JmsTemplate jmsTemplate;

    private Trainer trainer;
    private User user;
    private TrainingType specialization;
    private List<Trainer> trainerList;
    private List<TrainerMonthlySummary> monthlySummaryList;

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

        monthlySummaryList = trainerList.stream().map(
                trainer1 -> {
                    TrainerMonthlySummary monthlySummary = new TrainerMonthlySummary();
                    monthlySummary.setUsername(trainer1.getUser().getUsername());
                    monthlySummary.setFirstName(trainer1.getUser().getFirstName());
                    monthlySummary.setLastName(trainer1.getUser().getLastName());
                    monthlySummary.setStatus(trainer1.getUser().getIsActive());
                    HashMap<String, Double> monthlyTrainings = new HashMap<>();
                    monthlyTrainings.put("2024-05", 39.0);
                    monthlyTrainings.put("2024-06", 20.0);
                    monthlySummary.setMonthlySummary(monthlyTrainings);
                    return monthlySummary;
                }).collect(Collectors.toList());
    }

    @Test
    @DisplayName("save() with given trainer return UserLogin object")
    void save() {
        Mockito.doReturn(trainer).when(trainerRepository).save(trainer);
        Mockito.doReturn(user).when(userService).registerUser(user);
        Mockito.doReturn(user.getPassword()).when(passwordEncoder).encode(user.getPassword());
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
        assertThrows(
                ResourceNotFoundException.class,
                () -> trainerService.getAllWorkloads(),
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
        Mockito.when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        String updatePasswordResult = trainerService.updatePassword(user.getUsername(), user.getPassword(), "securePassword");
        assertEquals("Password updated", updatePasswordResult);
    }

    @Test
    @DisplayName("updatePassword() when newPassword is the same as oldPassword doesn't update")
    void not_updatePassword() {
        Mockito.doReturn(Optional.of(trainer)).when(trainerRepository).findTrainerByUserUsername(user.getUsername());
        Mockito.when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        String updatePasswordResult = trainerService.updatePassword(user.getUsername(), user.getPassword(), "trainerPassword");
        assertEquals("New password cannot be the same as old password", updatePasswordResult);
    }

    @Test
    @DisplayName("updateActiveStatus() with given username and password activate/deactivate trainer")
    void updateActiveStatus() {
        Mockito.doReturn(user).when(userService).updateStatus(false, user.getId());
        Mockito.doReturn(Optional.of(trainer)).when(trainerRepository).findTrainerByUserUsername(user.getUsername());
        Mockito.when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        String deactivated = trainerService.updateActiveStatus(user.getUsername(), user.getPassword());

        assertEquals("User deactivated", deactivated);
    }

    @Test
    @DisplayName("getAllWorkloadsTest() should return the registered monthly workloads of all trainers")
    void getAllWorkloadsTest(){
        trainerService.receiveWorkloads(monthlySummaryList);
        List<TrainerMonthlySummary> trainerMonthlySummaryList = trainerService.getAllWorkloads();

        Mockito.verify(jmsTemplate).convertAndSend(Mockito.anyString(), Mockito.anyString());
        assertIterableEquals(monthlySummaryList, trainerMonthlySummaryList);
        assertEquals(4, trainerMonthlySummaryList.size());
    }

    @Test
    @DisplayName("getMonthlySummaryTest() should return the workload summary of trainer")
    void getMonthlySummaryTest(){
        trainerService.receiveWorkloads(monthlySummaryList);
        YearMonth yearMonth = YearMonth.of(2024, 05);
        Double trainerMonthlySummary = trainerService.getMonthlySummary("trainerFirst1.trainerLast1", yearMonth);

        assertEquals(39.0, trainerMonthlySummary);
    }

    @Test
    @DisplayName("noMonthlySummaryRegisteredForTrainer should return 0.0")
    void noMonthlySummaryRegisteredForTrainer(){
        trainerService.receiveWorkloads(monthlySummaryList);
        YearMonth yearMonth = YearMonth.of(2024, 05);
        Double trainerMonthlySummary = trainerService.getMonthlySummary("ricardo.patino", yearMonth);

        assertEquals(0.0, trainerMonthlySummary);
    }

    @Test
    public void testReceiveWorkloads() {
        trainerService.receiveWorkloads(monthlySummaryList);
        // Assert
        // Assuming that getAllWorkloads() returns the list set by receiveWorkloads()
        assertEquals(monthlySummaryList, trainerService.getAllWorkloads());
    }

    @Test
    public void testReceiveWorkloads_NullWorkloads() {
        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> trainerService.receiveWorkloads(null));
    }

    @Test
    public void testReceiveWorkloads_NullElementInWorkloads() {
        // Arrange
        List<TrainerMonthlySummary> workloads = Arrays.asList(null, new TrainerMonthlySummary());

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> trainerService.receiveWorkloads(workloads));
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