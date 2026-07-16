package com.PromptToApp.core.repository;

import com.PromptToApp.core.Entity.ProjectMember;
import com.PromptToApp.core.enums.ProjectMemberRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface projectMemberRepository extends JpaRepository<ProjectMember , UUID> {

    @Query("""
                SELECT pm.projectMemberRole FROM ProjectMember pm
                WHERE pm.member.id = :userId
                AND pm.project = :projectId
""")
    Optional<ProjectMemberRole> checkUserAccessToProject(@Param("userId") UUID userId , @Param("projectId") UUID projectId);
}
