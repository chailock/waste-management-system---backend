package com.municipality.waste.entity;

public enum Role {
    /** Oversees every municipality (Business) on the platform; not tied to any single one. */
    SUPER_ADMIN,
    /** Full control within their own municipality. */
    ADMIN,
    /** Day-to-day operations within their own municipality. */
    MANAGER
}
