package com.apptechlab.moneymanager.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_app_config")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String platform;

    private String minVersion;
    private String storeUrl;
}
