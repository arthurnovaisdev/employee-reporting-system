package com.mbfreire.employee_reporting.repository;

import com.mbfreire.employee_reporting.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
}
