package com.example.charcuteria.dto;

public class UserResponseDto {
    private final Integer id;
    private final String name;

    public UserResponseDto(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
}
