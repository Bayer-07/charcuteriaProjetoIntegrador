package com.example.charcuteria.dto;

import com.example.charcuteria.enums.UserRoleEnum;

public class UserResponseDto {
    private final Integer id;
    private final String name;
    private final UserRoleEnum role;

    public UserResponseDto(Integer id, String name, UserRoleEnum role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public UserRoleEnum getRole() { return role; }
}
