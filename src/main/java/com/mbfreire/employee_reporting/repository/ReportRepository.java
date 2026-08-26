package com.mbfreire.employee_reporting.repository;

import com.mbfreire.employee_reporting.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, UUID> {
    Optional<Report> findByProtocol(String protocol);
}
