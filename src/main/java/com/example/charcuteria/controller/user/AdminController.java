package com.example.charcuteria.controller.user;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.charcuteria.model.User;


@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/dashboard")
    public String showDashboard(@AuthenticationPrincipal User loggedUser, Model model) {
        // pegar total produtos
        // pegar receita do mês atual
        // pegar assinantes ativos atuais
        // produtos com estoque abaixo de X
        // colocar isso em um DTO de response
        // retornar o DTO
        // mudar o html e css depois
        model.addAttribute("user", loggedUser);
        return "user/dashboardAdmin";
    }
}
