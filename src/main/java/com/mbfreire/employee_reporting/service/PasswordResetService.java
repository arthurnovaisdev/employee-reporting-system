package com.mbfreire.employee_reporting.service;

import com.mbfreire.employee_reporting.dto.request.ForgotPasswordRequestDTO;
import com.mbfreire.employee_reporting.dto.request.ResetPasswordRequestDTO;
import com.mbfreire.employee_reporting.entity.PasswordResetToken;
import com.mbfreire.employee_reporting.entity.User;
import com.mbfreire.employee_reporting.exception.BusinessRuleException;
import com.mbfreire.employee_reporting.exception.ResourceNotFoundException;
import com.mbfreire.employee_reporting.repository.PasswordResetTokenRepository;
import com.mbfreire.employee_reporting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void requestPasswordReset(ForgotPasswordRequestDTO dto) {
        User user = userRepository.findByCpf(dto.cpf())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        if (user.getContactEmail() == null || user.getContactEmail().isBlank()) {
            throw new BusinessRuleException("Este usuário não possui um e-mail de contato cadastrado para recuperação.");
        }

        String tokenString = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(tokenString)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .used(false)
                .build();

        tokenRepository.save(resetToken);

        emailService.sendPasswordResetEmail(user.getContactEmail(), user.getName(), tokenString);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequestDTO dto) {
        PasswordResetToken resetToken = tokenRepository.findByToken(dto.token())
                .orElseThrow(() -> new ResourceNotFoundException("Token inválido ou não encontrado."));

        if (resetToken.isUsed()) {
            throw new BusinessRuleException("Este link de recuperação já foi utilizado.");
        }
        if (resetToken.isExpired()) {
            throw new BusinessRuleException("O link de recuperação expirou. Solicite um novo.");
        }

        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(dto.newPassword()));
        user.setPasswordChanged(true);
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }
}
