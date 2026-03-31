package com.sergiogps.bus_map_api.dto;

import java.util.List;

public class AuthResponse {
    private String token;
    private List<String> rol;
    
    public AuthResponse() {}
    
    public AuthResponse(String token) { 
        this.token = token; 
    }
    
    public AuthResponse(String token, List<String> rol) { 
        this.token = token;
        this.rol = rol;
    }
    
    public String getToken() { 
        return token; 
    }
    
    public List<String> getRol() { 
        return rol; 
    }
    
}
