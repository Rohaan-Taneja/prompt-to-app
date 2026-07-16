package com.PromptToApp.core.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsageLogs extends BaseEntity{

    @ManyToOne()
    @JoinColumn()
    private User user;

    @ManyToOne()
    @JoinColumn
    private Project project;

    private String prompt;

    private Integer tokenUsed;

    private Integer Duration_ms;
}
