package com.sergiogps.bus_map_api.service;

import java.security.SecureRandom;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sergiogps.bus_map_api.entity.Seguridad;
import com.sergiogps.bus_map_api.entity.Usuarios;
import com.sergiogps.bus_map_api.repository.UsuariosRepository;

@Service
public class PasswordService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
    private static final int PASSWORD_LENGTH = 12;
    private final SecureRandom random = new SecureRandom();

    private final UsuariosRepository usuariosRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordService(UsuariosRepository usuariosRepository, PasswordEncoder passwordEncoder) {
        this.usuariosRepository = usuariosRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Generates a random password with letters, numbers, and special characters
     * @return A random password string
     */
    public String generateRandomPassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            password.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return password.toString();
    }

    /**
     * Updates the user's password (hashes it before storing)
     * @param username The username of the user
     * @param newPassword The new plain text password
     * @return true if password was updated successfully, false if user not found
     */
    @Transactional
    public boolean updatePassword(String username, String newPassword) {
        Optional<Usuarios> usuarioOpt = usuariosRepository.findByUsername(username);
        if (usuarioOpt.isEmpty()) {
            return false;
        }

        Usuarios usuario = usuarioOpt.get();
        Seguridad seguridad = usuario.getSeguridad();
        
        if (seguridad == null) {
            // Create seguridad if it doesn't exist
            seguridad = new Seguridad();
            seguridad.setUsuario(usuario);
            usuario.setSeguridad(seguridad);
        }

        seguridad.setPassword(passwordEncoder.encode(newPassword));
        usuariosRepository.save(usuario);
        return true;
    }
}
