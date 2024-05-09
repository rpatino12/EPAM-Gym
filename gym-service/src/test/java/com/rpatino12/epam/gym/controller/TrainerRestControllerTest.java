package com.rpatino12.epam.gym.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rpatino12.epam.gym.dto.TrainerDto;
import com.rpatino12.epam.gym.dto.UpdateUserDto;
import com.rpatino12.epam.gym.dto.UserDto;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TrainerRestControllerTest {

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
        this.mvc.perform(get("/api/trainers").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("roddy.patman")))
                .andExpect(content().string(containsString("ricardo.patino")));
    }

    @Test
    void getTrainerByUsername() throws Exception {
        this.mvc.perform(get("/api/trainers/ricardo.patino")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"firstName\":\"Ricardo\"")))
                .andExpect(content().string(containsString("\"lastName\":\"Patino\"")))
                .andExpect(content().string(containsString("\"trainingTypeName\":\"Resistance\"")));
    }

    @Test
    void whenTrainerNotFoundThen404() throws Exception {
        this.mvc.perform(get("/api/trainers/cristiano.ronaldo")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());

        TrainerDto trainerDto = new TrainerDto("trainer404", "Jane", "Doe", 5, false);

        this.mvc.perform(put("/api/trainers/update")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(trainerDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createTrainer() throws Exception {
        TrainerDto trainerDto = new TrainerDto("Chris", "Bumstead", 1);

        this.mvc.perform(post("/api/trainers/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(trainerDto)))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("\"username\":\"chris.bumstead\"")));
    }

    @Test
    void whenNoNameLastNameOrSpecializationThen400() throws Exception {
        TrainerDto trainerDto = new TrainerDto("Homer", "", 1);
        UserDto userDto = new UserDto("Homer", "Simpson");

        this.mvc.perform(post("/api/trainers/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(trainerDto)))
                .andExpect(status().isBadRequest());

        this.mvc.perform(post("/api/trainers/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenWrongSpecializationThen400() throws Exception {
        TrainerDto trainerDto = new TrainerDto("Homer", "Simpson", 8);
        TrainerDto trainerDto1 = new TrainerDto("betteann.staten", "John", "Wick", 0, false);

        this.mvc.perform(post("/api/trainers/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(trainerDto)))
                .andExpect(status().isBadRequest());

        this.mvc.perform(put("/api/trainers/update")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(trainerDto1)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateTrainer() throws Exception {
        TrainerDto trainerDto = new TrainerDto("betteann.staten", "Ronnie", "Coleman", 5, false);

        this.mvc.perform(put("/api/trainers/update")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(trainerDto)))
                .andExpect(status().isAccepted())
                .andExpect(content().string(containsString("\"username\":\"betteann.staten\"")))
                .andExpect(content().string(containsString("\"firstName\":\"Ronnie\"")))
                .andExpect(content().string(containsString("\"isActive\":false")))
                .andExpect(content().string(containsString("\"trainingTypeName\":\"Resistance\"")));
    }

    @Test
    void updatePassword() throws Exception {
        UpdateUserDto credentials = new UpdateUserDto("betteann.staten", "npwrbgzsom", "very-strong-password1234!");

        this.mvc.perform(put("/api/trainers/update-password")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(credentials)))
                .andExpect(status().isAccepted())
                .andExpect(content().string(containsString("Password updated")));
    }

    @Test
    void whenPasswordsNotMatchThen401() throws Exception {
        UpdateUserDto credentials = new UpdateUserDto("ricardo.patino", "wrong-password", "easy-password");

        UserLogin userLogin = new UserLogin("ricardo.patino", "wrong-password");

        this.mvc.perform(put("/api/trainers/update-password")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(credentials)))
                .andExpect(status().isUnauthorized());

        this.mvc.perform(patch("/api/trainers/activate")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userLogin)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenNoPasswordProvidedThen400() throws Exception {
        UpdateUserDto credentials = new UpdateUserDto("ricardo.patino", "password", "");

        this.mvc.perform(put("/api/trainers/update-password")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(credentials)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateStatus() throws Exception {
        UserLogin userLogin = new UserLogin("betteann.staten", "npwrbgzsom");

        this.mvc.perform(patch("/api/trainers/activate")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userLogin)))
                .andExpect(status().isAccepted())
                .andExpect(content().string(containsString("User deactivated")));
    }
}