package com.mbfreire.employee_reporting.service;

import com.mbfreire.employee_reporting.dto.request.ChangePasswordRequestDTO;
import com.mbfreire.employee_reporting.entity.User;
import com.mbfreire.employee_reporting.exception.BusinessRuleException;
import com.mbfreire.employee_reporting.exception.ResourceNotFoundException;
import com.mbfreire.employee_reporting.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void ChangePassword(UUID userId, ChangePasswordRequestDTO dto) {
        User user = findById(userId);

        if (!passwordEncoder.matches(dto.currentPassword(), user.getPasswordHash())) {
            throw new BusinessRuleException("A senha atual está incorreta.");
        }

        if (passwordEncoder.matches(dto.newPassword(), user.getPasswordHash())) {
            throw new BusinessRuleException("A nova senha não pode ser igual à senha provisória.");
        }

        user.setPasswordHash(passwordEncoder.encode(dto.newPassword()));
        user.setPasswordChanged(true);

        userRepository.save(user);
    }

    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
    }

    public User findByCpf(String cpf) {
        return userRepository.findByCpf(cpf)
                .orElseThrow(() -> new ResourceNotFoundException("CPF não encontrado."));
    }

    public Page<User> listPaged(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Transactional
    public void setActiveStatus(UUID id, boolean active) {
        User user = findById(id);
        user.setActive(active);
        userRepository.save(user);
    }
}
