package com.rpatino12.epam.trainerservice.stepdefs;

import com.rpatino12.epam.trainerservice.dto.WorkloadDto;
import com.rpatino12.epam.trainerservice.model.Trainer;
import com.rpatino12.epam.trainerservice.repo.TrainerRepository;
import com.rpatino12.epam.trainerservice.service.TrainerService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;

import java.sql.Date;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@CucumberContextConfiguration
public class TrainerServiceStepDefs {

    @InjectMocks
    private TrainerService trainerService;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private JmsTemplate jmsTemplate;

    private WorkloadDto workloadDto;
    private Trainer trainer;

    @Given("a Workload for a trainer with username {string}")
    public void a_workload_for_a_trainer_with_username(String username) {
        workloadDto = new WorkloadDto();
        workloadDto.setUsername(username);
        workloadDto.setFirstName(username.split("\\.")[0]);
        workloadDto.setLastName(username.split("\\.")[1]);
        workloadDto.setStatus(true);
        workloadDto.setTrainingDate(Date.valueOf("2024-06-06"));
        workloadDto.setTrainingDuration(32.5);
    }

    @Given("a request to delete the workload of the last training session of trainer {string}")
    public void a_request_to_delete_the_workload_of_the_last_training_session_of_trainer(String username) {
        workloadDto = new WorkloadDto();
        workloadDto.setUsername(username);
        workloadDto.setFirstName(username.split("\\.")[0]);
        workloadDto.setLastName(username.split("\\.")[1]);
        workloadDto.setStatus(true);
        workloadDto.setTrainingDate(Date.valueOf("2024-03-08"));
        workloadDto.setTrainingDuration(32.5);
        workloadDto.setActionType("DELETE");
    }

    @Given("the trainer {string} does not exist in the database")
    public void the_trainer_does_not_exist_in_the_database(String username) {
        when(trainerRepository.existsByUsername(username)).thenReturn(false);
    }

    @Given("the trainer {string} exists in the database")
    public void the_trainer_exists_in_the_database(String username) {
        when(trainerRepository.existsByUsername(username)).thenReturn(true);

        trainer = new Trainer();
        trainer.setUsername(workloadDto.getUsername());
        trainer.setFirstName(workloadDto.getFirstName());
        trainer.setLastName(workloadDto.getLastName());
        trainer.setStatus(workloadDto.isStatus());
        trainer.setMonthlySummary(new HashMap<>());

        when(trainerRepository.findByUsername(username)).thenReturn(Optional.of(trainer));
    }

    @When("I save the workload")
    public void i_save_the_workload() {
        trainerService.saveTrainer(workloadDto);
    }

    @Then("a new trainer {string} should be created in the system")
    public void a_new_trainer_should_be_created_in_the_system(String username) {
        verify(trainerRepository).save(argThat(trainer -> trainer.getUsername().equals(username)));
    }

    @Then("the workload should be saved under the trainer {string}")
    public void the_workload_should_be_saved_under_the_trainer(String username) {
        verify(trainerRepository).save(argThat(trainer -> trainer.getUsername().equals(username)));
    }

    @Then("the workload should be updated under the trainer {string}")
    public void the_workload_should_be_updated_under_the_trainer(String username) {
        verify(trainerRepository).save(argThat(trainer -> trainer.getUsername().equals(username)));
    }

    @Then("the monthly summary under the trainer {string} should be updated")
    public void the_monthly_summary_under_the_trainer_should_be_updated(String username) {
        verify(trainerRepository).save(argThat(trainer -> trainer.getUsername().equals(username) &&
                trainer.getMonthlySummary().get(YearMonth.of(2024, 6).toString()).equals(32.5)));
        assertEquals(32.5,  trainer.getMonthlySummary().get(YearMonth.of(2024, 6).toString()));
    }

    @Then("the monthly summary under the trainer {string} should be reduced by the last training session duration")
    public void the_monthly_summary_under_the_trainer_should_be_reduced_by_the_last_training_session_duration(String username) {
        verify(trainerRepository).save(trainer);
        assertEquals(0.0,  trainer.getMonthlySummary().get(YearMonth.of(2024, 3).toString()));
    }
}
