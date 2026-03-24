package com.example.charcuteria.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.charcuteria.dto.UserLoginDto;
import com.example.charcuteria.dto.UserRegistrationDto;
import com.example.charcuteria.dto.UserResponseDto;
import com.example.charcuteria.service.UserService;

import jakarta.servlet.http.HttpSession;
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
            return "user/registration-view";
        }

        try {
            userService.createUser(userDto);
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("registrationError", "Ocorreu um erro ao criar usuário");
            return "user/registration-view";
        }
    }

    // esse é o q retorna a pagina de login, muda pro endpoint que quiser @bayer
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("userDto", new UserLoginDto());
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@Valid @ModelAttribute("userDto") UserLoginDto userDto, HttpSession session, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "/login";
        }

        try {
            Optional<UserResponseDto> userOpt = userService.loginUser(userDto.getEmail(), userDto.getPassword());

            if (userOpt.isPresent()) {
                session.setAttribute("loggedUser", userOpt.get());
                return "redirect:/user/dashboard";
            } else {
                model.addAttribute("loginError", "Email or password incorrect");
                return "login";
            }

        } catch (Exception e) {
            model.addAttribute("loginError", "Internal server error, try again later please");
            return "login";
        }
    }


    @GetMapping("/index")
    public String showLoginForm() {
        return "index";
    }

    @GetMapping("/user/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        UserResponseDto user = (UserResponseDto) session.getAttribute("loggedUser");

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        return "user/dashboard";
    }
}
