package com.PromptToApp.core.repository;

import com.PromptToApp.core.Entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.UUID;

public interface planRepository extends JpaRepository<Plan , UUID> {

    Optional<Plan> findByStripePlanId(String stripePlanId);
}
