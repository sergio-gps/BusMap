package com.sergiogps.bus_map_api.service;

import java.util.List;
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
    public Optional<Usuarios> findById(Integer id) { return repo.findById(id); }

    @Override
    public Usuarios create(Usuarios entity) { return repo.save(entity); }

    /**
     * Busca un usuario por su email
     * @param email el email del usuario
     * @return el usuario encontrado o null si no existe
     */
    public Usuarios findByEmail(String email) {
        return repo.findByEmail(email).orElse(null);
    }

    /**
     * Guarda o actualiza un usuario
     * @param entity el usuario a guardar
     * @return el usuario guardado
     */
    public Usuarios save(Usuarios entity) {
        return repo.save(entity);
    }

    /**
     * Elimina un usuario por su id
     * @param id el id del usuario
     */
    public void deleteById(Integer id) {
        repo.deleteById(id);
    }

    /**
     * Actualiza un usuario existente
     * @param entity el usuario a actualizar
     * @return el usuario actualizado
     */
    public Usuarios update(Usuarios entity) {
        return repo.save(entity);
    }
}
