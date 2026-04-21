package com.example.charcuteria.dto.partner;

public class PartnerResponse {
    private String id;
    private String name;
    private String cnpj;
    private String responsible;
    private String email;
    private String phone;
    private String message;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCnpj() {
        return cnpj;
    }

    public String getResponsible() {
        return responsible;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getMessage() {
        return message;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public void setResponsible(String responsible) {
        this.responsible = responsible;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
