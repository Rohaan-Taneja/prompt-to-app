package com.PromptToApp.core.Entity;

import com.PromptToApp.core.enums.PaymentStatus;
import com.PromptToApp.core.enums.PlanType;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table()
public class Payment extends BaseEntity{

    private Integer amount;

    private String stripePaymentId;

    @Column(nullable = false)
    private UUID userId;

    @Enumerated(value = EnumType.STRING)
    private PlanType planType;

    @Enumerated( value = EnumType.STRING)
    private PaymentStatus paymentStatus;
}
