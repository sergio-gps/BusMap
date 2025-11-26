package com.sergiogps.bus_map_api.security;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sergiogps.bus_map_api.entity.Usuarios;
import com.sergiogps.bus_map_api.entity.UsuariosRoles;
import com.sergiogps.bus_map_api.repository.UsuariosRepository;
import com.sergiogps.bus_map_api.repository.UsuariosRolesRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuariosRepository usuariosRepository;
    private final UsuariosRolesRepository usuariosRolesRepository;

    public CustomUserDetailsService(UsuariosRepository usuariosRepository, UsuariosRolesRepository usuariosRolesRepository) {
        this.usuariosRepository = usuariosRepository;
        this.usuariosRolesRepository = usuariosRolesRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuarios u = usuariosRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<UsuariosRoles> urs = usuariosRolesRepository.findByUsuarioUsuarioId(u.getUsuarioId());
        List<GrantedAuthority> authorities = urs.stream()
                .map(ur -> new SimpleGrantedAuthority("ROLE_" + ur.getRol().getRolName()))
                .collect(Collectors.toList());

        if (authorities.isEmpty()) {
            authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return User.builder()
                .username(u.getUsername())
                .password(u.getSeguridad() != null ? u.getSeguridad().getPassword() : "")
                .authorities(authorities)
                .build();
    }
}
