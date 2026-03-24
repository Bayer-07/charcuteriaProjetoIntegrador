package com.example.charcuteria.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserLoginDto {
    @NotBlank(message = "The email cannot be empty")
    @Email(message = "The email format its invalid")
    private String email;

    @NotBlank(message = "The password cannot be empty")
    private String password;

    public UserLoginDto() {}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
