package com.rpatino12.epam.gym.service;

import com.rpatino12.epam.gym.model.SecurityUser;
import com.rpatino12.epam.gym.repo.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public JpaUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Spring Security is going to end up passing the username to this loadByUsername() method
    // If we have the username in our database, it will take the User entity object and map it to a SecurityUser object that implements the UserDetails interface
    // And then will try to authenticate with that user, if there is no such username will throw UsernameNotFoundException
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository
                .findByUsername(username)
                .map(SecurityUser::new)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found: " + username));
    }
}
