package com.PromptToApp.core.service;

import com.PromptToApp.core.Dto.Req.SignUpReqDto;
import com.PromptToApp.core.Dto.Req.loginReqDto;
import com.PromptToApp.core.Dto.Res.userAuthResponseDto;

public interface authService {

    userAuthResponseDto signUp(SignUpReqDto userData);

    userAuthResponseDto login(loginReqDto userData);
}
