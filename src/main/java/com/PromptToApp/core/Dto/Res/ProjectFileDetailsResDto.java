package com.PromptToApp.core.Dto.Res;


import java.util.UUID;

public record ProjectFileDetailsResDto(UUID fileId , String path , String content) {
}
