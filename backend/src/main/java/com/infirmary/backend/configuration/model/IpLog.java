// File: IpLog.java
package com.infirmary.backend.configuration.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ip_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IpLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ip;
    private String city;
    private String region;
    private String country;
    private String org;
    private String latitude;
    private String longitude;

    private LocalDateTime timestamp;
}
