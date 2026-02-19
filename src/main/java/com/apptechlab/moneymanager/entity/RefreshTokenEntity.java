package com.apptechlab.moneymanager.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "tbl_refresh_token")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    private String token;

    @OneToOne
    @JoinColumn(name = "profile_id", referencedColumnName = "id")
    private ProfileEntity profile;

    @Column(nullable = false)
    private Instant expiryDate;
}
