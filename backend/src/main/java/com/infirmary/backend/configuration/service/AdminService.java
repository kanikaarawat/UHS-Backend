package com.infirmary.backend.configuration.service;

import com.infirmary.backend.configuration.dto.ActivityLogDTO;
import com.infirmary.backend.configuration.dto.SystemStatsDTO;

import java.util.List;
import java.util.Map;

public interface AdminService {
    SystemStatsDTO getSystemStats();

    long getTotalPatients();

    long getActiveSessions();

    List<ActivityLogDTO> getRecentActivities();

    Map<String, Object> getSystemHealth();

    List<String> getCurrentAlerts();
}
