package com.rpatino12.epam.gym.controller;

import com.rpatino12.epam.gym.dto.TraineeResponse;
import com.rpatino12.epam.gym.dto.UpdateUserDto;
import com.rpatino12.epam.gym.dto.TraineeDto;
import com.rpatino12.epam.gym.dto.UserLogin;
import com.rpatino12.epam.gym.model.Trainee;
import com.rpatino12.epam.gym.model.User;
import com.rpatino12.epam.gym.service.TraineeService;
import com.rpatino12.epam.gym.util.DateUtils;
import com.rpatino12.epam.gym.util.TraineeMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.util.List;

@RestController
@RequestMapping("/api/trainees")
@Tag(name = "Trainee Controller", description = "Operations for creating, updating, retrieving and deleting trainees")
@Slf4j
public class TraineeRestController {
    private final TraineeService traineeService;
    private final DateUtils dateUtils;
    private final TraineeMapper traineeMapper;

    public TraineeRestController(TraineeService traineeService, DateUtils dateUtils, TraineeMapper traineeMapper) {
        this.traineeService = traineeService;
        this.dateUtils = dateUtils;
        this.traineeMapper = traineeMapper;
    }

    @GetMapping
    @Operation(summary = "View all trainees")
    public ResponseEntity<List<TraineeResponse>> getAll(){
        log.info("Received GET request to /api/trainees");

        List<Trainee> trainees = traineeService.getAll();
        List<TraineeResponse> traineeResponses = trainees.stream()
                .map(traineeMapper::mapToDTO)
                .toList();

        return new ResponseEntity<>(traineeResponses, HttpStatus.OK);
    }

    @GetMapping("/{username}")
    @Operation(summary = "Retrieve specific trainee with the supplied trainee username")
    public ResponseEntity<TraineeResponse> getTraineeByUsername(@PathVariable("username") String username){
        log.info("Received GET request to /api/trainees/{}", username);

        return traineeService.getByUsername(username)
                .map(traineeMapper::mapToDTO)
                .map(trainee -> new ResponseEntity<>(trainee, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/save")
    @Operation(summary = "Create a new trainee")
    public ResponseEntity<UserLogin> createTrainee(@Valid @RequestBody TraineeDto trainee){
        log.info("Received POST request to /api/trainees/save");

        User newUser = new User();
        newUser.setFirstName(trainee.getFirstName());
        newUser.setLastName(trainee.getLastName());

        Trainee newTrainee = new Trainee();
        newTrainee.setUser(newUser);
        Date birthdate = new Date(dateUtils.createDateFromDateString(trainee.getBirthdate()).getTime());
        newTrainee.setDateOfBirth(birthdate);
        if (trainee.getAddress() != null){
            newTrainee.setAddress(trainee.getAddress());
        }

        return new ResponseEntity<>(traineeService.save(newTrainee), HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{username}")
    @Operation(summary = "Delete specific trainee with the supplied trainee username")
    public ResponseEntity deleteTraineeByUsername(@PathVariable(name = "username") String username){
        log.info("Received DELETE request to /api/trainees/delete/{}", username);

        if (traineeService.deleteByUsername(username)){
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update")
    @Operation(summary = "Update trainee information")
    public ResponseEntity<Trainee> updateTrainee(@Valid @RequestBody TraineeDto trainee){
        log.info("Received PUT request to /api/trainees/update");

        if (traineeService.getByUsername(trainee.getUsername()).isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        User updatedUser = new User();
        updatedUser.setFirstName(trainee.getFirstName());
        updatedUser.setLastName(trainee.getLastName());
        updatedUser.setIsActive(trainee.isActive());

        Trainee updatedTrainee = new Trainee();
        updatedTrainee.setUser(updatedUser);

        if (trainee.getBirthdate() != null){
            Date birthdate = new Date(dateUtils.createDateFromDateString(trainee.getBirthdate()).getTime());
            updatedTrainee.setDateOfBirth(birthdate);
        }
        if (trainee.getAddress() != null){
            updatedTrainee.setAddress(trainee.getAddress());
        }

        return new ResponseEntity<>(traineeService.update(updatedTrainee, trainee.getUsername()), HttpStatus.ACCEPTED);
    }

    @PutMapping("/update-password")
    @Operation(summary = "Update trainee password")
    public ResponseEntity<String> updatePassword(@RequestBody UpdateUserDto credentials){
        log.info("Received PUT request to /api/trainees/update-password");

        String updateStatus = traineeService.updatePassword(
                credentials.getUsername(),
                credentials.getPassword(),
                credentials.getNewPassword()
        );
        if (updateStatus.equals("Password updated")){
            return new ResponseEntity<>(updateStatus, HttpStatus.ACCEPTED);
        } else if (updateStatus.equals("Wrong username or password")) {
            return new ResponseEntity<>(updateStatus, HttpStatus.UNAUTHORIZED);
        } else {
            return new ResponseEntity<>(updateStatus, HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/activate")
    @Operation(summary = "Activate/Deactivate trainee")
    public ResponseEntity<String> updateStatus(@RequestBody UserLogin userLogin) {
        log.info("Received PATCH request to /api/trainees/activate");

        String activate = traineeService.updateActiveStatus(userLogin.getUsername(), userLogin.getPassword());
        if (activate.equals("Wrong username or password")){
            return new ResponseEntity<>(activate, HttpStatus.UNAUTHORIZED);
        } else {
            return new ResponseEntity<>(activate, HttpStatus.ACCEPTED);
        }
    }
}
