package com.PromptToApp.core.Entity;

import com.PromptToApp.core.enums.PlanType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "plan")
public class Plan extends BaseEntity {

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal planPrice;


    @Column(name = "stripe_plan_id" , nullable = true , comment = "this is the product id that we have created on stripe")
    private String stripePlanId;

    @Column(nullable = true , comment = "plan price id that we creaeted/stored on stripe dashboard for this plan..  for free plan , it will be null")
    private String stripePlanPriceId;

    @Enumerated(value = EnumType.STRING)
    private PlanType planType;

    private Integer maxTokenPerDay;

    private Integer noOfDays;

    private Integer maxProjects;

    private Integer maxNoOfPreviews;

    private Boolean Active;

}
