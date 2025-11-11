package org.example.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Flux;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);


    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.GET, "/songs/**", "/resources/**").hasAnyRole("USER", "ADMIN")
                        .pathMatchers(HttpMethod.POST, "/songs/**", "/resources/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/songs/**", "/resources/**").hasRole("ADMIN")
                        .pathMatchers("/actuator/**", "/eureka/**").permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );
        log.info("SecurityWebFilterChain initialized");


        return http.build();
    }

    @Bean
    public ReactiveJwtAuthenticationConverter jwtAuthenticationConverter() {
        ReactiveJwtAuthenticationConverter converter = new ReactiveJwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            log.warn("****: "+jwt);
            List<?> roles = (List<?>) jwt.getClaim("roles");
            log.warn("JWT payload: " + jwt.getClaims());

            if (roles == null) {
                roles = List.of();
            }

            return Flux.fromIterable(roles)
                    .map(role -> new SimpleGrantedAuthority("" + role));
        });

        return converter;
    }
}
