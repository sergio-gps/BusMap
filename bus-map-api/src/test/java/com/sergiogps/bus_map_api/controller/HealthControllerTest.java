package com.sergiogps.bus_map_api.controller;

import com.sergiogps.bus_map_api.dto.HealthDto;
import com.sergiogps.bus_map_api.service.HealthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HealthController.class)
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HealthService healthService;

    @Test
    void pingEndpointShouldReturnPong() throws Exception {
        mockMvc.perform(get("/api/health/ping"))
                .andExpect(status().isOk())
                .andExpect(content().string("pong"));
    }

    @Test
    void healthEndpointShouldReturnUpStatus() throws Exception {
        // Given
        HealthDto healthDto = new HealthDto("UP", "Bus Map API is running successfully");
        healthDto.setVersion("1.0.0");
        
        when(healthService.getHealthStatus()).thenReturn(healthDto);

        // When & Then
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.message").value("Bus Map API is running successfully"))
                .andExpect(jsonPath("$.version").value("1.0.0"));
    }

    @Test
    void simpleHealthEndpointShouldReturnStatus() throws Exception {
        // Given
        when(healthService.getSimpleStatus()).thenReturn("UP");

        // When & Then
        mockMvc.perform(get("/api/health/status"))
                .andExpect(status().isOk())
                .andExpect(content().string("UP"));
    }
}
