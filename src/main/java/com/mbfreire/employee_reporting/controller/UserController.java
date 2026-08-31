package com.mbfreire.employee_reporting.controller;

import com.mbfreire.employee_reporting.dto.response.UserResponseDTO;
import com.mbfreire.employee_reporting.entity.User;
import com.mbfreire.employee_reporting.security.UserDetailsImpl;
import com.mbfreire.employee_reporting.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> myProfile(@AuthenticationPrincipal UserDetailsImpl principal) {
        return ResponseEntity.ok(toDTO(principal.getUser()));
    }

    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> list(
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(userService.listPaged(pageable).map(this::toDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(toDTO(userService.findById(id)));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
        userService.setActiveStatus(id, false);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable UUID id) {
        userService.setActiveStatus(id, true);
        return ResponseEntity.noContent().build();
    }

    private UserResponseDTO toDTO(User user) {
        return new UserResponseDTO(
                user.getId(), user.getName(), user.getEmail(),
                user.getRole().name(), user.isActive()
        );
    }
}
