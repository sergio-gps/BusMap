package com.sergiogps.bus_map_api.dto;

public class AuthResponse {
    private String token;
    public AuthResponse() {}
    public AuthResponse(String token) { this.token = token; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
