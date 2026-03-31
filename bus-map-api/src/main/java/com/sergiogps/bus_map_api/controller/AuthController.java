package com.sergiogps.bus_map_api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sergiogps.bus_map_api.dto.AuthRequest;
import com.sergiogps.bus_map_api.dto.AuthResponse;
import com.sergiogps.bus_map_api.dto.ChangePasswordRequestDTO;
import com.sergiogps.bus_map_api.dto.ForgotPasswordRequestDTO;
import com.sergiogps.bus_map_api.entity.Roles;
import com.sergiogps.bus_map_api.entity.Seguridad;
import com.sergiogps.bus_map_api.entity.Usuarios;
import com.sergiogps.bus_map_api.security.JwtUtil;
import com.sergiogps.bus_map_api.service.MailService;
import com.sergiogps.bus_map_api.service.PasswordService;
import com.sergiogps.bus_map_api.service.UsuariosService;

@RestController
@RequestMapping
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final PasswordService passwordService;
    private final MailService mailService;
    private final UsuariosService usuariosService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
            PasswordEncoder passwordEncoder,
            PasswordService passwordService,
            MailService mailService,
            UsuariosService usuariosService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.passwordService = passwordService;
        this.mailService = mailService;
        this.usuariosService = usuariosService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        if (req.getEmail() == null || req.getEmail().isBlank() || req.getPassword() == null) {
            return ResponseEntity.badRequest().body("Email and password are required");
        }

        try {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));

            Usuarios usuario = usuariosService.findByEmailOrUsername(req.getEmail());

            // Obtener el rol del usuario, o "USUARIO" si no tiene ninguno
            List<String> rol = List.of("USUARIO");
            if (usuario != null) {
                List<Roles> roles = usuario.getRoles();
                if (!roles.isEmpty()) {
                    rol = roles.stream().map(Roles::getRolName).toList();
                }
            }

            String token = jwtUtil.generateToken(req.getEmail());
            return ResponseEntity.ok(new AuthResponse(token, rol));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest req) {
        if (req.getEmail() == null || req.getEmail().isBlank() || req.getPassword() == null
                || req.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body("Email and password are required");
        }

        // Verificar si el usuario existe usando el service
        if (usuariosService.findByEmail(req.getEmail()) != null) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        Usuarios u = new Usuarios();
        u.setEmail(req.getEmail());
        u.setUsername(null);
        // create Seguridad and assign to Usuarios so cascade saves both
        Seguridad s = new Seguridad();
        s.setPassword(passwordEncoder.encode(req.getPassword()));
        // set both sides of the relation before persisting
        s.setUsuario(u);
        u.setSeguridad(s);
        // Persist user (and seguridad via CascadeType.ALL)
        Usuarios saved = usuariosService.save(u);

        String token = jwtUtil.generateToken(saved.getEmail());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refresh(@RequestBody AuthResponse req) {
        String token = req.getToken();
        if (token == null || !jwtUtil.validateToken(token)) {
            return ResponseEntity.badRequest().body("Invalid token");
        }
        String username = jwtUtil.extractUsername(token);
        String newToken = jwtUtil.generateToken(username);
        return ResponseEntity.ok(new AuthResponse(newToken));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequestDTO req) {
        // Validate email is provided
        if (req.getEmail() == null || req.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }

        // Find user by email using service
        Usuarios usuario = usuariosService.findByEmail(req.getEmail());
        if (usuario == null) {
            // For security reasons, don't reveal if email exists or not
            return ResponseEntity.ok("If the email exists in our system, a password reset link will be sent");
        }

        // Generate a new random password
        String newPassword = passwordService.generateRandomPassword();

        // Update the user's password
        boolean updated = passwordService.updatePassword(usuario.getEmail(), newPassword);
        if (!updated) {
            return ResponseEntity.status(500).body("Failed to update password");
        }

        // Send email with new password
        try {
            String recipientName = usuario.getUsername() != null && !usuario.getUsername().isBlank()
                    ? usuario.getUsername()
                    : usuario.getEmail();
            mailService.sendPasswordResetEmail(req.getEmail(), recipientName, newPassword);
        } catch (Exception e) {
            // Log error but don't reveal details to user
            return ResponseEntity.status(500)
                    .body("Password was reset but failed to send email. Please contact support.");
        }

        return ResponseEntity.ok("Password reset successful. Check your email for the new password.");
    }

    @PostMapping("change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequestDTO req) {
        // Validate request
        if (req.currentPassword() == null || req.newPassword() == null) {
            return ResponseEntity.badRequest().body("Old and new passwords are required");
        }

        // Get the currently authenticated user
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuarios usuario = usuariosService.findByEmailOrUsername(login);
        if (usuario == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        // Check if old password matches
        if (!passwordEncoder.matches(req.currentPassword(), usuario.getSeguridad().getPassword())) {
            return ResponseEntity.status(401).body("Old password is incorrect");
        }

        // Update password
        usuario.getSeguridad().setPassword(passwordEncoder.encode(req.newPassword()));
        usuariosService.save(usuario);

        return ResponseEntity.ok("Password changed successfully");
    }

}
