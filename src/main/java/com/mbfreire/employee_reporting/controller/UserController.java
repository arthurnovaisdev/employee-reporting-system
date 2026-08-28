package com.mbfreire.employee_reporting.controller;

import com.mbfreire.employee_reporting.dto.response.UserResponseDTO;
import com.mbfreire.employee_reporting.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> findAll(
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(userService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable UUID id) {
        var user = userService.findById(id);
        return ResponseEntity.ok(new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                user.isActive()
        ));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> toggleStatus(
            @PathVariable UUID id,
            @RequestParam boolean active) {
        userService.toglleActiveStatus(id, active);
        return ResponseEntity.noContent().build();
    }
}
