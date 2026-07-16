package com.PromptToApp.core.repository;

import com.PromptToApp.core.Entity.ProcessedStripeEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface processedStripeEventRepository extends JpaRepository<ProcessedStripeEvent , String> {


}
