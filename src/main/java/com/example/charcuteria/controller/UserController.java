package com.example.charcuteria.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.charcuteria.model.dto.UserRegistrationDto;

@Controller
@RequestMapping("/user")
public class UserController {

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userDto", new UserRegistrationDto());
        return "user/registration-view";
    }

    @PostMapping("/register")
    public String createUser(@ModelAttribute("userDto") UserRegistrationDto userDto) {
        // qualquer path para "redirect:/user/error" vai ser mudado pra exceptions dps
        if (userDto.getName() == null || userDto.getEmail() == null || userDto.getPassword() == null) return "redirect:/user/error";

        return "redirect:/user/success";
    }
}
