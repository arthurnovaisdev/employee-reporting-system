package com.mbfreire.employee_reporting.service;

import com.mbfreire.employee_reporting.dto.request.ReportRequestDTO;
import com.mbfreire.employee_reporting.dto.response.ProtocolResponseDTO;
import com.mbfreire.employee_reporting.dto.response.ReportResponseDTO;
import com.mbfreire.employee_reporting.entity.Category;
import com.mbfreire.employee_reporting.entity.Report;
import com.mbfreire.employee_reporting.exception.ResourceNotFoundException;
import com.mbfreire.employee_reporting.repository.CategoryRepository;
import com.mbfreire.employee_reporting.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final CategoryRepository categoryRepository;

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
}
