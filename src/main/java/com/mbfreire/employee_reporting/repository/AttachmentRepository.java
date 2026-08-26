package com.mbfreire.employee_reporting.repository;

import com.mbfreire.employee_reporting.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {
    List<Attachment> findByReportId(UUID reportId);
}
