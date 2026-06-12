package com.example.charcuteria.unit.category;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.example.charcuteria.config.SecurityConfig;
import com.example.charcuteria.controller.category.CategoryController;
import com.example.charcuteria.dto.category.CategoryEditResponseDto;
import com.example.charcuteria.enums.UserRoleEnum;
import com.example.charcuteria.model.User;
import com.example.charcuteria.service.category.CategoryService;

@WebMvcTest(CategoryController.class)
@Import(SecurityConfig.class)
public class CategoryControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private User adminUser;

    @BeforeEach
    void setUp() {
        adminUser = new User();
        adminUser.setId(1);
        adminUser.setEmail("admin@charcuteria.com");
        adminUser.setRole(UserRoleEnum.ADMIN);
    }

    @Test
    void testGetById_Success() throws Exception {
        CategoryEditResponseDto responseDto = new CategoryEditResponseDto("Defumados", "Produtos artesanais defumados");
        when(categoryService.getById(1)).thenReturn(responseDto);

        mockMvc.perform(get("/admin/categories/1")
                .with(user(adminUser))
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Defumados"))
                .andExpect(jsonPath("$.description").value("Produtos artesanais defumados"));

        verify(categoryService).getById(1);
    }

    @Test
    void testGetById_NotFound_ThrowsException() throws Exception {
        when(categoryService.getById(1)).thenReturn(null);

        org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () -> {
            mockMvc.perform(get("/admin/categories/1")
                    .with(user(adminUser)));
        });
    }

    @Test
    void testUpdate_Success() throws Exception {
        mockMvc.perform(post("/admin/categories/update")
                .with(user(adminUser))
                .with(csrf())
                .param("id", "1")
                .param("name", "Embutidos Atualizados"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/products?type=categories"));

        verify(categoryService).updateCategoryById(any());
    }

    @Test
    void testCreateCategory_Success() throws Exception {
        mockMvc.perform(post("/admin/categories/create")
                .with(user(adminUser))
                .with(csrf())
                .param("name", "Nova Categoria"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/products?type=categories"));

        verify(categoryService).createCategory(any());
    }

    @Test
    void testCreateCategory_Exception_RedirectsToProducts() throws Exception {
        doThrow(new RuntimeException("Erro ao salvar"))
                .when(categoryService).createCategory(any());

        mockMvc.perform(post("/admin/categories/create")
                .with(user(adminUser))
                .with(csrf())
                .param("name", "Nova Categoria"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/products?type=products"));
    }

    @Test
    void testDeleteById_Success() throws Exception {
        mockMvc.perform(post("/admin/categories/delete/1")
                .with(user(adminUser))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/products?type=categories"));

        verify(categoryService).deleteById(1);
    }

    @Test
    void testDeleteById_Exception_RedirectsToProducts() throws Exception {
        doThrow(new RuntimeException("Categoria possui produtos vinculados"))
                .when(categoryService).deleteById(1);

        mockMvc.perform(post("/admin/categories/delete/1")
                .with(user(adminUser))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/products?type=products"));
    }
}