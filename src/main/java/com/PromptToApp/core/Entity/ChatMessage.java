package com.PromptToApp.core.Entity;

import com.PromptToApp.core.enums.ChatBy;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


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
    @JoinColumn(name = "project_member")
    private ProjectMember projectMember;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Integer tokenConsumed;

    @Enumerated(EnumType.STRING)
    private ChatBy chatBy;
}
