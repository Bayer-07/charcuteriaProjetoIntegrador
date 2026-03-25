package com.example.charcuteria.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRegistrationDto {

    @NotBlank(message = "The name cannot be empty")
    @Size(min = 3, max = 100, message = "The name has to be between 3 and 100 characteres")
    private String name;

    @NotBlank(message = "The email cannot be empty")
    @Email(message = "The email format its invalid")
    private String email;

    @NotBlank(message = "The password cannot be empty")
    @Size(min = 8, message = "The password has to be, at least 8 characteres")
    private String password;

    public UserRegistrationDto() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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
