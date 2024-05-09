package com.rpatino12.epam.gym.service;

import com.rpatino12.epam.gym.dto.UserLogin;
import com.rpatino12.epam.gym.exception.ResourceNotFoundException;
import com.rpatino12.epam.gym.exception.TraineeNullException;
import com.rpatino12.epam.gym.model.User;
import com.rpatino12.epam.gym.repo.TraineeRepository;
import com.rpatino12.epam.gym.model.Trainee;
import jakarta.annotation.PostConstruct;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TraineeService {

    private final TraineeRepository traineeRepository;
    private final UserService userService;

    public TraineeService(TraineeRepository traineeRepository, UserService userService) {
        this.traineeRepository = traineeRepository;
        this.userService = userService;
    }

    // Trainee Service class should support possibility to create/update/delete/select Trainee profile.
    @Transactional
    public UserLogin save(Trainee newTrainee){
        if (null == newTrainee){
            log.error("Cannot save a null or empty entity");
            throw new TraineeNullException("Trainee cannot be null");
        }
        newTrainee.setUser(userService.registerUser(newTrainee.getUser()));
        Trainee trainee = traineeRepository.save(newTrainee);
        log.info("Creating trainee: " + trainee);

        return new UserLogin(trainee.getUser().getUsername(), trainee.getUser().getPassword());
    }

    @Transactional
    public Trainee update(Trainee updatedTrainee, String username) {
        if (null == updatedTrainee){
            log.error("Cannot update trainee");
            throw new TraineeNullException("Trainee cannot be null");
        }
        User updatedUser = userService.updateUser(updatedTrainee.getUser(), username);
        log.info("""
                        Updating trainee {}:\s
                        First Name: {}\s
                        Last Name: {}\s
                        Birthdate: {}
                        Address: {}\s
                        Is Active: {}""",
                username,
                updatedUser.getFirstName(),
                updatedUser.getLastName(),
                updatedTrainee.getDateOfBirth()==null?"-":updatedTrainee.getDateOfBirth(),
                updatedTrainee.getAddress()==null?"-":updatedTrainee.getAddress(),
                updatedUser.getIsActive());

        if (traineeRepository.findTraineeByUserUsername(username).isEmpty()){
            log.error("The entity to be updated does not exist");
            throw new ResourceNotFoundException("Trainee", "username", username);
        }
        Trainee trainee = traineeRepository.findTraineeByUserUsername(username).get();

        if (updatedTrainee.getDateOfBirth() != null){
            trainee.setDateOfBirth(updatedTrainee.getDateOfBirth());
        }
        if (updatedTrainee.getAddress() != null){
            trainee.setAddress(updatedTrainee.getAddress());
        }
        trainee.setUser(updatedUser);

        return traineeRepository.save(trainee);
    }

    @Transactional
    public boolean deleteByUsername(String username){
        boolean isDeleteSuccessful = false;
        log.info("Deleting trainee " + username);
        Optional<Trainee> optionalTrainee = this.getByUsername(username);
        if (optionalTrainee.isPresent()){
            traineeRepository.deleteByUserUsername(username);
            isDeleteSuccessful = true;
        }
        log.info(isDeleteSuccessful?"Delete successful":"Couldn't delete trainee");
        return isDeleteSuccessful;
    }

    @Transactional(readOnly = true)
    public List<Trainee> getAll(){
        log.info("Getting all trainees");
        List<Trainee> trainees = traineeRepository.findAll();
        if (trainees.isEmpty()){
            log.error("There are no trainees registered");
            throw new ResourceNotFoundException("Trainee");
        }
        return trainees;
    }

    @Transactional(readOnly = true)
    public Optional<Trainee> getByUsername(String username){
        log.info("Searching trainee: " + username);
        return traineeRepository.findTraineeByUserUsername(username);
    }

    @Transactional
    public String updatePassword(String username, String oldPassword, String newPassword){
        log.info("Updating trainee password");
        Trainee trainee = this.getByUsername(username).orElse(new Trainee());
        String result = "";
        if (null==trainee.getUser() || !oldPassword.equals(trainee.getUser().getPassword())){
            result = "Wrong username or password";
            log.error(result);
        } else {
            if (oldPassword.equals(newPassword)) {
                result = "New password cannot be the same as old password";
                log.error(result);
            } else if (newPassword.isEmpty()) {
                result = "New password cannot be empty, please enter new password";
                log.error(result);
            } else {
                trainee.setUser(userService.updateUserPassword(newPassword, trainee.getUser().getId()));
                result = "Password updated";
                log.info(result);
            }
        }
        return result;
    }

    @Transactional
    public String updateActiveStatus(String username, String password){
        Trainee trainee = this.getByUsername(username).orElse(new Trainee());
        String result = "";
        if (null==trainee.getUser() || !password.equals(trainee.getUser().getPassword())){
            result = "Wrong username or password";
            log.error(result);
        } else {
            if (trainee.getUser().getIsActive()){
                trainee.setUser(userService.updateStatus(false, trainee.getUser().getId()));
                result = "User deactivated";
                log.info(result);
            } else {
                trainee.setUser(userService.updateStatus(true, trainee.getUser().getId()));
                result = "User activated";
                log.info(result);
            }
        }
        return result;
    }

    @PostConstruct
    public void init(){
        log.info("Starting TraineeService");
    }
}
