package com.mbfreire.employee_reporting.repository;

import com.mbfreire.employee_reporting.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {
    List<Attachment> findByReportId(UUID reportId);

    Optional<Attachment> findByIdAndReportProtocol(UUID id, String protocol);

    long countByReportId(UUID reportId);

    @Query("""
            select sum(a.fileSize)
            from Attachment a
            where a.report.id = :reportId
            """)
    Long sumFileSizeByReportId(@Param("reportId") UUID reportId);
}
