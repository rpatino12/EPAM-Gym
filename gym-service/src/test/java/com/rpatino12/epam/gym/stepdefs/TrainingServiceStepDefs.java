package com.rpatino12.epam.gym.stepdefs;

import com.rpatino12.epam.gym.dto.WorkloadDto;
import com.rpatino12.epam.gym.exception.ResourceNotFoundException;
import com.rpatino12.epam.gym.exception.TrainingNullException;
import com.rpatino12.epam.gym.model.*;
import com.rpatino12.epam.gym.repo.TrainingRepository;
import com.rpatino12.epam.gym.service.TraineeService;
import com.rpatino12.epam.gym.service.TrainerService;
import com.rpatino12.epam.gym.service.TrainingService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;

import java.sql.Date;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@CucumberContextConfiguration
public class TrainingServiceStepDefs {

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

    private Training training;
    private Exception exception;
    private boolean saveOperationResult;
    private String deleteOperationString;
    private List<Training> trainingList;

    /*
    * Step definitions for Save Training and send the workload feature
    * */

    @Given("a new training session for an existing trainee {string} and an existing trainer {string}")
    public void a_new_training_session_for_an_existing_trainee_and_an_existing_trainer(String traineeUsername, String trainerUsername) {
        training = new Training();
        training.setTrainingName("Testing training");
        training.setTrainingDate(Date.valueOf("2024-01-01"));
        training.setTrainingDuration(20.5);
        training.setTrainingType(new TrainingType());

        User traineeUser = new User();
        User trainerUser = new User();
        traineeUser.setUsername(traineeUsername);
        trainerUser.setUsername(trainerUsername);

        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();
        trainee.setUser(traineeUser);
        trainer.setUser(trainerUser);
        when(traineeService.getByUsername(traineeUsername)).thenReturn(Optional.of(trainee));
        when(trainerService.getByUsername(trainerUsername)).thenReturn(Optional.of(trainer));
    }

    @Given("a new training session for a non-existing trainee {string} and an existing trainer {string}")
    public void a_new_training_session_for_a_non_existing_trainee_and_an_existing_trainer(String traineeUsername, String trainerUsername) {
        training = new Training();
        // Set other fields of training as needed...

        when(traineeService.getByUsername(traineeUsername)).thenReturn(Optional.empty());
        when(trainerService.getByUsername(trainerUsername)).thenReturn(Optional.of(new Trainer()));
    }

    @Given("a new training session for an existing trainee {string} and a non-existing trainer {string}")
    public void a_new_training_session_for_an_existing_trainee_and_a_non_existing_trainer(String traineeUsername, String trainerUsername) {
        training = new Training();
        // Set other fields of training as needed...

        when(traineeService.getByUsername(traineeUsername)).thenReturn(Optional.of(new Trainee()));
        when(trainerService.getByUsername(trainerUsername)).thenReturn(Optional.empty());
    }

    @Given("a null training session")
    public void a_null_training_session() {
        training = null;
    }

