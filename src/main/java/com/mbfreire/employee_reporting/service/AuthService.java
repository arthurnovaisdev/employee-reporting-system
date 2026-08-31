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
                new UsernamePasswordAuthenticationToken(dto.email(),dto.password())
        );

        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        String token = jwtService.generateToken(new UserDetailsImpl(user));

        return new LoginResponseDTO(token, user.getName(), user.getRole().name());
    }

    public void register(RegisterRequestDTO dto) {
        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new BusinessRuleException("E-mail já cadastrado.");
        }

        User user = User.builder()
                .name(dto.name())
                .email(dto.email())
                .passwordHash(passwordEncoder.encode(dto.password()))
                .role(Role.EMPLOYEE)
                .active(true)
                .build();

        userRepository.save(user);
    }
}
