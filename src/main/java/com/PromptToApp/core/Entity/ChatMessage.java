package com.PromptToApp.core.Entity;

import com.PromptToApp.core.enums.ChatBy;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


//need to undesratdn how chats are stored
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table
public class ChatMessage extends BaseEntity {

    @ManyToOne
    @JoinColumn()
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn()
    private User user;

    private String content;

    private String tokenConsumed;

    @Enumerated(EnumType.STRING)
    private ChatBy chatBy;

//    enum for chat written by whom , need to understand that

}
