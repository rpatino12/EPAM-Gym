package com.rpatino12.epam.gym.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rpatino12.epam.gym.dto.TrainingDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TrainingRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private String token;

    @BeforeEach
    public void setUp() throws Exception {
        MvcResult result = this.mvc.perform(post("/api/auth/token")
                        .with(httpBasic("ricardo.patino", "password")))
                .andExpect(status().isOk())
                .andReturn();

        this.token = result.getResponse().getContentAsString();
    }

    @Test
    void getAll() throws Exception {
        this.mvc.perform(get("/api/trainings").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"trainingName\":\"Hard\"")))
                .andExpect(content().string(containsString("\"username\":\"roddy.patman\"")))
                .andExpect(content().string(containsString("\"username\":\"manya.whitcomb\"")))
                .andExpect(content().string(containsString("\"trainingTypeName\":\"Fitness\"")));
    }

    @Test
    void getTrainingTypes() throws Exception {
        this.mvc.perform(get("/api/trainings/training-types")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"trainingType\":\"Yoga\"")))
                .andExpect(content().string(containsString("\"trainingType\":\"Zumba\"")))
                .andExpect(content().string(containsString("\"trainingTypeId\":5")))
                .andExpect(content().string(containsString("\"trainingTypeId\":1")));
    }

    @Test
    void getByTrainee() throws Exception {
        this.mvc.perform(get("/api/trainings/trainee-username/manya.whitcomb")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"firstName\":\"Manya\"")))
                .andExpect(content().string(containsString("\"lastName\":\"Whitcomb\"")))
                .andExpect(content().string(containsString("\"trainingDate\":\"2023-09-10\"")));
    }

    @Test
    void getByTrainer() throws Exception {
        this.mvc.perform(get("/api/trainings/trainer-username/betteann.staten")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"firstName\":\"Betteann\"")))
                .andExpect(content().string(containsString("\"lastName\":\"Staten\"")))
                .andExpect(content().string(containsString("\"trainingId\":2")))
                .andExpect(content().string(containsString("\"trainingName\":\"Easy\"")));
    }

    @Test
    void whenTrainingsNotFoundThen404() throws Exception {
        this.mvc.perform(get("/api/trainings/trainee-username/shea.mcfater")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());

        this.mvc.perform(get("/api/trainings/trainer-username/ricardo.patino")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenUsernameNotFoundThen404() throws Exception {
        this.mvc.perform(get("/api/trainings/trainee-username/not.trainee")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());

        this.mvc.perform(get("/api/trainings/trainer-username/not.trainer")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void createTraining() throws Exception {
        TrainingDto trainingDto = new TrainingDto("Mr. Olympia Training", "2024-01-10", 35.2, "betteann.staten", "manya.whitcomb");

        this.mvc.perform(post("/api/trainings/save")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(trainingDto)))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("Training created")));
    }

    @Test
    void whenWrongUsernamesThen400() throws Exception {
        TrainingDto trainingDto = new TrainingDto("Goat training", "2024-01-10", 50.0, "lionel.messi", "cristiano.ronaldo");

        this.mvc.perform(post("/api/trainings/save")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(trainingDto)))
                .andExpect(status().isBadRequest());
    }
}