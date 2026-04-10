package com.example.charcuteria.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.charcuteria.dto.address.AddressDtoRequest;
import com.example.charcuteria.model.Address;
import com.example.charcuteria.model.User;
import com.example.charcuteria.service.AddressService;

@Controller
@RequestMapping("/addresses")
public class AddressController {

    private final AddressService addressService;
    
    @Autowired
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    public String listAddresses(@AuthenticationPrincipal User loggedUser, Model model) {
        if (loggedUser == null) {
            return "redirect:/login";
        }
        
        List<Address> addresses = addressService.getAddressesByUserId(loggedUser.getId());
        model.addAttribute("addresses", addresses);
        model.addAttribute("userEmail", loggedUser.getEmail());
        
        return "address/addresses-list";
    }

    @GetMapping("/new")
    public String showCreateForm(@AuthenticationPrincipal User loggedUser, Model model) {
        if (loggedUser == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("addressDto", new AddressDtoRequest());
        model.addAttribute("userId", loggedUser.getId());
        
        return "address/address-form";
    }

    @PostMapping
    public String createAddress(
            @ModelAttribute AddressDtoRequest addressDto,
            @AuthenticationPrincipal User loggedUser,
            RedirectAttributes redirectAttributes) {
        
        if (loggedUser == null) {
            return "redirect:/login";
        }
        
        try {
            addressDto.setUserId(loggedUser.getId());
            addressService.createAddress(addressDto);
            redirectAttributes.addFlashAttribute("successMessage", "Endereço criado com sucesso!");
            return "redirect:/addresses";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao criar endereço: " + e.getMessage());
            return "redirect:/addresses/new";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(
            @PathVariable Integer id,
            @AuthenticationPrincipal User loggedUser,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        if (loggedUser == null) {
            return "redirect:/login";
        }
        
        var address = addressService.getAddressById(id);
        
        if (address.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Endereço não encontrado");
            return "redirect:/addresses";
        }
        
        Address foundAddress = address.get();
        if (!foundAddress.getUserId().equals(loggedUser.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Você não tem permissão para editar este endereço");
            return "redirect:/addresses";
        }
        
        model.addAttribute("address", foundAddress);
        AddressDtoRequest dto = new AddressDtoRequest();
        dto.setUserId(foundAddress.getUserId());
        dto.setStreet(foundAddress.getStreet());
        dto.setNumber(foundAddress.getNumber());
        dto.setComplement(foundAddress.getComplement());
        dto.setNeighborhood(foundAddress.getNeighborhood());
        dto.setCity(foundAddress.getCity());
        dto.setState(foundAddress.getState());
        dto.setZipCode(foundAddress.getZipCode());
        
        model.addAttribute("addressDto", dto);
        model.addAttribute("addressId", id);
        
        return "address/address-form";
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
                return "redirect:/addresses";
            }
            
            addressDto.setUserId(loggedUser.getId());
            addressService.updateAddress(id, addressDto);
            redirectAttributes.addFlashAttribute("successMessage", "Endereço atualizado com sucesso!");
            return "redirect:/addresses";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao atualizar endereço: " + e.getMessage());
            return "redirect:/addresses/" + id + "/edit";
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
                return "redirect:/addresses";
            }
            
            addressService.deleteAddress(id);
            redirectAttributes.addFlashAttribute("successMessage", "Endereço deletado com sucesso!");
            return "redirect:/addresses";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao deletar endereço: " + e.getMessage());
            return "redirect:/addresses";
        }
    }
}
