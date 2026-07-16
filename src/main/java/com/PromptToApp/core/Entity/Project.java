package com.PromptToApp.core.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.repository.cdi.Eager;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table
public class Project extends BaseEntity {

    private String name;

    private String description;

    @ManyToOne( fetch = FetchType.LAZY)
    @JoinColumn(name = "project_owner")
    private User owner;

    @OneToMany(fetch = FetchType.LAZY , mappedBy = "project")
    private List<ProjectMember> projectMembers;


    @OneToMany(fetch = FetchType.LAZY , mappedBy = "project")
    private List<ProjectFile> projectFiles;

    @Column(nullable = true)
    private LocalDateTime deletedAt;
}
