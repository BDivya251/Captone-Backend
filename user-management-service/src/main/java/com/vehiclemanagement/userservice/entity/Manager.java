package com.vehiclemanagement.userservice.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Entity
@Table(name = "managers")
@Getter @Setter
public class Manager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long managerId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String department;
    private String specialization;

    private Boolean isAvailable = true;

    private LocalDateTime createdAt = LocalDateTime.now();
}
