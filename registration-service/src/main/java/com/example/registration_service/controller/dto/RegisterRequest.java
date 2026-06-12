package com.example.registration_service.controller.dto;

import com.example.registration_service.domain.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public class RegisterRequest {
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Username must only contain alphanumeric characters")
    private String username;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be a valid format")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 4, max = 50, message = "Password must be at least 4 characters")
    private String password;

    @NotNull(message = "Role is required")
    private Role role;

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
