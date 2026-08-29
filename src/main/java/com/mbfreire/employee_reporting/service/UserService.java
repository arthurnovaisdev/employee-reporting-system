package com.mbfreire.employee_reporting.service;

import com.mbfreire.employee_reporting.entity.User;
import com.mbfreire.employee_reporting.exception.ResourceNotFoundException;
import com.mbfreire.employee_reporting.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("E-mail não encontrado."));
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
