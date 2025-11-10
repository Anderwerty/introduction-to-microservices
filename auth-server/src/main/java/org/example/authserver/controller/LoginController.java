package org.example.authserver.controller;

import org.example.authserver.dto.LoginRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class LoginController {

    private final UserDetailsService userDetailsService;
    private final JwtEncoder jwtEncoder;

    public LoginController(UserDetailsService uds, JwtEncoder encoder) {
        this.userDetailsService = uds;
        this.jwtEncoder = encoder;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> login(@RequestBody LoginRequest request) {
        String username = request.username();
        String password = request.password();
        UserDetails user = userDetailsService.loadUserByUsername(username);

        if(user != null && "{noop}".concat(password).equals(user.getPassword())) {
            Instant now = Instant.now();
            String token = jwtEncoder.encode(
                    JwtEncoderParameters.from(JwtClaimsSet.builder()
                            .issuer("auth-server")
                            .issuedAt(now)
                            .expiresAt(now.plus(1, ChronoUnit.HOURS))
                            .subject(username)
                            .claim("roles", user.getAuthorities().stream()
                                    .map(GrantedAuthority::getAuthority)
                                    .toList())
                            .build())
            ).getTokenValue();

            return ResponseEntity.ok(Map.of("access_token", token));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
