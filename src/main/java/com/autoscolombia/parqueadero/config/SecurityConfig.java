package com.autoscolombia.parqueadero.config;

import com.autoscolombia.parqueadero.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

/**
 * Configuración de Spring Security con JWT (arquitectura basada en RollerApp).
 * Usa DaoAuthenticationProvider estándar con BCrypt.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Proveedor de autenticación estándar: usa el UserDetailsService para cargar
     * el usuario y BCryptPasswordEncoder para validar la contraseña.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
                                                           PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   DaoAuthenticationProvider authProvider,
                                                   SecurityContextRepository secContextRepo) throws Exception {
        http
            .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**", "/h2-console/**", "/login", "/login/**", "/logout"))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .securityContext(context -> context.securityContextRepository(secContextRepo))
            .authenticationProvider(authProvider)
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas y recursos estáticos
                .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico", "/h2-console/**").permitAll()
                .requestMatchers("/login/**", "/logout", "/api/auth/**").permitAll()
                .requestMatchers("/consultar-celda/**").permitAll()

                // Gestión de usuarios: exclusivo para ADMINISTRADOR
                .requestMatchers("/usuarios/**").hasRole("ADMINISTRADOR")

                // Operación diaria: ADMINISTRADOR y EMPLEADO
                .requestMatchers("/", "/ingreso/**", "/salida/**", "/celdas/**", "/novedades/**", "/historial/**")
                    .hasAnyRole("ADMINISTRADOR", "EMPLEADO")

                // Cualquier otra requiere autenticación
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> response.sendRedirect("/login"))
                .accessDeniedPage("/login?error=denied")
            )
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
