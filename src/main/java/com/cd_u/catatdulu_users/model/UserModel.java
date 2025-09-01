package com.cd_u.catatdulu_users.model;

import jakarta.persistence.*;
import jdk.jfr.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String firstName;
    private String lastName;
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    private String profileImage;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDate createdAt;

    @UpdateTimestamp
    private LocalDate updatedAt;

    private Boolean isOnline;
    private String activationToken;

    @PrePersist
    public void prePersist() {
        if (this.isOnline == null) {
            isOnline = false;
        }
    }
}
