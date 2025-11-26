package com.sergiogps.bus_map_api.controller;

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
import com.sergiogps.bus_map_api.entity.Seguridad;
import com.sergiogps.bus_map_api.entity.Usuarios;
import com.sergiogps.bus_map_api.repository.UsuariosRepository;
import com.sergiogps.bus_map_api.security.JwtUtil;
import com.sergiogps.bus_map_api.service.MailService;
import com.sergiogps.bus_map_api.service.PasswordService;

@RestController
@RequestMapping
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UsuariosRepository usuariosRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordService passwordService;
    private final MailService mailService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
            UsuariosRepository usuariosRepository,
            PasswordEncoder passwordEncoder,
            PasswordService passwordService,
            MailService mailService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.usuariosRepository = usuariosRepository;
        // seguridadRepository removed; we will persist Seguridad through Usuarios
        // cascade
        this.passwordEncoder = passwordEncoder;
        this.passwordService = passwordService;
        this.mailService = mailService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        try {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
            String token = jwtUtil.generateToken(req.getUsername());
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest req) {
        if (usuariosRepository.findByUsername(req.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        Usuarios u = new Usuarios();
        u.setUsername(req.getUsername());
        // create Seguridad and assign to Usuarios so cascade saves both
        Seguridad s = new Seguridad();
        s.setPassword(passwordEncoder.encode(req.getPassword()));
        // set both sides of the relation before persisting
        s.setUsuario(u);
        u.setSeguridad(s);
        // Persist user (and seguridad via CascadeType.ALL)
        Usuarios saved = usuariosRepository.save(u);

        String token = jwtUtil.generateToken(saved.getUsername());
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

        // Find user by email
        var usuarioOpt = usuariosRepository.findByUsername(req.getEmail());
        if (usuarioOpt.isEmpty()) {
            // For security reasons, don't reveal if email exists or not
            return ResponseEntity.ok("If the email exists in our system, a password reset link will be sent");
        }

        Usuarios usuario = usuarioOpt.get();

        // Generate a new random password
        String newPassword = passwordService.generateRandomPassword();

        // Update the user's password
        boolean updated = passwordService.updatePassword(usuario.getUsername(), newPassword);
        if (!updated) {
            return ResponseEntity.status(500).body("Failed to update password");
        }

        // Send email with new password
        try {
            mailService.sendPasswordResetEmail(req.getEmail(), usuario.getUsername(), newPassword);
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
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuarios usuario = usuariosRepository.findByUsername(username).orElse(null);
        if (usuario == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        // Check if old password matches
        if (!passwordEncoder.matches(req.currentPassword(), usuario.getSeguridad().getPassword())) {
            return ResponseEntity.status(401).body("Old password is incorrect");
        }

        // Update password
        usuario.getSeguridad().setPassword(passwordEncoder.encode(req.newPassword()));
        usuariosRepository.save(usuario);

        return ResponseEntity.ok("Password changed successfully");
    }

}
