package com.PromptToApp.core.service;

import com.PromptToApp.core.Dto.Res.UserProfileResponseDto;

import java.util.UUID;

public interface userService {


    UserProfileResponseDto getUserProfile(UUID user_id);



}
