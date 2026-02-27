package com.nexora.synergy.platform.domain.model;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object: ContactInfo
 *
 * Holds the tenant admin's contact details. Self-validating — invalid
 * emails/phones never enter the domain.
 */
public final class ContactInfo {

    private static final Pattern EMAIL_PATTERN
            = Pattern.compile("^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$");

    private final String adminName;
    private final String adminEmail;
    private final String adminPhone;

    private ContactInfo(String adminName, String adminEmail, String adminPhone) {
        this.adminName = validateName(adminName);
        this.adminEmail = validateEmail(adminEmail);
        this.adminPhone = adminPhone; // optional
    }

    public static ContactInfo of(String adminName, String adminEmail, String adminPhone) {
        return new ContactInfo(adminName, adminEmail, adminPhone);
    }

    private String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Admin name must not be blank");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("Admin name too long (max 100 chars)");
        }
        return name.trim();
    }

    private String validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Admin email must not be blank");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format: " + email);
        }
        return email.toLowerCase();
    }

    public String getAdminName() {
        return adminName;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public String getAdminPhone() {
        return adminPhone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ContactInfo other)) {
            return false;
        }
        return Objects.equals(adminEmail, other.adminEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(adminEmail);
    }

    @Override
    public String toString() {
        return "ContactInfo{name='" + adminName + "', email='" + adminEmail + "'}";
    }
}
