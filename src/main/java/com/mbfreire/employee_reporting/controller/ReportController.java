package com.mbfreire.employee_reporting.controller;

import com.mbfreire.employee_reporting.dto.request.ReportRequestDTO;
import com.mbfreire.employee_reporting.dto.request.ReportStatusUpdateRequestDTO;
import com.mbfreire.employee_reporting.dto.response.ProtocolResponseDTO;
import com.mbfreire.employee_reporting.dto.response.ReportResponseDTO;
import com.mbfreire.employee_reporting.enums.ReportStatus;
import com.mbfreire.employee_reporting.security.UserDetailsImpl;
import com.mbfreire.employee_reporting.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<ProtocolResponseDTO> register(@Valid @RequestBody ReportRequestDTO dto) {
        ProtocolResponseDTO response = reportService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/consult")
    public ResponseEntity<ReportResponseDTO> consult(
            @RequestParam String protocol,
            @RequestParam String code
    ) {
        return ResponseEntity.ok(reportService.consult(protocol, code));
    }

    @GetMapping("/admin")
    public ResponseEntity<Page<ReportResponseDTO>> listAll(
            @PageableDefault(page = 0, size = 10, sort = "createdAt") Pageable pageable
            ) {
        Page<ReportResponseDTO> reports = reportService.findAll(pageable);
        return ResponseEntity.ok(reports);
    }

    @PatchMapping("/admin/{protocol}/status")
    public ResponseEntity<ReportResponseDTO> updateStatus(
            @PathVariable String protocol,
            @Valid @RequestBody ReportStatusUpdateRequestDTO dto,
            @AuthenticationPrincipal UserDetailsImpl principal
            ) {
        ReportResponseDTO response = reportService.updateStatus(protocol, dto, principal.getUser());
        return ResponseEntity.ok(response);
    }
}
