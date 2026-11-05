package com.hwsinha.carbid.auth;

import com.hwsinha.carbid.auth.dto.LoginRequest;
import com.hwsinha.carbid.auth.dto.LoginResponse;
import com.hwsinha.carbid.auth.dto.RegisterRequest;
import com.hwsinha.carbid.security.JwtService;
import com.hwsinha.carbid.user.Role;
import com.hwsinha.carbid.user.User;
import com.hwsinha.carbid.user.UserRepository;
import com.hwsinha.carbid.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public UserResponse registerUser(RegisterRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if(userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }
        String passwordHash = passwordEncoder.encode(request.password());
        User user=new User();
        user.setEmail(email);
        user.setCity(request.city());
        user.setName(request.name());
        user.setPasswordHash(passwordHash);
        user.setRole(Role.BUYER);
        User savedUser=userRepository.save(user);
        return toUserResponse(savedUser);
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        User user= userRepository.findByEmail(email)
                .filter(u-> passwordEncoder.matches(request.password(), u.getPasswordHash()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        String token=jwtService.generateToken(user);
        return new LoginResponse(token,"Bearer", jwtService.getExpirySeconds());
    }

    private UserResponse toUserResponse(User savedUser) {
        Long id=savedUser.getId();
        String name=savedUser.getName();
        String email=savedUser.getEmail();
        Role role=savedUser.getRole();
        String city=savedUser.getCity();
        return new UserResponse(id,name,email,role,city);
    }
}
