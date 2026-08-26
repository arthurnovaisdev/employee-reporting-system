package com.mbfreire.employee_reporting.repository;

import com.mbfreire.employee_reporting.entity.StatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StatusHistoryRepository extends JpaRepository<StatusHistory, UUID> {
    List<StatusHistory> findByReportIdOrderByCreatedAtAsc(UUID reportId);
}
