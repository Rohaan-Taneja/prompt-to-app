package com.PromptToApp.core.controller;

import com.PromptToApp.core.service.fileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("admin")
public class adminController {

    private final fileService file_service;


    @GetMapping("upload-template")
    public ResponseEntity<Boolean> addReactTemplateToMinio() {

        UUID userId = UUID.randomUUID();

        return ResponseEntity.ok(file_service.copyReactTemplateToMinio());

    }

}
