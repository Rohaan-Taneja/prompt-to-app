package com.PromptToApp.core.repository;

import com.PromptToApp.core.Entity.ProjectFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface projectFileRepository extends JpaRepository<ProjectFile , UUID> {

    Optional<ProjectFile> findByProjectIdAndPath(UUID projectId , String FilePath);


    @Query("""
        SELECT pf FROM ProjectFile pf
        WHERE pf.project.id = :project_id
""")
    List<ProjectFile> getFileTree(@Param("project_id") UUID project_id);
}
