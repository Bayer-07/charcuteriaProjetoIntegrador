package com.example.charcuteria.unit.address;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.example.charcuteria.config.SecurityConfig;
import com.example.charcuteria.controller.address.AddressController;
import com.example.charcuteria.dto.address.AddressDtoRequest;
import com.example.charcuteria.enums.UserRoleEnum;
import com.example.charcuteria.model.User;
import com.example.charcuteria.model.Address;
import com.example.charcuteria.service.address.AddressService;

@WebMvcTest(AddressController.class)
@Import(SecurityConfig.class)
public class AddressControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AddressService addressService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1);
        mockUser.setEmail("test@gmail.com");
        mockUser.setRole(UserRoleEnum.CUSTOMER);
    }

    @Test
    void testListAddresses_NotLogged() throws Exception {
        mockMvc.perform(get("/addresses"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    void testListAddresses_Logged() throws Exception {
        mockMvc.perform(get("/addresses")
                .with(user(mockUser)))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/user/dashboard"));
    }

    @Test
    void testCreateAddress_NotLogged() throws Exception {
        mockMvc.perform(post("/addresses").with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    void testCreateAddress_Success() throws Exception {
        mockMvc.perform(post("/addresses")
                .with(user(mockUser))
                .with(csrf())
                .param("street", "Rua Teste"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/user/dashboard"))
            .andExpect(flash().attribute("successMessage", "Endereço criado com sucesso!"));

        verify(addressService).createAddress(any(AddressDtoRequest.class));
    }

    @Test
    void testCreateAddress_SuccessWithRedirectTo() throws Exception {
        mockMvc.perform(post("/addresses")
                .with(user(mockUser))
                .with(csrf())
                .param("redirectTo", "/checkout")
                .param("street", "Rua Teste"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/checkout"))
            .andExpect(flash().attribute("successMessage", "Endereço criado com sucesso!"));
    }

    @Test
    void testCreateAddress_InvalidRedirectTo() throws Exception {
        mockMvc.perform(post("/addresses")
                .with(user(mockUser))
                .with(csrf())
                .param("redirectTo", "http://malicious-site.com")
                .param("street", "Rua Teste"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/user/dashboard"));
    }

    @Test
    void testCreateAddress_InternalServerError() throws Exception {
        doThrow(new RuntimeException("Database down"))
            .when(addressService).createAddress(any(AddressDtoRequest.class));

        mockMvc.perform(post("/addresses")
                .with(user(mockUser))
                .with(csrf())
                .param("street", "Rua Teste"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/user/dashboard"))
            .andExpect(flash().attribute("errorMessage", "Erro ao criar endereço: Database down"));
    }

    @Test
    void testUpdateAddress_NotLogged() throws Exception {
        mockMvc.perform(post("/addresses/1/edit").with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    void testUpdateAddress_NotFoundOrNoPermission() throws Exception {
        Address foreignAddress = new Address();
        foreignAddress.setUSerId(99); 

        when(addressService.getAddressById(1)).thenReturn(Optional.of(foreignAddress));

        mockMvc.perform(post("/addresses/1/edit")
                .with(user(mockUser))
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/user/dashboard"))
            .andExpect(flash().attribute("errorMessage", "Endereço não encontrado ou você não tem permissão"));
    }

    @Test
    void testUpdateAddress_Success() throws Exception {
        Address currentAddress = new Address();
        currentAddress.setUSerId(mockUser.getId());

        when(addressService.getAddressById(1)).thenReturn(Optional.of(currentAddress));

        mockMvc.perform(post("/addresses/1/edit")
                .with(user(mockUser))
                .with(csrf())
                .param("street", "Rua Atualizada"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/user/dashboard"))
            .andExpect(flash().attribute("successMessage", "Endereço atualizado com sucesso!"));

        verify(addressService).updateAddress(eq(1), any(AddressDtoRequest.class));
    }

    @Test
    void testUpdateAddress_InternalServerError() throws Exception {
        Address currentAddress = new Address();
        currentAddress.setUSerId(mockUser.getId());

        when(addressService.getAddressById(1)).thenReturn(Optional.of(currentAddress));
        doThrow(new RuntimeException("Update failed"))
            .when(addressService).updateAddress(eq(1), any(AddressDtoRequest.class));

        mockMvc.perform(post("/addresses/1/edit")
                .with(user(mockUser))
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/addresses/1/edit"))
            .andExpect(flash().attribute("errorMessage", "Erro ao atualizar endereço: Update failed"));
    }

    @Test
    void testSetDefaultAddress_Success() throws Exception {
        Address currentAddress = new Address();
        currentAddress.setUSerId(mockUser.getId());

        when(addressService.getAddressById(1)).thenReturn(Optional.of(currentAddress));

        mockMvc.perform(post("/addresses/1/default")
                .with(user(mockUser))
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/user/dashboard"))
            .andExpect(flash().attribute("successMessage", "Endereço padrão atualizado com sucesso!"));

        verify(addressService).setDefaultAddress(1, mockUser.getId());
    }

    @Test
    void testDeleteAddress_Success() throws Exception {
        Address currentAddress = new Address();
        currentAddress.setUSerId(mockUser.getId());

        when(addressService.getAddressById(1)).thenReturn(Optional.of(currentAddress));

        mockMvc.perform(post("/addresses/1/delete")
                .with(user(mockUser))
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/user/dashboard"))
            .andExpect(flash().attribute("successMessage", "Endereço deletado com sucesso!"));

        verify(addressService).deleteAddress(1);
    }
}