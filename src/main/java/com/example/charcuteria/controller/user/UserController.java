package com.example.charcuteria.controller.user;

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
import com.example.charcuteria.enums.UserRoleEnum;
import com.example.charcuteria.exceptions.BusinessException;
import com.example.charcuteria.exceptions.ErrorCode;
import com.example.charcuteria.service.user.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Get
    @GetMapping("/teste")
    public String showTeste(Model model) {
        return "teste";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userDto", new UserRegistrationDto());
        return "user/registration-view";
    }

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
    public String handleProfile(HttpSession session, Model model) {
        UserResponseDto user = (UserResponseDto) session.getAttribute("loggedUser");
        UserResponseDto admin = (UserResponseDto) session.getAttribute("loggedAdmin");

        if (user == null && admin == null) {
            return "redirect:/login";
        }

        if (admin != null) {
            model.addAttribute("user", admin);
            return "redirect:/user/dashboardAdmin";
        }

        model.addAttribute("user", user);
        return "redirect:/user/dashboard";
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

    @GetMapping("/user/dashboardAdmin")
    public String showDashboardAdmin(HttpSession session, Model model) {
        UserResponseDto user = (UserResponseDto) session.getAttribute("loggedAdmin");

        if (user == null) {
            return "redirect:/loginAdmin";
        }

        model.addAttribute("user", user);
        return "user/dashboardAdmin";
    }

    // Post
    @PostMapping("/register")
    public String createUser(@Valid @ModelAttribute("userDto") UserRegistrationDto userDto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "user/registration-view";
        }

        try {
            if (!userDto.getPassword().equals(userDto.getPasswordControl())) throw new BusinessException(ErrorCode.DIFFERENT_PASSWORDS);
            userService.createUser(userDto, UserRoleEnum.CUSTOMER);
            return "redirect:/login";
        } catch (BusinessException ex) {
            model.addAttribute("registrationError", ex.getErrorCode().getMessage());
            return "user/registration-view";

        } catch (Exception e) {
            model.addAttribute("registrationError", "Internal server error, try again later please");
            return "user/registration-view";
        }
    }

    @PostMapping("/registerAdmin")
    public String createAdmin(@Valid @ModelAttribute("userDto") UserRegistrationDto userDto, BindingResult result, Model model) {
        if (result.hasErrors()) return "user/adminRegistration-view";

        try {
            if (!userDto.getPassword().equals(userDto.getPasswordControl())) throw new BusinessException(ErrorCode.DIFFERENT_PASSWORDS);
            userService.createUser(userDto, UserRoleEnum.ADMIN);
            return "redirect:/loginAdmin";
        } catch (BusinessException ex) {
            model.addAttribute("registrationError", ex.getErrorCode().getMessage());
            return "user/registration-view";

        } catch (Exception e) {
            model.addAttribute("registrationError", "Internal server error, try again later please");
            return "user/registration-view";
        }
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

        } catch (BusinessException ex) {
            model.addAttribute("loginError", ex.getErrorCode().getMessage());
            return "login";
        } catch (Exception e) {
            model.addAttribute("loginError", "Internal server error, try again later please");
            return "login";
        }
    }

    @PostMapping("/loginAdmin")
    public String loginAdmin(@Valid @ModelAttribute("userDto") UserLoginDto userDto, HttpSession session, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "/loginAdmin";
        }

        try {
            Optional<UserResponseDto> userOpt = userService.loginAdmin(userDto.getEmail(), userDto.getPassword());

            if (userOpt.isPresent()) {
                session.setAttribute("loggedAdmin", userOpt.get());
                return "redirect:/user/dashboardAdmin";
            } else {
                model.addAttribute("loginError", "Email or passoword incorrect");
                return "loginAdmin";
            }

        } catch (BusinessException ex) {
            model.addAttribute("registrationError", ex.getErrorCode().getMessage());
            return "loginAdmin";

        } catch (Exception e) {
            model.addAttribute("registrationError", "Internal server error, try again later please");
            return "loginAdmin";
        }
    }

}
