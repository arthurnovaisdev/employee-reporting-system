package com.mbfreire.employee_reporting.controller;

import com.mbfreire.employee_reporting.dto.request.ReportRequestDTO;
import com.mbfreire.employee_reporting.dto.request.ReportStatusUpdateRequestDTO;
import com.mbfreire.employee_reporting.dto.response.AttachmentDownloadDTO;
import com.mbfreire.employee_reporting.dto.response.ProtocolResponseDTO;
import com.mbfreire.employee_reporting.dto.response.ReportAdminResponseDTO;
import com.mbfreire.employee_reporting.dto.response.ReportResponseDTO;
import com.mbfreire.employee_reporting.security.UserDetailsImpl;
import com.mbfreire.employee_reporting.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

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

    @PostMapping(value = "/{protocol}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadAttachments(
            @PathVariable String protocol,
            @RequestParam("trackingCode") String trackingCode,
            @RequestParam("files")List<MultipartFile> files
            ) {
        reportService.uploadAttachments(protocol, trackingCode, files);
        return ResponseEntity.status(HttpStatus.CREATED).build();
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
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<ReportResponseDTO> reports = reportService.findAll(pageable);

        return ResponseEntity.ok(reports);
    }

    @GetMapping("/admin/{protocol}")
    public ResponseEntity<ReportAdminResponseDTO> findAdminDetail(@PathVariable String protocol) {
        ReportAdminResponseDTO response = reportService.findAdminDetail(protocol);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/{protocol}/attachments/{attachmentId}")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable String protocol, @PathVariable UUID attachmentId) {
        AttachmentDownloadDTO attachment = reportService.downloadAttachment(protocol, attachmentId);

        ContentDisposition contentDisposition = ContentDisposition
                .inline()
                .filename(
                        attachment.originalFileName(),
                        StandardCharsets.UTF_8
                ).build();

        return ResponseEntity.ok().
                contentType(
                MediaType.parseMediaType(
                        attachment.contentType()
                )
        )
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        contentDisposition.toString()
                ).body(attachment.resource());
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
