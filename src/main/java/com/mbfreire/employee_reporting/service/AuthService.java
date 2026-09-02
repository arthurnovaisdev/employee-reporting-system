package com.mbfreire.employee_reporting.service;

import com.mbfreire.employee_reporting.dto.request.LoginRequestDTO;
import com.mbfreire.employee_reporting.dto.request.RegisterRequestDTO;
import com.mbfreire.employee_reporting.dto.response.LoginResponseDTO;
import com.mbfreire.employee_reporting.entity.User;
import com.mbfreire.employee_reporting.enums.Role;
import com.mbfreire.employee_reporting.exception.BusinessRuleException;
import com.mbfreire.employee_reporting.exception.ResourceNotFoundException;
import com.mbfreire.employee_reporting.repository.UserRepository;
import com.mbfreire.employee_reporting.security.JWTService;
import com.mbfreire.employee_reporting.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    public LoginResponseDTO login(LoginRequestDTO dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.cpf(),dto.password())
        );

        User user = userRepository.findByCpf(dto.cpf())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        String token = jwtService.generateToken(new UserDetailsImpl(user));

        return new LoginResponseDTO(token, user.getName(), user.getRole().name(), user.isPasswordChanged());
    }

    public void register(RegisterRequestDTO dto) {
        if (userRepository.findByCpf(dto.cpf()).isPresent()) {
            throw new BusinessRuleException("CPF já cadastrado no sistema.");
        }

        User user = User.builder()
                .name(dto.name())
                .cpf(dto.cpf())
                .contactEmail(dto.contactEmail())
                .passwordHash(passwordEncoder.encode(dto.password()))
                .role(Role.EMPLOYEE)
                .passwordChanged(false)
                .build();

        userRepository.save(user);
    }
}
