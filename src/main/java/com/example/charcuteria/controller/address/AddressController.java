package com.example.charcuteria.controller.address;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.charcuteria.dto.address.AddressDtoRequest;
import com.example.charcuteria.model.Address;
import com.example.charcuteria.model.User;
import com.example.charcuteria.service.address.AddressService;

@Controller
@RequestMapping("/addresses")
public class AddressController {

    private final AddressService addressService;

    @Autowired
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    public String listAddresses(@AuthenticationPrincipal User loggedUser) {
        if (loggedUser == null) {
            return "redirect:/login";
        }

        return "redirect:/user/dashboard";
    }

    @PostMapping
    public String createAddress(
            @ModelAttribute AddressDtoRequest addressDto,
            @RequestParam(name = "redirectTo", required = false) String redirectTo,
            @AuthenticationPrincipal User loggedUser,
            RedirectAttributes redirectAttributes) {

        if (loggedUser == null) {
            return "redirect:/login";
        }

        try {
            addressDto.setUserId(loggedUser.getId());
            addressService.createAddress(addressDto);
            redirectAttributes.addFlashAttribute("successMessage", "Endereço criado com sucesso!");
            return "redirect:" + resolveRedirectPath(redirectTo, "/user/dashboard");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao criar endereço: " + e.getMessage());
            return "redirect:" + resolveRedirectPath(redirectTo, "/user/dashboard");
        }   
    }

    private String resolveRedirectPath(String redirectTo, String fallback) {
        if (redirectTo == null || redirectTo.isBlank()) {
            return fallback;
        }

        String trimmedPath = redirectTo.trim();
        if (!trimmedPath.startsWith("/") || trimmedPath.startsWith("//")) {
            return fallback;
        }

        return trimmedPath;
    }

    @PostMapping("/{id}/edit")
    public String updateAddress(
            @PathVariable Integer id,
            @ModelAttribute AddressDtoRequest addressDto,
            @AuthenticationPrincipal User loggedUser,
            RedirectAttributes redirectAttributes) {

        if (loggedUser == null) {
            return "redirect:/login";
        }

        try {
            var address = addressService.getAddressById(id);

            if (address.isEmpty() || !address.get().getUserId().equals(loggedUser.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Endereço não encontrado ou você não tem permissão");
                return "redirect:/user/dashboard";
            }

            addressDto.setUserId(loggedUser.getId());
            addressService.updateAddress(id, addressDto);
            redirectAttributes.addFlashAttribute("successMessage", "Endereço atualizado com sucesso!");
            return "redirect:/user/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao atualizar endereço: " + e.getMessage());
            return "redirect:/addresses/" + id + "/edit";
        }
    }

    @PostMapping("/{id}/default")
    public String setDefaultAddress(
            @PathVariable Integer id,
            @AuthenticationPrincipal User loggedUser,
            RedirectAttributes redirectAttributes) {

        if (loggedUser == null) {
            return "redirect:/login";
        }

        try {
            var address = addressService.getAddressById(id);

            if (address.isEmpty() || !address.get().getUserId().equals(loggedUser.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Endereço não encontrado ou você não tem permissão");
                return "redirect:/user/dashboard";
            }

            addressService.setDefaultAddress(id, loggedUser.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Endereço padrão atualizado com sucesso!");
            return "redirect:/user/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao atualizar endereço padrão: " + e.getMessage());
            return "redirect:/user/dashboard";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteAddress(
            @PathVariable Integer id,
            @AuthenticationPrincipal User loggedUser,
            RedirectAttributes redirectAttributes) {

        if (loggedUser == null) {
            return "redirect:/login";
        }

        try {
            var address = addressService.getAddressById(id);

            if (address.isEmpty() || !address.get().getUserId().equals(loggedUser.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Endereço não encontrado ou você não tem permissão");
                return "redirect:/user/dashboard";
            }

            addressService.deleteAddress(id);
            redirectAttributes.addFlashAttribute("successMessage", "Endereço deletado com sucesso!");
            return "redirect:/user/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao deletar endereço: " + e.getMessage());
            return "redirect:/user/dashboard";
        }
    }
}
