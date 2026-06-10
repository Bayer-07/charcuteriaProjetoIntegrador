package com.example.charcuteria.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public class UserRegistrationDto {

    @NotBlank(message = "O nome não pode ser vazio.")
    @Size(min = 3, max = 100, message = "O nome deve conter entre 3 e 100 caracteres.")
    private String name;

    @NotBlank(message = "O email não pode ser vazio.")
    @Email(message = "O formato do email é inválido.")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "O email deve conter um domínio válido. Ex: '.com'")
    private String email;

    @NotBlank(message = "A senha não pode ser vazia.")
    @Size(min = 8, message = "A senha deve conter pelo menos 8 caracteres.")
    private String password;

    @NotBlank(message = "A senha não pode ser vazia.")
    @Size(min = 8, message = "A senha deve conter pelo menos 8 caracteres.")
    private String passwordControl;

    public UserRegistrationDto() {
    }

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

    public String getPasswordControl() {
        return passwordControl;
    }

    public void setPasswordControl(String passwordControl) {
        this.passwordControl = passwordControl;
    }
}
