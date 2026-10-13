package com.mbfreire.employee_reporting.config;

import com.mbfreire.employee_reporting.entity.User;
import com.mbfreire.employee_reporting.enums.Role;
import com.mbfreire.employee_reporting.repository.UserRepository;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Proxy;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AdminBootstrapConfigTest {

    private static final String ADMIN_CPF = "12345678901";
    private static final String ADMIN_PASSWORD = "SenhaForte123";
    private final AdminBootstrapConfig config = new AdminBootstrapConfig();
    private final AtomicReference<User> saved = new AtomicReference<>();

    @Test
    void createsAdminWithConfiguredContactEmail() throws Exception {
        runBootstrap(repository(Optional.empty()), " Admin@Example.com ");

        User admin = saved.get();
        assertEquals(Role.ADMIN, admin.getRole());
        assertEquals(ADMIN_CPF, admin.getCpf());
        assertEquals("admin@example.com", admin.getContactEmail());
        assertEquals("hashed-password", admin.getPasswordHash());
    }

    @Test
    void fillsMissingEmailOnExistingAdminWithoutChangingPassword() throws Exception {
        User admin = User.builder()
                .cpf(ADMIN_CPF)
                .passwordHash("existing-hash")
                .role(Role.ADMIN)
                .build();

        runBootstrap(repository(Optional.of(admin)), "admin@example.com");

        assertEquals("admin@example.com", admin.getContactEmail());
        assertEquals("existing-hash", admin.getPasswordHash());
        assertEquals(admin, saved.get());
    }

    @Test
    void preservesExistingContactEmail() throws Exception {
        User admin = User.builder()
                .cpf(ADMIN_CPF)
                .contactEmail("current@example.com")
                .role(Role.ADMIN)
                .build();

        runBootstrap(repository(Optional.of(admin)), "replacement@example.com");

        assertEquals("current@example.com", admin.getContactEmail());
        assertNull(saved.get());
    }

    @Test
    void rejectsInvalidContactEmail() {
        assertThrows(IllegalStateException.class, () ->
                runBootstrap(repository(Optional.empty()), "invalid-email"));
        assertNull(saved.get());
    }

    private void runBootstrap(UserRepository repository, String contactEmail) throws Exception {
        try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
            config.createInitialAdmin(
                    repository,
                    new PasswordEncoder() {
                        @Override
                        public String encode(CharSequence rawPassword) {
                            return "hashed-password";
                        }

                        @Override
                        public boolean matches(CharSequence rawPassword, String encodedPassword) {
                            return false;
                        }
                    },
                    validatorFactory.getValidator(),
                    ADMIN_CPF,
                    ADMIN_PASSWORD,
                    contactEmail
            ).run();
        }
    }

    private UserRepository repository(Optional<User> existing) {
        return (UserRepository) Proxy.newProxyInstance(
                UserRepository.class.getClassLoader(),
                new Class<?>[]{UserRepository.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "findByCpf" -> existing;
                    case "save" -> {
                        User user = (User) args[0];
                        saved.set(user);
                        yield user;
                    }
                    default -> throw new UnsupportedOperationException(method.getName());
                }
        );
    }
}
