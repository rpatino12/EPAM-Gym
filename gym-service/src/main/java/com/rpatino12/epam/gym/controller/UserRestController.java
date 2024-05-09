package com.rpatino12.epam.gym.controller;

import com.rpatino12.epam.gym.dto.UserLogin;
import com.rpatino12.epam.gym.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Controller", description = "Operations for users login")
@Slf4j
public class UserRestController {
    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    @Operation(summary = "User login")
    public ResponseEntity login(@RequestBody UserLogin userLogin) {
        log.info("Received GET request to /api/users/login");

        if (userService.authenticate(userLogin.getUsername(), userLogin.getPassword())){
            return new ResponseEntity<>("Welcome", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Wrong username or password", HttpStatus.UNAUTHORIZED);
        }
    }
}
