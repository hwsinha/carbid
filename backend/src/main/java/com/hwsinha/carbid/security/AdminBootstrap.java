package com.hwsinha.carbid.security;

import com.hwsinha.carbid.user.Role;
import com.hwsinha.carbid.user.User;
import com.hwsinha.carbid.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminBootstrap implements ApplicationRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String email;

    @Value("${app.admin.password}")
    private String password;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String adminEmail = email.trim().toLowerCase(Locale.ROOT);

        if (adminEmail.isEmpty() || password.isBlank()) {
            log.info("No admin configuration provided");
            return;
        }
        if (userRepository.existsByRole(Role.ADMIN)) {
            log.info("Admin already exists");
            return;
        }
        if (password.length() < 12) {
            log.error("Admin password must be at least 12 characters; admin not created");
            return;
        }

        User user = new User();
        user.setEmail(adminEmail);
        user.setName("Administrator");
        user.setCity("N/A");
        user.setRole(Role.ADMIN);
        user.setPasswordHash(passwordEncoder.encode(password));
        userRepository.save(user);
        log.info("Admin account created for {}", adminEmail);
    }
}
