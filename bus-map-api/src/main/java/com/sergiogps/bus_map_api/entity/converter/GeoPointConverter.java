package com.sergiogps.bus_map_api.entity.converter;

import com.sergiogps.bus_map_api.entity.GeoPoint;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts GeoPoint <-> String to persist PostgreSQL point type using its text form.
 * Ensure the column is declared as "point" in the database.
 */
@Converter(autoApply = false)
public class GeoPointConverter implements AttributeConverter<GeoPoint, String> {

    @Override
    public String convertToDatabaseColumn(GeoPoint attribute) {
        return attribute == null ? null : attribute.toString();
    }

    @Override
    public GeoPoint convertToEntityAttribute(String dbData) {
        return dbData == null ? null : GeoPoint.fromString(dbData);
    }
}
