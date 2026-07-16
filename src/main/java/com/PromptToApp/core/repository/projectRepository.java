package com.PromptToApp.core.repository;

import com.PromptToApp.core.Entity.Project;
import com.PromptToApp.core.enums.ProjectMemberRole;
import jakarta.validation.Valid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface projectRepository extends JpaRepository<Project, UUID> {



//    getting the details of the project if this user is a project member
//    getting project with only id , then anyuser can view project
//    so adding project + user id , so if user is member then only prject will be returned
    @Query("""
                SELECT p FROM Project p
                WHERE p.deletedAt IS NULL
                AND p.id = :projectId
                AND EXISTS (
                    SELECT 1 FROM ProjectMember pm
                    WHERE pm.member.id = :user_id
                    AND pm.project.id = p.id
                )
            """)
    Optional<Project> getProjectAccessibleByUSer(@Param("projectId") UUID projectId , @Param("userId") UUID UserId);


//    return all the projects where this user id  is projectMember
    @Query("""
                SELECT p FROM Project p
                WHERE p.deletedAt IS NULL
                AND EXISTS (
                    SELECT 1 FROM ProjectMember pm
                    WHERE pm.member.id = :user_id
                    AND pm.project.id = p.id
                )
                ORDER BY p.updated_at DESC
            """)
    List<Project> getUserAllAccessibleProjects(@Param("user_id") UUID user_id);




}
