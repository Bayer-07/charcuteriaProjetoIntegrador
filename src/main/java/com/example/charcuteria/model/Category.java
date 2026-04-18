package com.example.charcuteria.model;

public class Category {
    private Integer id;
    private String name;
    private String description;

    public Category() {}

    public Category(Integer id, String name, String description) {
        this.name = name;
        this.id = id;
        this.description = description;
    }

    public Category(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
