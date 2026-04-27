package com.example.charcuteria.controller.partner;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.charcuteria.dto.partner.PartnerRequest;
import com.example.charcuteria.service.partner.EmailService;
import com.example.charcuteria.service.partner.PartnerService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@Controller
@RequestMapping("/partners")
public class PartnerController {
    private final PartnerService service;
    private final EmailService emailService;

    public PartnerController(PartnerService service, EmailService emailService) {
        this.service = service;
        this.emailService = emailService;
    }

    @GetMapping
    public String form(Model model) {
        model.addAttribute("partner", new PartnerRequest());
        return "partners/partners";
    }

    @PostMapping
    public String enviar(@ModelAttribute PartnerRequest request) {
        service.sendForm(request);
        return "redirect:/partners?success";
    }
}
