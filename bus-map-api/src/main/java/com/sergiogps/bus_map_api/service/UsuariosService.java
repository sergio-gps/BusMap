package com.sergiogps.bus_map_api.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sergiogps.bus_map_api.entity.Usuarios;
import com.sergiogps.bus_map_api.repository.RolesRepository;
import com.sergiogps.bus_map_api.repository.UsuariosRepository;

@Service
public class UsuariosService implements CrudService<Usuarios, Integer> {
    private final UsuariosRepository userRepository;
    private final RolesRepository rolesRepository;

    public UsuariosService(UsuariosRepository repo, RolesRepository rolesRepo) {
        this.userRepository = repo;
        this.rolesRepository = rolesRepo;
    }

    @Override
    public List<Usuarios> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<Usuarios> findById(Integer id) {
        Integer userId = Objects.requireNonNull(id, "id no puede ser null");
        return userRepository.findById(userId);
    }

    @Override
    public Usuarios create(Usuarios entity) {
        Usuarios usuario = Objects.requireNonNull(entity, "entity no puede ser null");
        return Objects.requireNonNull(userRepository.save(usuario), "No se pudo crear el usuario");
    }

    /**
     * Busca un usuario por su email
     * 
     * @param email el email del usuario
     * @return el usuario encontrado o null si no existe
     */
    public Usuarios findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    /**
     * Busca un usuario por email y, si no existe, por username.
     *
     * @param login identificador recibido en autenticación
     * @return el usuario encontrado o null si no existe
     */
    public Usuarios findByEmailOrUsername(String login) {
        Usuarios porEmail = userRepository.findByEmail(login).orElse(null);
        if (porEmail != null) {
            return porEmail;
        }
        return userRepository.findByUsername(login).orElse(null);
    }

    /**
     * Guarda o actualiza un usuario
     * 
     * @param entity el usuario a guardar
     * @return el usuario guardado
     */
    public Usuarios save(Usuarios entity) {
        Usuarios usuario = Objects.requireNonNull(entity, "entity no puede ser null");
        return Objects.requireNonNull(userRepository.save(usuario), "No se pudo guardar el usuario");
    }

    /**
     * Elimina un usuario por su id
     * 
     * @param id el id del usuario
     */
    public void deleteById(Integer id) {
        Integer userId = Objects.requireNonNull(id, "id no puede ser null");
        userRepository.deleteById(userId);
    }

    /**
     * Actualiza un usuario existente
     * 
     * @param entity el usuario a actualizar
     * @return el usuario actualizado
     */
    public Usuarios update(Usuarios entity) {
        Usuarios usuario = Objects.requireNonNull(entity, "entity no puede ser null");
        return Objects.requireNonNull(userRepository.save(usuario), "No se pudo actualizar el usuario");
    }

    /**
     * Añade un nuevo rol a un usuario existente
     * 
     * @param userId el id del usuario
     * @param rol    el rol a añadir
     * @return el usuario actualizado con el nuevo rol
     */
    public Usuarios addRoleToUser(Integer userId, String rol) {
        Usuarios usuario = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id: " + userId));
        boolean rolExists = usuario.getRoles().stream().anyMatch(role -> role.getRolName().equals(rol));

        if (!rolExists) {
            usuario.getRoles().add(rolesRepository.findByRolName(rol)
                    .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado con nombre: " + rol)));
            return userRepository.save(usuario);
        }
        return usuario; // El rol ya estaba asignado, no se modifica el usuario
    }

    /**
     * Quita un rol de un usuario existente
     * 
     * @param userId el id del usuario
     * @param rol    el rol a quitar
     * @return el usuario actualizado sin el rol
     */
    public Usuarios removeRoleFromUser(Integer userId, String rol) {
        Usuarios usuario = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id: " + userId));
        boolean rolExists = usuario.getRoles().stream().anyMatch(role -> role.getRolName().equals(rol));

        if (rolExists) {
            usuario.getRoles().removeIf(role -> role.getRolName().equals(rol));
            return userRepository.save(usuario);
        }
        return usuario; // El rol no estaba asignado, no se modifica el usuario
    }
}
