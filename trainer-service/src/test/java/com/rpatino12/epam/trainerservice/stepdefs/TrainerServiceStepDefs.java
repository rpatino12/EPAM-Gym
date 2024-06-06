package com.rpatino12.epam.trainerservice.stepdefs;

import com.rpatino12.epam.trainerservice.dto.WorkloadDto;
import com.rpatino12.epam.trainerservice.repo.TrainerRepository;
import com.rpatino12.epam.trainerservice.service.TrainerService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;

import java.sql.Date;
import java.time.YearMonth;

import static org.mockito.Mockito.*;

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

    @Given("a Workload for a trainer with username {string}")
    public void a_workload_for_a_trainer_with_username(String username) {
        workloadDto = new WorkloadDto();
        workloadDto.setUsername(username);
        workloadDto.setFirstName("John");
        workloadDto.setLastName("Doe");
        workloadDto.setStatus(true);
        workloadDto.setTrainingDate(Date.valueOf("2024-06-06"));
        workloadDto.setTrainingDuration(32.5);
    }

    @Given("the trainer {string} does not exist in the database")
    public void the_trainer_does_not_exist_in_the_database(String username) {
        when(trainerRepository.existsByUsername(username)).thenReturn(false);
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
}
