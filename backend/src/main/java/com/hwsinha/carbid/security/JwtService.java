package com.hwsinha.carbid.security;

import com.hwsinha.carbid.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${app.jwt.expiry-minutes}")
    private int expiryMinutes;
    private final JwtEncoder jwtEncoder;

    public String generateToken(User user) {
        Instant now = Instant.now();
        JwsHeader header= JwsHeader.with(MacAlgorithm.HS256).build();
        JwtClaimsSet claims= JwtClaimsSet.builder()
                .issuer("carbid")
                .subject(user.getId().toString())
                .issuedAt(now)
                .expiresAt(now.plus(expiryMinutes, ChronoUnit.MINUTES))
                .claim("role", user.getRole().name())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public long getExpirySeconds() {
        return expiryMinutes * 60L;
    }

}
