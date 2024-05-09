package com.rpatino12.epam.gym.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rpatino12.epam.gym.dto.TraineeDto;
import com.rpatino12.epam.gym.dto.UpdateUserDto;
import com.rpatino12.epam.gym.dto.UserLogin;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TraineeRestControllerTest {

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
        this.mvc.perform(get("/api/trainees").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("manya.whitcomb")))
                .andExpect(content().string(containsString("miquela.trembley")));
    }

    @Test
    void getTraineeByUsername() throws Exception {
        this.mvc.perform(get("/api/trainees/shea.mcfater")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"firstName\":\"Shea\"")))
                .andExpect(content().string(containsString("\"lastName\":\"McFater\"")));
    }

    @Test
    void whenTraineeNotFoundThen404() throws Exception {
        this.mvc.perform(get("/api/trainees/cristiano.ronaldo")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());

        this.mvc.perform(delete("/api/trainees/delete/no.trainee")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());

        TraineeDto traineeDto = new TraineeDto("trainee404", "NoTrainee", "Smith", true);

        this.mvc.perform(put("/api/trainees/update")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(traineeDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createTrainee() throws Exception {
        TraineeDto traineeDto = new TraineeDto("Elton John", "Doe", "1999-01-31", "123 Elm Street");

        this.mvc.perform(post("/api/trainees/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(traineeDto)))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("\"username\":\"elton.doe\"")));
    }

    @Test
    void whenNoNameOrLastNameThen400() throws Exception {
        TraineeDto traineeDto = new TraineeDto("", "Doe", "2001-07-21", "5th Ave, New York");

        this.mvc.perform(post("/api/trainees/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(traineeDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteTraineeByUsername() throws Exception {
        this.mvc.perform(delete("/api/trainees/delete/miquela.trembley")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void updateTrainee() throws Exception {
        TraineeDto traineeDto = new TraineeDto("shea.mcfater", "Kylian", "Mbappe", "1999-02-02", "Eiffel tower, Paris", false);

        this.mvc.perform(put("/api/trainees/update")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(traineeDto)))
                .andExpect(status().isAccepted())
                .andExpect(content().string(containsString("\"username\":\"shea.mcfater\"")))
                .andExpect(content().string(containsString("\"firstName\":\"Kylian\"")))
                .andExpect(content().string(containsString("\"isActive\":false")))
                .andExpect(content().string(containsString("\"address\":\"Eiffel tower, Paris\"")));
    }

    @Test
    void updatePassword() throws Exception {
        UpdateUserDto credentials = new UpdateUserDto("manya.whitcomb", "vbxowmkpue", "very-strong-password1234!");

        this.mvc.perform(put("/api/trainees/update-password")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(credentials)))
                .andExpect(status().isAccepted())
                .andExpect(content().string(containsString("Password updated")));
    }

    @Test
    void whenPasswordsNotMatchThen401() throws Exception {
        UpdateUserDto credentials = new UpdateUserDto("manya.whitcomb", "wrong-password", "easy-password");

        UserLogin userLogin = new UserLogin("manya.whitcomb", "wrong-password");

        this.mvc.perform(put("/api/trainees/update-password")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(credentials)))
                .andExpect(status().isUnauthorized());

        this.mvc.perform(patch("/api/trainees/activate")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userLogin)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenNoPasswordProvidedThen400() throws Exception {
        UpdateUserDto credentials = new UpdateUserDto("manya.whitcomb", "vbxowmkpue", "");

        this.mvc.perform(put("/api/trainees/update-password")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(credentials)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void updateStatus() throws Exception {
        UserLogin userLogin = new UserLogin("manya.whitcomb", "vbxowmkpue");

        this.mvc.perform(patch("/api/trainees/activate")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userLogin)))
                .andExpect(status().isAccepted())
                .andExpect(content().string(containsString("User deactivated")));
    }
}