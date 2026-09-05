package com.mbfreire.employee_reporting.service;

import com.mbfreire.employee_reporting.dto.request.ReportRequestDTO;
import com.mbfreire.employee_reporting.dto.request.ReportStatusUpdateRequestDTO;
import com.mbfreire.employee_reporting.dto.response.ProtocolResponseDTO;
import com.mbfreire.employee_reporting.dto.response.ReportResponseDTO;
import com.mbfreire.employee_reporting.entity.*;
import com.mbfreire.employee_reporting.enums.ReportStatus;
import com.mbfreire.employee_reporting.exception.BusinessRuleException;
import com.mbfreire.employee_reporting.exception.ResourceNotFoundException;
import com.mbfreire.employee_reporting.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final CategoryRepository categoryRepository;
    private final StatusHistoryRepository statusHistoryRepository;
    private final AuditLogRepository auditLogRepository;
    private final FileStorageService fileStorageService;
    private final AttachmentRepository attachmentRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final String CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    @Transactional
    public ProtocolResponseDTO register(ReportRequestDTO dto) {
        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));

        String trackingCode = generateTrackingCode();

        Report report = Report.builder()
                .protocol(generateProtocol())
                .accessCodeHash(passwordEncoder.encode(trackingCode))
                .category(category)
                .description(dto.description())
                .incidentDate(dto.incidentDate())
                .incidentLocation(dto.incidentLocation())
                .build();

        reportRepository.save(report);

        return new ProtocolResponseDTO(report.getProtocol(), trackingCode);
    }

    public ReportResponseDTO consult(String protocol, String code) {
        Report report = reportRepository.findByProtocol(protocol)
                .orElseThrow(() -> new ResourceNotFoundException("Protocolo não encontrado."));

        if (!passwordEncoder.matches(code, report.getAccessCodeHash())) {
            throw new ResourceNotFoundException("Protocolo ou código de acesso inválido.");
        }

        return new ReportResponseDTO(
                report.getProtocol(),
                report.getCategory().getName(),
                report.getDescription(),
                report.getStatus(),
                report.getCreatedAt()
        );
    }

    public Page<ReportResponseDTO> findAll(Pageable pageable) {
        return reportRepository.findAll(pageable)
                .map(report -> new ReportResponseDTO(
                        report.getProtocol(),
                        report.getCategory().getName(),
                        report.getDescription(),
                        report.getStatus(),
                        report.getCreatedAt()
                ));
    }

    @Transactional
    public ReportResponseDTO updateStatus(String protocol, ReportStatusUpdateRequestDTO dto, User loggedInAdmin) {
        Report report = reportRepository.findByProtocol(protocol)
                .orElseThrow(() -> new ResourceNotFoundException("Denúncia não encontrada com o protocolo: " + protocol));

        ReportStatus oldStatus = report.getStatus();
        ReportStatus newStatus = dto.newStatus();

        if (oldStatus == newStatus) {
            return new ReportResponseDTO(
                    report.getProtocol(),
                    report.getCategory().getName(),
                    report.getDescription(),
                    report.getStatus(),
                    report.getCreatedAt()
            );
        }

        report.setStatus(newStatus);
        reportRepository.save(report);

        StatusHistory history = StatusHistory.builder()
                .report(report)
                .status(newStatus)
                .observation(dto.note())
                .build();
        statusHistoryRepository.save(history);

        AuditLog audit = AuditLog.builder()
                .action("UPDATE_STATUS: " + oldStatus + " -> " + newStatus)
                .report(report)
                .adminUser(loggedInAdmin)
                .build();
        auditLogRepository.save(audit);

        return new ReportResponseDTO(
                report.getProtocol(),
                report.getCategory().getName(),
                report.getDescription(),
                report.getStatus(),
                report.getCreatedAt()
        );
    }

    private String generateProtocol() {
        int year = java.time.Year.now().getValue();
        String number = String.valueOf((int) (Math.random() * 9000000) + 1000000);
        return "DEN-" + year + "-" + number;
    }

    private String generateTrackingCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    @Transactional
    public void uploadAttachments(String protocol, String trackingCode, List<MultipartFile> files) {
        Report report = reportRepository.findByProtocol(protocol)
                .orElseThrow(() -> new ResourceNotFoundException("Denúncia não encontrada com o protocolo: " + protocol));

        if (!passwordEncoder.matches(trackingCode, report.getAccessCodeHash())) {
            throw new BusinessRuleException("Código de rastreio inválido para este protocolo.");
        }

        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("Nenhum arquivo foi enviado.");
        }

        for (MultipartFile file : files) {
            String storedFileName = fileStorageService.storeFile(file);

            Attachment attachment = Attachment.builder()
                    .report(report)
                    .originalFileName(file.getOriginalFilename())
                    .storedFileName(storedFileName)
                    .contentType(file.getContentType())
                    .fileSize(file.getSize())
                    .build();

            attachmentRepository.save(attachment);
        }
    }
}
