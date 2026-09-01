package com.mbfreire.employee_reporting.config;

import com.mbfreire.employee_reporting.entity.User;
import com.mbfreire.employee_reporting.enums.Role;
import com.mbfreire.employee_reporting.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminBootstrapConfig {

    @Bean
    CommandLineRunner createInitialAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${admin.bootstrap.email}") String adminEmail,
            @Value("${admin.bootstrap.password}") String adminPassword
    ) {
        return args -> {
         if (userRepository.findByEmail(adminEmail).isEmpty()) {
             User admin = User.builder()
                     .name("Administrador")
                     .email(adminEmail)
                     .passwordHash(passwordEncoder.encode(adminPassword))
                     .role(Role.ADMIN)
                     .build();

             userRepository.save(admin);
             System.out.println("Conta de admin inicial criada: " + adminEmail);
         }
        };
    }
}
