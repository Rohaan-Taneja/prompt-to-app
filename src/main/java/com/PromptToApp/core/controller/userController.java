package com.PromptToApp.core.controller;

import com.PromptToApp.core.Dto.Res.UserProfileResponseDto;
import com.PromptToApp.core.service.userService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("user/me")
public class userController {

    private final userService user_service;



    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponseDto> getUserProfile(){

        UUID user_id = UUID.randomUUID();

        return ResponseEntity.ok(user_service.getUserProfile(user_id));


    }
}
