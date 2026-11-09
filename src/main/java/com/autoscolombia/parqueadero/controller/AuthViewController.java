package com.autoscolombia.parqueadero.controller;

import com.autoscolombia.parqueadero.security.JwtAuthenticationFilter;
import com.autoscolombia.parqueadero.security.JwtService;
import com.autoscolombia.parqueadero.security.UserPrincipal;
import com.autoscolombia.parqueadero.security.dto.AuthRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador MVC para flujo de login/logout con vistas Thymeleaf usando JWT.
 */
@Controller
public class AuthViewController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final SecurityContextRepository securityContextRepository;

    public AuthViewController(AuthenticationManager authenticationManager,
                              JwtService jwtService,
                              SecurityContextRepository securityContextRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.securityContextRepository = securityContextRepository;
    }

    @GetMapping("/login")
    public String loginForm(@RequestParam(value = "logout", required = false) String logout, Model model) {
        if (!model.containsAttribute("authRequest")) {
            model.addAttribute("authRequest", new AuthRequest());
        }
        if (logout != null) {
            model.addAttribute("mensajeLogout", "Sesión cerrada correctamente.");
        }
        return "login";
    }

    @PostMapping("/login")
    public String loginSubmit(@ModelAttribute("authRequest") AuthRequest request,
                             HttpServletRequest httpRequest,
                             HttpServletResponse httpResponse,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        try {
            // 1. Autenticar con DaoAuthenticationProvider (BCrypt estándar)
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getIdentificador(), request.getPassword()));

            // 2. Guardar contexto en sesión (Spring Security 6 requiere esto explícitamente)
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, httpRequest, httpResponse);

            // 3. Generar token JWT y establecer cookie HttpOnly
            UserDetails principal = (UserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(principal);

            Cookie jwtCookie = new Cookie(JwtAuthenticationFilter.JWT_COOKIE_NAME, token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(false); // false para localhost
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge((int) (jwtService.getExpirationInMillis() / 1000));
            httpResponse.addCookie(jwtCookie);

            // 4. Determinar rol para redirección
            String rol = principal instanceof UserPrincipal userPrincipal
                    ? userPrincipal.getRol()
                    : principal.getAuthorities().stream()
                            .findFirst()
                            .map(authority -> authority.getAuthority().replace("ROLE_", ""))
                            .orElse("EMPLEADO");

            redirectAttributes.addFlashAttribute("mensajeExito", "Bienvenido al sistema.");

            // 5. Redirigir según el rol del usuario
            return switch (rol) {
                case "ADMINISTRADOR" -> "redirect:/usuarios";
                case "CLIENTE" -> "redirect:/consultar-celda";
                default -> "redirect:/ingreso";
            };

        } catch (BadCredentialsException ex) {
            model.addAttribute("errorLogin", "Documento/Correo o contraseña incorrectos.");
            return "login";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
                         RedirectAttributes redirectAttributes) {
        SecurityContextHolder.clearContext();

        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Eliminar la cookie del JWT
        Cookie jwtCookie = new Cookie(JwtAuthenticationFilter.JWT_COOKIE_NAME, "");
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(false);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);
        httpResponse.addCookie(jwtCookie);

        redirectAttributes.addFlashAttribute("mensajeLogout", "Sesión cerrada correctamente.");
        return "redirect:/login?logout=true";
    }
}
