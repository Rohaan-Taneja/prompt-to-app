package com.PromptToApp.core.repository;

import com.PromptToApp.core.Entity.UsageLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface usageLogsRepository extends JpaRepository<UsageLogs, UUID> {


    @Query("""
        SELECT COALESCE(SUM(UL.tokenUsed) , 0) FROM UsageLogs UL
        WHERE UL.user.id = :user_id
        AND FUNCTION('DATE', UL.created_at) = CURRENT_DATE
    
""")
    Integer getTokenUsedByUserToday(@Param("user_id") UUID userId);
}