    @When("I save the training session")
    public void i_save_the_training_session() {
        when(trainingRepository.save(training)).thenReturn(training);
        try {
            saveOperationResult = trainingService.save(training, "alice.smith", "bob.jones");
            exception = null;
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("the training session should be saved under the trainee {string} and the trainer {string}")
    public void the_training_session_should_be_saved_under_the_trainee_and_the_trainer(String traineeUsername, String trainerUsername) {
        verify(trainingRepository).save(argThat(training ->
                training.getTrainee().getUser().getUsername().equals(traineeUsername) &&
                        training.getTrainer().getUser().getUsername().equals(trainerUsername)
        ));
    }

    @Then("a workload summary for the trainer {string} should be sent to the JMS queue")
    public void a_workload_summary_for_the_trainer_should_be_sent_to_the_JMS_queue(String trainerUsername) {
        ArgumentCaptor<WorkloadDto> workloadDtoCaptor = ArgumentCaptor.forClass(WorkloadDto.class);
        verify(jmsTemplate).convertAndSend(anyString(), workloadDtoCaptor.capture());

        WorkloadDto actualWorkloadDto = workloadDtoCaptor.getValue();
        assertEquals(trainerUsername, actualWorkloadDto.getUsername());
    }

    @Then("the operation should return false")
    public void the_operation_should_return_false() {
        assertFalse(saveOperationResult);
    }

    @Then("an error should be thrown")
    public void an_error_should_be_thrown() {
        assertInstanceOf(TrainingNullException.class, exception);
    }

    /*
     * Step definitions for delete workloads feature
     * */

    @Given("there are multiple training sessions for trainer {string} on date {string}")
    public void there_are_multiple_training_sessions_for_trainer_on_date(String trainerUsername, String date) {
        User trainerUser = new User();
        trainerUser.setUsername(trainerUsername);

        Trainer trainer = new Trainer();
        trainer.setUser(trainerUser);

        Training training1 = new Training();
        training1.setTrainingName("Testing training");
        training1.setTrainingDate(Date.valueOf(date));
        training1.setTrainingDuration(20.5);
        training1.setTrainer(trainer);
        training1.setTrainee(new Trainee());

        List<Training> trainings = Arrays.asList(training1, training1);
        when(trainingRepository.findByTrainingDateAndTrainerUserUsername(Date.valueOf(date), trainerUsername)).thenReturn(trainings);
    }

    @Given("there are no training sessions for trainer {string} on date {string}")
    public void there_are_no_training_sessions_for_trainer_on_date(String trainerUsername, String date) {
        when(trainingRepository.findByTrainingDateAndTrainerUserUsername(Date.valueOf(date), trainerUsername)).thenReturn(Collections.emptyList());
    }

    @When("I delete the trainer's workload for date {string}")
    public void i_delete_the_trainer_s_workload_for_date(String date) {
        WorkloadDto workloadDto = new WorkloadDto();
        workloadDto.setTrainingDate(Date.valueOf(date));
        workloadDto.setUsername("bob.jones");
        try {
            deleteOperationString = trainingService.deleteTrainerWorkload(workloadDto);
            exception = null;
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("the last training session workload should be deleted for trainer {string} on date {string}")
    public void the_last_training_session_workload_should_be_deleted_for_trainer_on_date(String trainerUsername, String date) {
        ArgumentCaptor<WorkloadDto> workloadDtoCaptor = ArgumentCaptor.forClass(WorkloadDto.class);
        verify(jmsTemplate).convertAndSend(anyString(), workloadDtoCaptor.capture());

        WorkloadDto actualWorkloadDto = workloadDtoCaptor.getValue();
        assertEquals(trainerUsername, actualWorkloadDto.getUsername());
        assertEquals("Delete successful", deleteOperationString);
    }

    @Then("a ResourceNotFoundException should be thrown")
    public void a_resource_not_found_exception_should_be_thrown() {
        assertInstanceOf(ResourceNotFoundException.class, exception);
    }

    /*
     * Step definitions for get trainings feature
     * */

    @Given("there are multiple training sessions in the system")
    public void there_are_multiple_training_sessions_in_the_system() {
        List<Training> trainings = Arrays.asList(new Training(), new Training());
        when(trainingRepository.findAll()).thenReturn(trainings);
    }

    @Given("there are no training sessions in the system")
    public void there_are_no_training_sessions_in_the_system() {
        when(trainingRepository.findAll()).thenReturn(Collections.emptyList());
    }

    @Given("there are multiple training sessions for trainee {string}")
    public void there_are_multiple_training_sessions_for_trainee(String traineeUsername) {
        User user = new User();
        user.setUsername(traineeUsername);

        Trainee trainee = new Trainee();
        trainee.setUser(user);

        Training training1 = new Training();
        training1.setTrainee(trainee);

        List<Training> trainings = Arrays.asList(training1, training1);
        when(trainingRepository.findTrainingByTraineeUserUsername(traineeUsername)).thenReturn(Optional.of(trainings));
    }

    @Given("there are no training sessions for trainee {string}")
    public void there_are_no_training_sessions_for_trainee(String traineeUsername) {
        when(trainingRepository.findTrainingByTraineeUserUsername(traineeUsername)).thenReturn(Optional.empty());
    }

    @Given("there are multiple training sessions for trainer {string}")
    public void there_are_multiple_training_sessions_for_trainer(String trainerUsername) {
        User user = new User();
        user.setUsername(trainerUsername);

        Trainer trainer = new Trainer();
        trainer.setUser(user);

        Training training1 = new Training();
        training1.setTrainer(trainer);

        List<Training> trainings = Arrays.asList(training1, training1);
        when(trainingRepository.findTrainingByTrainerUserUsername(trainerUsername)).thenReturn(Optional.of(trainings));
    }

    @Given("there are no training sessions for trainer {string}")
    public void there_are_no_training_sessions_for_trainer(String trainerUsername) {
        when(trainingRepository.findTrainingByTrainerUserUsername(trainerUsername)).thenReturn(Optional.empty());
    }

    @When("I retrieve all training sessions")
    public void i_retrieve_all_training_sessions() {
        try {
            trainingList = trainingService.getAll();
            exception = null;
        } catch (Exception e) {
            exception = e;
        }
    }

    @When("I retrieve all training sessions for trainee {string}")
    public void i_retrieve_all_training_sessions_for_trainee(String traineeUsername) {
        try {
            trainingList = trainingService.getByTraineeUsername(traineeUsername).orElse(Collections.emptyList());
            exception = null;
        } catch (Exception e) {
            exception = e;
        }
    }

    @When("I retrieve all training sessions for trainer {string}")
    public void i_retrieve_all_training_sessions_for_trainer(String trainerUsername) {
        try {
            trainingList = trainingService.getByTrainerUsername(trainerUsername).orElse(Collections.emptyList());
            exception = null;
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("I should receive a list of all training sessions")
    public void i_should_receive_a_list_of_all_training_sessions() {
        assertFalse(trainingList.isEmpty());
    }

    @Then("I should receive a list of all training sessions for trainee {string}")
    public void i_should_receive_a_list_of_all_training_sessions_for_trainee(String traineeUsername) {
        assertFalse(trainingList.isEmpty());
        assertTrue(trainingList.stream().allMatch(training1 ->
                training1.getTrainee().getUser().getUsername().equals(traineeUsername)));
    }

    @Then("I should receive a list of all training sessions for trainer {string}")
    public void i_should_receive_a_list_of_all_training_sessions_for_trainer(String trainerUsername) {
        assertFalse(trainingList.isEmpty());
        assertTrue(trainingList.stream().allMatch(training1 ->
                training1.getTrainer().getUser().getUsername().equals(trainerUsername)));
    }

    @Then("I should receive an empty list")
    public void i_should_receive_an_empty_list() {
        assertTrue(trainingList.isEmpty());
    }
}
