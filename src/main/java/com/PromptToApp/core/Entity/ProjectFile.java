package com.PromptToApp.core.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table
public class ProjectFile extends BaseEntity{

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Column( comment = "the path of the file in the project , we will be placing the file in project through this path")
    private String path;

    @Column(name = "content" , comment = "we will be storing the file id stored in Min.io storage")
    private String minIoObjectKey;

    @ManyToOne
    @JoinColumn(name = "file_created_by")
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "file_last_updated_by")
    private User lastUpdatedBy;


}
