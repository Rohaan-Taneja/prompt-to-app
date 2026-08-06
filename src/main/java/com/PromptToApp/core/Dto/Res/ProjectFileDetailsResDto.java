package com.PromptToApp.core.Dto.Res;


import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class ProjectFileDetailsResDto {
    private UUID id;
    private String path;
    private String content;
}
