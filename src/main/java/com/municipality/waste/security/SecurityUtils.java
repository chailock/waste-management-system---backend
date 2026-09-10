package com.municipality.waste.security;

import com.municipality.waste.exception.BadRequestException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Convenience accessor for the authenticated user's tenant (Business) so
 * every service can scope its queries without repeating boilerplate.
 *
 * SUPER_ADMIN users are not tied to a single business. When a SUPER_ADMIN
 * is "viewing as" a chosen municipality, the frontend sends that choice on
 * every request via the X-Business-Id header, and currentBusinessId() below
 * honors it. This means every existing business-scoped service (Site,
 * Vehicle, Route, etc.) works for the super-admin "view as" feature with no
 * changes of its own — only this class needed to change.
 */
public final class SecurityUtils {

    public static final String BUSINESS_HEADER = "X-Business-Id";

    private SecurityUtils() {
    }

    public static UserPrincipal currentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal;
        }
        throw new IllegalStateException("No authenticated user in security context");
    }

    public static boolean isSuperAdmin() {
        return "SUPER_ADMIN".equals(currentUser().getRole());
    }

    public static Long currentBusinessId() {
        UserPrincipal user = currentUser();

        if (isSuperAdmin()) {
            Long override = businessHeaderValue();
            if (override == null) {
                throw new BadRequestException(
                        "Select a municipality first (missing " + BUSINESS_HEADER + " header)");
            }
            return override;
        }

        if (user.getBusinessId() == null) {
            throw new IllegalStateException("User " + user.getUsername() + " has no business assigned");
        }
        return user.getBusinessId();
    }

    private static Long businessHeaderValue() {
        var attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return null;
        }
        String header = attrs.getRequest().getHeader(BUSINESS_HEADER);
        if (header == null || header.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(header.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
