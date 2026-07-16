package com.PromptToApp.core.controller;

import com.PromptToApp.core.Dto.Req.SignUpReqDto;
import com.PromptToApp.core.Dto.Req.loginReqDto;
import com.PromptToApp.core.Dto.Res.userAuthResponseDto;
import com.PromptToApp.core.service.authService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Slf4j
public class authController {


    private final authService auth_service;

    @PostMapping("/sign-up")
    public ResponseEntity<userAuthResponseDto> signUp( @Valid @RequestBody() SignUpReqDto userData){

        log.info("controller reached");
        return ResponseEntity.ok(auth_service.signUp(userData));



    }

    @PostMapping("/login")
    public ResponseEntity<userAuthResponseDto> login(@Valid @RequestBody() loginReqDto userData){

        log.info("login controller reached");
        return ResponseEntity.ok(auth_service.login(userData));
    }
}
