package com.PromptToApp.core.Entity;

import com.PromptToApp.core.enums.PodPreviewStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table
public class Preview extends BaseEntity{

    @ManyToOne
    private Project project;

    private String nameSpace;

    private String podName;

    private String PreviewUrl;

    @Enumerated(value = EnumType.STRING)
    private PodPreviewStatus status;

    private LocalDateTime terminatedAt;

}
