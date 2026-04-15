package com.example.charcuteria.controller.user;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.charcuteria.dto.user.UserLoginDto;
import com.example.charcuteria.dto.user.UserRegistrationDto;
import com.example.charcuteria.enums.UserRoleEnum;
import com.example.charcuteria.exceptions.BusinessException;
import com.example.charcuteria.exceptions.UserErrorCode;
import com.example.charcuteria.model.User;
import com.example.charcuteria.service.user.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Get
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userDto", new UserRegistrationDto());
        return "user/registration-view";
    }

    // vai pra rota privada pra admin quando acabarem os testes
    @GetMapping("/registerAdmin")
    public String showAdminRegistrationAdmin(Model model) {
        model.addAttribute("userDto", new UserRegistrationDto());
        return "user/adminRegistration-view";
    }

    @GetMapping("/login")
    public String showUserLoginForm(Model model) {
        model.addAttribute("userDto", new UserLoginDto());
        return "login";
    }

    @GetMapping("/loginAdmin")
    public String showAdminLoginForm(Model model) {
        model.addAttribute("userDto", new UserLoginDto());
        return "loginAdmin";
    }

    @GetMapping("/handleProfile")
    public String handleProfile(@AuthenticationPrincipal User loggedUser) {
        if (loggedUser == null) {
            return "redirect:/login";
        }

        boolean isAdmin = loggedUser.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return "redirect:/admin/dashboard";
        }

        return "redirect:/user/dashboard";
    }

    @GetMapping("/user/dashboard")
    public String showDashboard(@AuthenticationPrincipal User loggedUser, Model model) {
        model.addAttribute("user", loggedUser);
        return "user/dashboard";
    }

    // Post
    @PostMapping("/register")
    public String createUser(@Valid @ModelAttribute("userDto") UserRegistrationDto userDto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "user/registration-view";
        }

        try {
            if (!userDto.getPassword().equals(userDto.getPasswordControl())) throw new BusinessException(UserErrorCode.DIFFERENT_PASSWORDS);
            userService.createUser(userDto, UserRoleEnum.CUSTOMER);
            return "redirect:/login";
        } catch (BusinessException ex) {
            model.addAttribute("registrationError", ex.getUserErrorCode().getMessage());
            return "user/registration-view";

        } catch (Exception e) {
            model.addAttribute("registrationError", "Internal server error, try again later please");
            return "user/registration-view";
        }
    }

    // vai pra rota privada pra admin quando acabarem os testes
    @PostMapping("/registerAdmin")
    public String createAdmin(@Valid @ModelAttribute("userDto") UserRegistrationDto userDto, BindingResult result, Model model) {
        if (result.hasErrors()) return "user/adminRegistration-view";

        try {
            if (!userDto.getPassword().equals(userDto.getPasswordControl())) throw new BusinessException(UserErrorCode.DIFFERENT_PASSWORDS);
            userService.createUser(userDto, UserRoleEnum.ADMIN);
            return "redirect:/loginAdmin";
        } catch (BusinessException ex) {
            model.addAttribute("registrationError", ex.getUserErrorCode().getMessage());
            return "user/registration-view";

        } catch (Exception e) {
            model.addAttribute("registrationError", "Internal server error, try again later please");
            return "user/registration-view";
        }
    }

}
