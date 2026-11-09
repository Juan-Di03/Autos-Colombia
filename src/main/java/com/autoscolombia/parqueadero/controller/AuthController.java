package com.autoscolombia.parqueadero.controller;

import com.autoscolombia.parqueadero.security.JwtAuthenticationFilter;
import com.autoscolombia.parqueadero.security.JwtService;
import com.autoscolombia.parqueadero.security.UserPrincipal;
import com.autoscolombia.parqueadero.security.dto.AuthRequest;
import com.autoscolombia.parqueadero.security.dto.AuthResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;

/**
 * Endpoint REST para autenticación y emisión de tokens JWT (idéntico a RollerApp).
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getIdentificador(), request.getPassword()));

        UserDetails principal = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(principal);

        String rol = principal instanceof UserPrincipal customPrincipal
                ? customPrincipal.getRol()
                : principal.getAuthorities().stream().findFirst().map(ga -> ga.getAuthority()).orElse("CLIENTE");

        String nombre = principal instanceof UserPrincipal customPrincipal ? customPrincipal.getNombre() : principal.getUsername();

        AuthResponse response = new AuthResponse(
                token,
                jwtService.getExpirationInMillis(),
                Instant.now().toString(),
                rol.replace("ROLE_", ""),
                nombre);

        ResponseCookie cookie = ResponseCookie.from(JwtAuthenticationFilter.JWT_COOKIE_NAME, token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofMillis(jwtService.getExpirationInMillis()))
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie cookie = ResponseCookie.from(JwtAuthenticationFilter.JWT_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }
}
