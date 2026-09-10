package com.mbfreire.employee_reporting.config;

import com.mbfreire.employee_reporting.entity.User;
import com.mbfreire.employee_reporting.enums.Role;
import com.mbfreire.employee_reporting.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("bootstrap-admin")
@Slf4j
public class AdminBootstrapConfig {

    private static final int MIN_PASSWORD_LENGTH = 6;

    @Bean
    CommandLineRunner createInitialAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${admin.bootstrap.cpf}") String adminCpf,
            @Value("${admin.bootstrap.password}") String adminPassword
    ) {
        return args -> {
            validateCpf(adminCpf);
            validatePassword(adminPassword);

            var existingUser = userRepository.findByCpf(adminCpf);

            if (existingUser.isPresent()) {
                User user = existingUser.get();
                if (user.getRole() != Role.ADMIN) {
                    throw new IllegalStateException("O CPF configurado para bootstrap já pertence a um usuário que não é ADMIN.");
                }

            log.info("Bootstrap administrativo ignorado: a conta administrativa já existe.");

        return;

    }

        User admin = User.builder()
                .name("Administrador")
                .cpf(adminCpf)
                .passwordHash(passwordEncoder.encode(adminPassword))
                .role(Role.ADMIN)
                .build();

        userRepository.save(admin);
        log.info("Conta administrativa inicial criada com sucesso.");
    };
}

    private void validateCpf(String cpf) {
    if (cpf == null || !cpf.matches("\\d{11}")) {
        throw new IllegalStateException("ADMIN_CPF deve conter exatamente 11 dígitos.");
    }
    }

    private void validatePassword(String password) {
    if (password == null || password.isBlank()) {
        throw new IllegalStateException("ADMIN_INITIAL_PASSWORD não foi configurada.");
    }

    if (password.length() < MIN_PASSWORD_LENGTH) {
        throw new IllegalStateException("ADMIN_INITIAL_PASSWORD deve possuir pelo menos "
                + MIN_PASSWORD_LENGTH
                + " caracteres.");
    }

    String normalized = password.trim().toLowerCase();

    if (normalized.equals("changeme123") || normalized.equals("password") || normalized.equals("admin123") || normalized.equals("123456")) {
        throw new IllegalStateException("ADMIN_INITIAL_PASSWORD é muito previsível.");
        }
    }
}
