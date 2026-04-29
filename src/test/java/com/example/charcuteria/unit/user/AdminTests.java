package com.example.charcuteria.unit.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.charcuteria.config.SecurityConfig;
import com.example.charcuteria.controller.user.AdminController;
import com.example.charcuteria.controller.user.UserController;
import com.example.charcuteria.dto.user.UserRegistrationDto;
import com.example.charcuteria.enums.UserRoleEnum;
import com.example.charcuteria.model.User; // Your custom entity
import com.example.charcuteria.service.user.AdminService;
import com.example.charcuteria.service.user.UserService;

@WebMvcTest({AdminController.class, UserController.class})
@Import(SecurityConfig.class)
public class AdminTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAdminRegistrationView_Authorized() throws Exception {
        mockMvc.perform(get("/registerAdmin"))
            .andExpect(status().isOk())
            .andExpect(view().name("public/adminRegistration-view"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateAdmin_Success() throws Exception {
        mockMvc.perform(post("/registerAdmin")
                .param("name", "Admin Test")
                .param("email", "admin@test.com")
                .param("password", "admin12345")
                .param("passwordControl", "admin12345"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/loginAdmin"));

        verify(userService).createUser(any(UserRegistrationDto.class), eq(UserRoleEnum.ADMIN));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAdminDashboard_AccessGranted() throws Exception {
        when(adminService.getOrderCount()).thenReturn(10);

        mockMvc.perform(get("/admin/dashboard"))
            .andExpect(status().isOk())
            .andExpect(view().name("admin/dashboardAdmin"))
            .andExpect(model().attributeExists("data"));
    }

    @Test
    void testHandleProfile_RedirectsToAdminDashboard() throws Exception {
        User adminUser = new User();
        adminUser.setEmail("admin@test.com");
        adminUser.setRole(UserRoleEnum.ADMIN);

        mockMvc.perform(get("/handleProfile")
                .with(user(adminUser)))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/dashboard"));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testAdminDashboard_ForbiddenForCustomer() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
            .andExpect(status().isForbidden());
    }
}
