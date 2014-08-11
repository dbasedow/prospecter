package de.danielbasedow.prospecter.core.index;

/**
 * Fields that are implemented as FieldIndex and in DocumentBuilder
 */
public enum FieldType {
    FULL_TEXT,
    INTEGER,
    GEO_DISTANCE,
    DATE_TIME,
    LONG,
    DOUBLE,
    STRING
}
