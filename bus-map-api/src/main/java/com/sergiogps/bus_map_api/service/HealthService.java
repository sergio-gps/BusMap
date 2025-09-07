package com.sergiogps.bus_map_api.service;

import com.sergiogps.bus_map_api.dto.HealthDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Service
public class HealthService {

    private final DataSource dataSource;

    @Autowired
    public HealthService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Get API health status including database connectivity
     */
    public HealthDto getHealthStatus() {
        HealthDto healthDto = new HealthDto();
        healthDto.setVersion("1.0.0");
        
        try {
            // Check database connectivity
            HealthDto.DatabaseHealth dbHealth = checkDatabaseHealth();
            healthDto.setDatabase(dbHealth);
            
            // Set overall status based on database health
            if ("UP".equals(dbHealth.getStatus())) {
                healthDto.setStatus("UP");
                healthDto.setMessage("Bus Map API is running successfully");
            } else {
                healthDto.setStatus("DOWN");
                healthDto.setMessage("API has issues - Database connection failed");
            }
            
        } catch (Exception e) {
            healthDto.setStatus("DOWN");
            healthDto.setMessage("Health check failed: " + e.getMessage());
            
            HealthDto.DatabaseHealth dbHealth = new HealthDto.DatabaseHealth("DOWN", -1L);
            healthDto.setDatabase(dbHealth);
        }
        
        return healthDto;
    }

    /**
     * Get simple health status for quick checks
     */
    public String getSimpleStatus() {
        try {
            checkDatabaseConnection();
            return "UP";
        } catch (Exception e) {
            return "DOWN";
        }
    }

    /**
     * Check database health with response time measurement
     */
    private HealthDto.DatabaseHealth checkDatabaseHealth() {
        long startTime = System.currentTimeMillis();
        
        try {
            checkDatabaseConnection();
            long responseTime = System.currentTimeMillis() - startTime;
            return new HealthDto.DatabaseHealth("UP", responseTime);
            
        } catch (SQLException e) {
            long responseTime = System.currentTimeMillis() - startTime;
            return new HealthDto.DatabaseHealth("DOWN", responseTime);
        }
    }

    /**
     * Test database connection
     */
    private void checkDatabaseConnection() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            if (!connection.isValid(5)) { // 5 seconds timeout
                throw new SQLException("Database connection is not valid");
            }
        }
    }
}
