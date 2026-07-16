package com.PromptToApp.core.Entity;

import com.PromptToApp.core.enums.ProjectMemberRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table
public class ProjectMember extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "member" , nullable = false)
    private User member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project" , nullable = false)
    private Project project;

    @Enumerated( value = EnumType.STRING)
    private ProjectMemberRole projectMemberRole;

    @ManyToOne
    @JoinColumn(name = "invited_by")

    private User invitedBy;
    @CreationTimestamp
    private LocalDateTime invitedAt;

}
