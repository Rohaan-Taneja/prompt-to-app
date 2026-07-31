package com.PromptToApp.core.repository;

import com.PromptToApp.core.Entity.ProjectFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface projectFileRepository extends JpaRepository<ProjectFile , UUID> {

    Optional<ProjectFile> findByProjectIdAndPath(UUID projectId , String FilePath);
}
