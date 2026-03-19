package com.example.charcuteria.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.charcuteria.dto.UserRegistrationDto;
import com.example.charcuteria.service.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userDto", new UserRegistrationDto());
        return "user/registration-view";
    }

    @PostMapping("/register")
    public String createUser(@Valid @ModelAttribute("userDto") UserRegistrationDto userDto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            System.out.println("ERROR: " + result.getAllErrors());
            return "user/registration-view";
        }

        try {
            userService.createUser(userDto);
            return "redirect:/user/success";
        } catch (Exception e) {
            System.out.println("ERROR: " + result.getAllErrors());
        }

        return "redirect:/user/success";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

}
