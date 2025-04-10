// File: IpLogRequest.java
package com.infirmary.backend.configuration.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IpLogRequestDTO {
    private String ip;
    private String city;
    private String region;
    private String country;
    private String org;
    private String latitude;
    private String longitude;
}
