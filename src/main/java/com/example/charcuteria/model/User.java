package com.example.charcuteria.model;

import java.security.Timestamp;

import com.example.charcuteria.enums.UserRoleEnum;

public class User {
    private Integer id;
    private String name;
    private String email;
    private String password;
    private UserRoleEnum role;
    private Timestamp created_at;

    public User() {}

    public User(String name, String email, String password, UserRoleEnum role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public User(Integer id, String name, String email, String password, UserRoleEnum role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public User(Integer id, String name, String email, String password, UserRoleEnum role, Timestamp created_at) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.created_at = created_at;
    }

    public Integer getId() {
        return id;
    }

    public User setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPasswordHash() {
        return password;
    }

    public User setPasswordHash(String password) {
        this.password = password;
        return this;
    }

    public UserRoleEnum getRole() {
        return role;
    }

    public User setRole(UserRoleEnum role) {
        this.role = role;
        return this;
    }

    public Timestamp getCreatedAt() {
        return created_at;
    }

    public User setCreatedAt(Timestamp created_at) {
        this.created_at = created_at;
        return this;
    }

}
