package com.rpatino12.epam.gym.service;

import com.rpatino12.epam.gym.exception.ResourceNotFoundException;
import com.rpatino12.epam.gym.exception.TrainingNullException;
import com.rpatino12.epam.gym.model.Trainee;
import com.rpatino12.epam.gym.model.Trainer;
import com.rpatino12.epam.gym.model.Training;
import com.rpatino12.epam.gym.model.TrainingType;
import com.rpatino12.epam.gym.model.TrainingTypes;
import com.rpatino12.epam.gym.model.User;
import com.rpatino12.epam.gym.repo.TrainingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @InjectMocks
    private TrainingService trainingService;

    @Mock
    private TrainingRepository trainingRepository;
    @Mock
    private TraineeService traineeService;
    @Mock
    private TrainerService trainerService;
    @Mock
    private JmsTemplate jmsTemplate;

    private Trainee trainee;
    private Trainer trainer;
    private Training training;

    private User userTrainee;
    private User userTrainer;

    private TrainingType specialization;

    @BeforeEach
    public void setUp(){
        userTrainee = new User();
        userTrainee.setId(1L);
        userTrainee.setFirstName("traineeFirst");
        userTrainee.setLastName("traineeLast");
        userTrainee.setUsername(userTrainee.getFirstName() + "." + userTrainee.getLastName());
        userTrainee.setPassword("traineePassword");
        userTrainee.setIsActive(true);

        trainee = new Trainee();
        trainee.setTraineeId(1L);
        trainee.setAddress("1234 Main Street #1");
        trainee.setDateOfBirth(new Date(System.currentTimeMillis()));
        trainee.setUser(userTrainee);

        userTrainer = new User();
        userTrainer.setId(2L);
        userTrainer.setFirstName("trainerFirst");
        userTrainer.setLastName("trainerLast");
        userTrainer.setUsername(userTrainer.getFirstName() + "." + userTrainer.getLastName());
        userTrainer.setPassword("trainerPassword");
        userTrainer.setIsActive(true);

        specialization = new TrainingType();
        specialization.setTrainingTypeId(1L);
        specialization.setTrainingTypeName(TrainingTypes.Fitness);

        trainer = new Trainer();
        trainer.setTrainerId(1L);
        trainer.setSpecialization(specialization);
        trainer.setUser(userTrainer);

        training = new Training();
        training.setTrainingId(1L);
        training.setTrainingName("Testing training");
        training.setTrainingDuration(45.0);
        training.setTrainingDate(new Date(System.currentTimeMillis()));
        training.setTrainee(trainee);
        training.setTrainer(trainer);
    }

    @Test
    @DisplayName("save() with given training and trainee/trainer username return true")
    void save() {
        Mockito.doReturn(Optional.of(trainee)).when(traineeService).getByUsername(userTrainee.getUsername());
        Mockito.doReturn(Optional.of(trainer)).when(trainerService).getByUsername(userTrainer.getUsername());
        Mockito.doReturn(training).when(trainingRepository).save(training);
        Mockito.doNothing().when(jmsTemplate).convertAndSend(Mockito.anyString(), Mockito.any(Object.class));

        boolean isTrainingSaved = trainingService.save(training, userTrainee.getUsername(), userTrainer.getUsername());
        assertTrue(isTrainingSaved);
    }

    @Test
    @DisplayName("save() with null training throw TrainingNullException")
    void saveNull(){
        Training trainingNull = null;
        assertThrows(TrainingNullException.class,
                () -> trainingService.save(trainingNull, userTrainee.getUsername(), userTrainer.getUsername()),
                "Exception not throw as expected"
        );
    }

    @Test
    @DisplayName("No entity found will throw ResourceNotFoundException")
    void throwsResourceNotFoundException(){
        List<Training> emptyTrainingList = new ArrayList<>();
        Mockito.doReturn(emptyTrainingList).when(trainingRepository).findAll();
        assertThrows(
                ResourceNotFoundException.class,
                () -> trainingService.getAll(),
                "Exception not throw as expected"
        );
    }

    @Test
    @DisplayName("save() with given training and trainee/trainer username return false")
    void username_not_found_save() {
        Mockito.doReturn(Optional.empty()).when(traineeService).getByUsername(userTrainee.getUsername());
        Mockito.doReturn(Optional.empty()).when(trainerService).getByUsername(userTrainer.getUsername());

        boolean isTrainingSaved = trainingService.save(training, userTrainee.getUsername(), userTrainer.getUsername());
        assertFalse(isTrainingSaved);
    }

    @Test
    @DisplayName("getAll() return all trainings list")
    void getAll() {
        List<Training> trainingList = getMockTrainings(5);
        Mockito.doReturn(trainingList).when(trainingRepository).findAll();

        List<Training> trainings = trainingService.getAll();
        assertNotNull(trainings);
        assertEquals(5, trainings.size());
        assertIterableEquals(trainingList, trainings);
    }

    @Test
    @DisplayName("getByTraineeUsername() with given trainee username return trainee's trainings")
    void getByTraineeUsername() {
        List<Training> trainingList = getMockTrainings(5);
        Mockito.doReturn(Optional.of(trainingList)).when(trainingRepository).findTrainingByTraineeUserUsername(userTrainee.getUsername());

        Optional<List<Training>> trainings = trainingService.getByTraineeUsername(userTrainee.getUsername());
        assertNotNull(trainings.get());
        assertEquals(5, trainings.get().size());
        assertIterableEquals(trainingList, trainings.get());
    }

    @Test
    @DisplayName("getByTrainerUsername() with given trainer username return trainer's trainings")
    void getByTrainerUsername() {
        List<Training> trainingList = getMockTrainings(5);
        Mockito.doReturn(Optional.of(trainingList)).when(trainingRepository).findTrainingByTrainerUserUsername(userTrainer.getUsername());

        Optional<List<Training>> trainings = trainingService.getByTrainerUsername(userTrainer.getUsername());
        assertNotNull(trainings.get());
        assertEquals(5, trainings.get().size());
        assertIterableEquals(trainingList, trainings.get());
    }

    private List<Training> getMockTrainings(int numOfTrainings){
        List<Training> trainings = new ArrayList<>(numOfTrainings);
        for (int i = 0; i < numOfTrainings; i++) {
            Training training1 = new Training();
            training1.setTrainingId((long) i);
            training1.setTrainingName("Testing training " + i);
            training1.setTrainingDuration(15 + Math.random()*45);
            training1.setTrainingDate(new Date(System.currentTimeMillis()));
            training1.setTrainee(trainee);
            training1.setTrainer(trainer);

            trainings.add(training1);
        }
        return trainings;
    }
}