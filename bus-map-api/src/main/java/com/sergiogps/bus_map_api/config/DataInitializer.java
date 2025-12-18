package com.sergiogps.bus_map_api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.sergiogps.bus_map_api.entity.Roles;
import com.sergiogps.bus_map_api.repository.RolesRepository;

import jakarta.annotation.PostConstruct;

@Component
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final RolesRepository rolesRepository;
    private final JdbcTemplate jdbcTemplate;

    public DataInitializer(RolesRepository rolesRepository, JdbcTemplate jdbcTemplate) {
        this.rolesRepository = rolesRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @SuppressWarnings("null")
    @PostConstruct
    public void seed() {
        try {
            if (rolesRepository.findByRolName("USER").isEmpty()) {
                Roles r = new Roles();
                r.setRolName("USER");
                try {
                    rolesRepository.save(r);
                } catch (DataIntegrityViolationException ex) {
                    log.warn("Could not insert USER role (possible duplicate or sequence issue). Continuing. {}", ex.getMessage());
                }
            }
            if (rolesRepository.findByRolName("ADMIN").isEmpty()) {
                Roles r = new Roles();
                r.setRolName("ADMIN");
                try {
                    rolesRepository.save(r);
                } catch (DataIntegrityViolationException ex) {
                    log.warn("Could not insert ADMIN role (possible duplicate or sequence issue). Continuing. {}", ex.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Unexpected error during DataInitializer.seed(): {}", e.getMessage(), e);
        }

        // Ensure Postgres sequence for roles.rol_id is aligned with max value in table to avoid duplicate key issues
        try {
            String seq = jdbcTemplate.queryForObject("SELECT pg_get_serial_sequence('roles', 'rol_id')", String.class);
            if (seq != null) {
                Long max = jdbcTemplate.queryForObject("SELECT COALESCE(MAX(rol_id),0) FROM roles", Long.class);
                // set sequence to max(rol_id) so next nextval() will be > max
                String sql = String.format("SELECT setval('%s', %d)", seq, max != null ? max : 0L);
                jdbcTemplate.execute(sql);
                log.info("Synchronized sequence {} to value {}", seq, max);
            } else {
                log.warn("No sequence found for roles.rol_id; skipping sequence sync");
            }
        } catch (Exception ex) {
            log.warn("Could not synchronize roles sequence: {}", ex.getMessage());
        }
    }
}
