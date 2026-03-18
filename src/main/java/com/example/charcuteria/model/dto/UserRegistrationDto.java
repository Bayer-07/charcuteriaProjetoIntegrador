package com.example.charcuteria.model.dto;

import com.example.charcuteria.model.enums.UserRoleEnum;

public class UserRegistrationDto {
    private String name;
    private String email;
    private String password;
    private UserRoleEnum role;

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

    public UserRoleEnum getRole() {
        return role;
    }

    public void setRole(UserRoleEnum role) {
        this.role = role;
    }
}
