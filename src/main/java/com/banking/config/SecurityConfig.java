package com.banking.config;

import com.banking.filters.JWTFilter;
import com.banking.repository.TokenRepository;
import com.banking.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;

import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {
    private final AuthenticationProvider authenticationProvider;
    private final JWTFilter jwtFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors(Customizer.withDefaults())
                .csrf(CsrfConfigurer())
                .authorizeHttpRequests(request ->
                        request
                                .requestMatchers("/auth/**")
                                .permitAll()
                                .anyRequest()
                                .authenticated()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(customLogout())
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(jwtAuthenticationEntryPoint)
                );
        return httpSecurity.build();
    }

    private static Customizer<CsrfConfigurer<HttpSecurity>> CsrfConfigurer(){
        return AbstractHttpConfigurer::disable;
    }

    private Customizer<LogoutConfigurer<HttpSecurity>> customLogout() {
        return logout -> logout
                .logoutUrl("/auth/logout")
                .addLogoutHandler((request, response, authentication) -> {
                    final String authHeader = request.getHeader("Authorization");

                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        final String encryptedToken = authHeader.substring(7);
                        try {
                            String decryptedToken = jwtService.decryptJwt(encryptedToken);

                            tokenRepository.findByToken(decryptedToken).ifPresent(token -> {
                                System.out.println("Found token");
                                token.setExpired(true);
                                token.setRevoked(true);
                                tokenRepository.save(token);
                                System.out.println("Saved token into expired token");
                            });

                        } catch (Exception e) {
                            System.out.println("Failed to revoke token: " + e.getMessage());
                        }
                    }

                    SecurityContextHolder.clearContext();
                })
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID");
    }

}
