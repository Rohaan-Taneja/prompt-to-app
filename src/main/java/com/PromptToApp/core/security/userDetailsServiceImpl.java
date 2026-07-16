package com.PromptToApp.core.security;

import com.PromptToApp.core.CustomExceptionHandling.ResourceNotFoundException;
import com.PromptToApp.core.repository.userRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class userDetailsServiceImpl implements UserDetailsService {


    private final userRepository user_repo;

//    spring security has username keyword to find user
//    but in our system use will login via email and password , so for us username is email only
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return user_repo.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException("user with this email not found"));
    }
}
