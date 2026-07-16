package com.PromptToApp.core.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "processed_stripe_event" ,

        indexes = {
        @Index(name = "idx_event_id" , columnList = "event_id")
        }
)
public class ProcessedStripeEvent {

    @Id
    @Column(name = "event_id")
    private String eventId;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
