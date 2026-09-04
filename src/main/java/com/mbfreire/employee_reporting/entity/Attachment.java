package com.mbfreire.employee_reporting.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "attachments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    @Column(name = "original_file_name", nullable = false)
    private String originalFileName; // Nome real que o usuário enviou (ex: comprovante.pdf)

    @Column(name = "stored_file_name", nullable = false, unique = true)
    private String storedFileName; // Nome seguro gerado pelo sistema (ex: 550e8400-e29b-41d4-a716-446655440000.pdf)

    @Column(name = "content_type", nullable = false)
    private String contentType; // Tipo do arquivo (ex: image/png, application/pdf)

    @Column(name = "file_size")
    private Long fileSize; // Tamanho do arquivo em bytes

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
