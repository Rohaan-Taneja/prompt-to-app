package com.PromptToApp.core.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "app_user",
        indexes = {
                @Index(name = "idx_user_email", columnList = "email")
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity implements UserDetails {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false , unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
    private UUID jti;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

//    this will be called by dao authProvider during authentication time
    @Override
    public String getUsername() {
        return this.getEmail();
    }
}
