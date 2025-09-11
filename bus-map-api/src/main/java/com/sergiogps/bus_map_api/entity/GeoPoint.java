package com.sergiogps.bus_map_api.entity;

import java.util.Objects;

/**
 * Simple value object for a 2D point (x,y). Stored in PostgreSQL as type `point`.
 */
public class GeoPoint {
    private double x;
    private double y;

    public GeoPoint() {}

    public GeoPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    /**
     * Formats as PostgreSQL point literal: (x,y)
     */
    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

    public static GeoPoint fromString(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        if (trimmed.startsWith("(") && trimmed.endsWith(")")) {
            trimmed = trimmed.substring(1, trimmed.length() - 1);
        }
        String[] parts = trimmed.split(",");
        if (parts.length != 2) throw new IllegalArgumentException("Invalid point: " + value);
        double x = Double.parseDouble(parts[0].trim());
        double y = Double.parseDouble(parts[1].trim());
        return new GeoPoint(x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GeoPoint)) return false;
        GeoPoint geoPoint = (GeoPoint) o;
        return Double.compare(geoPoint.x, x) == 0 && Double.compare(geoPoint.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
