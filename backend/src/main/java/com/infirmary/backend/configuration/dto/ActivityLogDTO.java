package com.infirmary.backend.configuration.dto;

import com.infirmary.backend.configuration.model.ActivityLog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityLogDTO {
    private UUID id;
    private String action;
    private String performedBy;
    private LocalDateTime timestamp;

    public ActivityLogDTO(ActivityLog log) {
        this.id = log.getId();
        this.action = log.getActivityType();
        this.performedBy = log.getPerformedBy();
        this.timestamp = log.getTimestamp();
    }
}
