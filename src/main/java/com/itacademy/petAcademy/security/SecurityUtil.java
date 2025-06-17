package com.itacademy.petAcademy.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class SecurityUtil {

    public static boolean isAdmin(Authentication authentication) {
        if (authentication == null) {
            return false;
        }

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSubAdmin(Authentication authentication) {
        if (authentication == null) {
            return false;
        }

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_SUB_ADMIN")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNotUser(Authentication authentication) {
        if (authentication == null) {
            return false;
        }

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_USER")) {
                return false;
            }
        }
        return false;
    }

    public static boolean isUser(Authentication authentication) {
        if (authentication == null) {
            return false;
        }

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_USER")) {
                return true;
            }
        }
        return false;
    }

    // Pots combinar-ho si vols comprovar múltiples rols
    public static boolean hasAnyRole(Authentication authentication, String... roles) {
        if (authentication == null || roles == null || roles.length == 0) {
            return false;
        }

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            for (String role : roles) {
                if (authority.getAuthority().equals(role)) {
                    return true;
                }
            }
        }
        return false;
    }
}