package com.mbfreire.employee_reporting.repository;

import com.mbfreire.employee_reporting.entity.PasswordResetToken;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select t
            from PasswordResetToken t
            join fetch t.user
            where t.tokenHash = :tokenHash
            """)
    Optional<PasswordResetToken> findByTokenHashForUpdate(@Param("tokenHash") String token);

    @Modifying(
            flushAutomatically = true,
            clearAutomatically = true
    )
    @Query("""
            delete from PasswordResetToken t
            where t.user.id = :userId
            """)
    int deleteAllByUserId(
            @Param("userId")
            UUID userId
    );

    @Modifying(
            flushAutomatically = true,
            clearAutomatically = true
    )
    @Query("""
            delete from PasswordResetToken t
            where t.expiryDate < :now
               or t.used = true
            """)
    int deleteExpiredOrUsed(
            @Param("now")
            LocalDateTime now
    );
}
