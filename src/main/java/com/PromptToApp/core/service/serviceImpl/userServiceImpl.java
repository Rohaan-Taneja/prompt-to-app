package com.PromptToApp.core.service.serviceImpl;

import com.PromptToApp.core.Dto.Res.UserProfileResponseDto;
import com.PromptToApp.core.service.userService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class userServiceImpl implements userService {


    public UserProfileResponseDto getUserProfile(UUID user_id) {
        return null;
    }
}
