package com.PromptToApp.core.Dto.Req;

public record aiResponseObject(boolean ai_assistant_chat , boolean file_change , String file_path , String file_change_content) {
}
