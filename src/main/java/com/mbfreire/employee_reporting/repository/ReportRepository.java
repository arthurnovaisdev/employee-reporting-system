package com.mbfreire.employee_reporting.repository;

import com.mbfreire.employee_reporting.entity.Report;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, UUID> {
    Optional<Report> findByProtocol(String protocol);

    boolean existsByProtocol(String protocol);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select r
            from Report r
            where r.protocol = :protocol
            """)
    Optional<Report> findByProtocolForUpdate(@Param("protocol") String protocol);

    @Override
    @EntityGraph(attributePaths = "category")
    Page<Report> findAll(Pageable pageable);
}
