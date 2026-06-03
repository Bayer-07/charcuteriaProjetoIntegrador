    package com.example.charcuteria.controller.partner;

    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.validation.BindingResult;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.ModelAttribute;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.ResponseBody;

    import com.example.charcuteria.dto.partner.PartnerRequest;
    import com.example.charcuteria.service.partner.EmailService;
    import com.example.charcuteria.service.partner.PartnerService;
    import org.springframework.web.servlet.mvc.support.RedirectAttributes;

    import jakarta.validation.Valid;

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
            return "public/partners";
        }

        @PostMapping
        public String enviar(@Valid @ModelAttribute PartnerRequest request,
                BindingResult result,
                RedirectAttributes redirectAttributes) {

            if (result.hasErrors()) {
                return "partners/partners";
            }

            try {
                service.sendForm(request);
                redirectAttributes.addFlashAttribute("success", "Mensagem enviada!");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Erro ao enviar.");
            }

            return "redirect:/partners";
        }

        @GetMapping("/teste-email")
        @ResponseBody
        public String testEmail() {
            PartnerRequest dto = new PartnerRequest();
            dto.setName("Teste LTDA");
            dto.setCnpj("00.000.000/0001-00");
            dto.setResponsible("João da Silva");
            dto.setEmail("seuemail@gmail.com");
            dto.setPhone("(41) 99999-9999");
            dto.setMessage("Teste de envio de email funcionando!");

            emailService.sendPartnerEmail(dto);

            return "Email enviado!";
        }
    }
