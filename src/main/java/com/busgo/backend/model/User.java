package com.busgo.backend.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String phone;

    private String avatarUrl;
    private String nextOfKinPhone;
    private String nextOfKinName;
    private String nin;
    private String dateOfBirth;
    private String gender;
    
    @Builder.Default
    private Boolean kycComplete = false;

    @Column(nullable = false)
    private String role; // "USER" or "ADMIN"

    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }
}
