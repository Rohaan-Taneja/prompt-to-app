package com.PromptToApp.core.repository;

import com.PromptToApp.core.Entity.SubscriptionPlan;
import com.PromptToApp.core.enums.SubscriptionStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface userSubscriptionRepository extends JpaRepository<SubscriptionPlan , UUID> {

    Optional<SubscriptionPlan> getByUserIdAndStatus(UUID userId , SubscriptionStatus status);



    Optional<SubscriptionPlan> getByUserIdAndStatusIn(UUID userId , Set<SubscriptionStatus> status);
}
