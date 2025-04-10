// File: IpLogRepository.java
package com.infirmary.backend.configuration.repository;

import com.infirmary.backend.configuration.model.IpLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IpLogRepository extends JpaRepository<IpLog, Long> {
}
