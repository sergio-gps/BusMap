package com.sergiogps.bus_map_api.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sergiogps.bus_map_api.entity.Usuarios;
import com.sergiogps.bus_map_api.repository.UsuariosRepository;

@Service
public class UsuariosService implements CrudService<Usuarios, Integer> {
    private final UsuariosRepository repo;

    public UsuariosService(UsuariosRepository repo) { this.repo = repo; }

    @Override
    public List<Usuarios> findAll() { return repo.findAll(); }

    @Override
    public Optional<Usuarios> findById(Integer id) {
        Integer userId = Objects.requireNonNull(id, "id no puede ser null");
        return repo.findById(userId);
    }

    @Override
    public Usuarios create(Usuarios entity) {
        Usuarios usuario = Objects.requireNonNull(entity, "entity no puede ser null");
        return Objects.requireNonNull(repo.save(usuario), "No se pudo crear el usuario");
    }

    /**
     * Busca un usuario por su email
     * @param email el email del usuario
     * @return el usuario encontrado o null si no existe
     */
    public Usuarios findByEmail(String email) {
        return repo.findByEmail(email).orElse(null);
    }

    /**
     * Busca un usuario por email y, si no existe, por username.
     * Mantiene compatibilidad mientras se termina la migración a email como identificador.
     *
     * @param login identificador recibido en autenticación
     * @return el usuario encontrado o null si no existe
     */
    public Usuarios findByEmailOrUsername(String login) {
        Usuarios porEmail = repo.findByEmail(login).orElse(null);
        if (porEmail != null) {
            return porEmail;
        }
        return repo.findByUsername(login).orElse(null);
    }

    /**
     * Guarda o actualiza un usuario
     * @param entity el usuario a guardar
     * @return el usuario guardado
     */
    public Usuarios save(Usuarios entity) {
        Usuarios usuario = Objects.requireNonNull(entity, "entity no puede ser null");
        return Objects.requireNonNull(repo.save(usuario), "No se pudo guardar el usuario");
    }

    /**
     * Elimina un usuario por su id
     * @param id el id del usuario
     */
    public void deleteById(Integer id) {
        Integer userId = Objects.requireNonNull(id, "id no puede ser null");
        repo.deleteById(userId);
    }

    /**
     * Actualiza un usuario existente
     * @param entity el usuario a actualizar
     * @return el usuario actualizado
     */
    public Usuarios update(Usuarios entity) {
        Usuarios usuario = Objects.requireNonNull(entity, "entity no puede ser null");
        return Objects.requireNonNull(repo.save(usuario), "No se pudo actualizar el usuario");
    }
}
