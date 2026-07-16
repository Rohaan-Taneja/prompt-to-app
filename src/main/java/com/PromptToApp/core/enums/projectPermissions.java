package com.PromptToApp.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.security.Permission;

@RequiredArgsConstructor
@Getter
public enum projectPermissions {
    VIEW("PROJECT:VIEW"),
    EDIT("PROJECT:VIEW"),
    DELETE("PROJECT:VIEW"),
    MANAGER_MEMBERS("PROJECT:VIEW");


    private final String permission;
}
