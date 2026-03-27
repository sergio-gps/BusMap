package com.sergiogps.bus_map_api.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public class AuthRequest {
    @JsonAlias({"username", "email"})
    private String email;
    private String password;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    // Compatibilidad temporal con clientes que siguen enviando/leyendo "username".
    public String getUsername() { return email; }
    public void setUsername(String username) { this.email = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
