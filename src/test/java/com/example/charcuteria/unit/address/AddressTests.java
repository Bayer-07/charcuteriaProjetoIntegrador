package com.example.charcuteria.unit.address;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import com.example.charcuteria.config.SecurityConfig;
import com.example.charcuteria.controller.address.AddressController;
import com.example.charcuteria.enums.UserRoleEnum;
import com.example.charcuteria.model.Address;
import com.example.charcuteria.model.User;
import com.example.charcuteria.service.address.AddressService;

@WebMvcTest(AddressController.class)
@Import(SecurityConfig.class)
public class AddressTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AddressService addressService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setEmail("user@test.com");
        testUser.setRole(UserRoleEnum.ADMIN);
    }

    @Test
    void testListAddresses_Authenticated() throws Exception {
        when(addressService.getAddressesByUserId(1)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/addresses")
                .with(user(testUser)))
            .andExpect(status().isOk())
            .andExpect(view().name("user/dashboard"))
            .andExpect(model().attributeExists("addresses"))
            .andExpect(model().attribute("userEmail", "user@test.com"));
    }

    @Test
    void testCreateAddress_Success() throws Exception {
        mockMvc.perform(post("/addresses")
                .with(user(testUser))
                .param("street", "Rua Teste")
                .param("number", "123")
                .param("city", "Toledo")
                .param("state", "PR")
                .param("zipCode", "85900-000"))
            .andExpect(status().is3xxRedirection())
            .andExpect(flash().attributeExists("successMessage"))
            .andExpect(redirectedUrl("/user/dashboard"));

        verify(addressService).createAddress(any());
    }

    @Test
    void testShowEditForm_Authorized() throws Exception {
        Address address = new Address();
        address.setId(10);
        address.setUSerId(1);
        address.setStreet("Rua Original");

        when(addressService.getAddressById(10)).thenReturn(Optional.of(address));

        mockMvc.perform(get("/addresses/10/edit")
                .with(user(testUser)))
            .andExpect(status().isOk())
            .andExpect(view().name("address/address-form"))
            .andExpect(model().attributeExists("addressDto"));
    }

    @Test
    void testUpdateAddress_ForbiddenForDifferentUser() throws Exception {
        Address addressOfAnotherUser = new Address();
        addressOfAnotherUser.setId(20);
        addressOfAnotherUser.setUSerId(99);

        when(addressService.getAddressById(20)).thenReturn(Optional.of(addressOfAnotherUser));

        mockMvc.perform(post("/addresses/20/edit")
                .with(user(testUser))
                .param("street", "Tentativa Hack"))
            .andExpect(status().is3xxRedirection())
            .andExpect(flash().attribute("errorMessage", "Endereço não encontrado ou você não tem permissão"))
            .andExpect(redirectedUrl("/addresses"));
    }

    @Test
    void testDeleteAddress_Success() throws Exception {
        Address address = new Address();
        address.setId(10);
        address.setUSerId(1);

        when(addressService.getAddressById(10)).thenReturn(Optional.of(address));

        mockMvc.perform(post("/addresses/10/delete")
                .with(user(testUser)))
            .andExpect(status().is3xxRedirection())
            .andExpect(flash().attribute("successMessage", "Endereço deletado com sucesso!"))
            .andExpect(redirectedUrl("/user/dashboard"));

        verify(addressService).deleteAddress(10);
    }
}
