package com.sergiogps.bus_map_api.dto;

import java.time.LocalDateTime;

public class HealthDto {
    private String status;
    private String message;
    private LocalDateTime timestamp;
    private String version;
    private DatabaseHealth database;

    public HealthDto() {
        this.timestamp = LocalDateTime.now();
    }

    public HealthDto(String status, String message) {
        this();
        this.status = status;
        this.message = message;
    }

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public DatabaseHealth getDatabase() {
        return database;
    }

    public void setDatabase(DatabaseHealth database) {
        this.database = database;
    }

    // Inner class for database health
    public static class DatabaseHealth {
        private String status;
        private Long responseTimeMs;

        public DatabaseHealth() {}

        public DatabaseHealth(String status, Long responseTimeMs) {
            this.status = status;
            this.responseTimeMs = responseTimeMs;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Long getResponseTimeMs() {
            return responseTimeMs;
        }

        public void setResponseTimeMs(Long responseTimeMs) {
            this.responseTimeMs = responseTimeMs;
        }
    }
}
