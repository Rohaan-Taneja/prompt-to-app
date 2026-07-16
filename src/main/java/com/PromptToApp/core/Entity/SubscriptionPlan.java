package com.PromptToApp.core.Entity;

import com.PromptToApp.core.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table
public class SubscriptionPlan extends BaseEntity {

    @Column(nullable = true , unique = true)
    private String stripeCustomerId;

    @Column(nullable = true , unique = true)
    private String stripeSubscriptionId;


    @ManyToOne(fetch = FetchType.EAGER , cascade = CascadeType.ALL)
    @JoinColumn(name = "plan_type" )
    private Plan plan;

//    can be many to one ,1 user can have many plans , but active would be 1 only
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private LocalDateTime subscriptionStartDate;

    private LocalDateTime subscriptionPlanEndDate;

    private boolean cancelAtPeriodEnd;

    @Enumerated(value = EnumType.STRING)
    private SubscriptionStatus status;
}
