package com.sergiogps.bus_map_api.dto;

public class RutaRequest {
    private double latOrigen;
    private double lonOrigen;
    private double latDestino;
    private double lonDestino;

    public double getLatOrigen() {
        return latOrigen;
    }
    public void setLatOrigen(double latOrigen) {
        this.latOrigen = latOrigen;
    }
    public double getLonOrigen() {
        return lonOrigen;
    }
    public void setLonOrigen(double lonOrigen) {
        this.lonOrigen = lonOrigen;
    }
    public double getLatDestino() {
        return latDestino;
    }
    public void setLatDestino(double latDestino) {
        this.latDestino = latDestino;
    }
    public double getLonDestino() {
        return lonDestino;
    }
    public void setLonDestino(double lonDestino) {
        this.lonDestino = lonDestino;
    }
}