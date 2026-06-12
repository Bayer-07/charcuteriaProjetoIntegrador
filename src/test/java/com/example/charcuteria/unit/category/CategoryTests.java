package com.example.charcuteria.unit.category;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.charcuteria.config.SecurityConfig;
import com.example.charcuteria.controller.category.CategoryController;
import com.example.charcuteria.dto.category.CategoryEditResponseDto;
import com.example.charcuteria.service.category.CategoryService;

@WebMvcTest(CategoryController.class)
@Import(SecurityConfig.class)
public class CategoryTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetCategoryById_ReturnsJson() throws Exception {
        CategoryEditResponseDto response = new CategoryEditResponseDto("Defumados", "Produtos defumados artesanalmente");

        when(categoryService.getById(1)).thenReturn(response);

        mockMvc.perform(get("/admin/categories/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Defumados"))
            .andExpect(jsonPath("$.description").value("Produtos defumados artesanalmente"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateCategory_Success() throws Exception {
        mockMvc.perform(post("/admin/categories/create")
                .with(csrf()) // Adicionado token de segurança
                .param("name", "Nova Categoria")
                .param("description", "Descrição da categoria"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/products?type=categories"));

        verify(categoryService).createCategory(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateCategory_Success() throws Exception {
        mockMvc.perform(post("/admin/categories/update")
                .with(csrf()) // Adicionado token de segurança
                .param("id", "1")
                .param("name", "Nome Atualizado")
                .param("description", "Descricao Atualizada"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/products?type=categories"));

        verify(categoryService).updateCategoryById(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteCategory_Success() throws Exception {
        mockMvc.perform(post("/admin/categories/delete/1")
                .with(csrf())) // Adicionado token de segurança
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/products?type=categories"));

        verify(categoryService).deleteById(1);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteCategory_ConstraintViolation_RedirectsToProducts() throws Exception {
        doThrow(new RuntimeException("Cannot delete: active products linked."))
            .when(categoryService).deleteById(1);

        mockMvc.perform(post("/admin/categories/delete/1")
                .with(csrf())) // Adicionado token de segurança
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/products?type=products"));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testAccessDeniedForCustomer() throws Exception {
        mockMvc.perform(post("/admin/categories/create")
                .with(csrf())) // Adicionado token de segurança
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetCategoryById_NotFound() throws Exception {
        when(categoryService.getById(999)).thenReturn(null);

        // Substituído try-catch pelo assertThrows idiomático do JUnit 5
        assertThrows(Exception.class, () -> {
            mockMvc.perform(get("/admin/categories/999"));
        });

        verify(categoryService).getById(999);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateCategory_DuplicateName() throws Exception {
        doThrow(new RuntimeException("Categoria já existe"))
            .when(categoryService).createCategory(any());

        mockMvc.perform(post("/admin/categories/create")
                .with(csrf()) // Adicionado token de segurança
                .param("name", "Defumados")
                .param("description", "Duplicado"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/products?type=products"));
    }

    @Test
    void testCreateCategory_Unauthenticated() throws Exception {
        mockMvc.perform(post("/admin/categories/create")
                .with(csrf()) // Adicionado token de segurança
                .param("name", "Categoria")
                .param("description", "Teste"))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateCategory_NotFound() throws Exception {
        doThrow(new RuntimeException("Categoria não encontrada"))
            .when(categoryService).updateCategoryById(any());

        // Substituído try-catch pelo assertThrows idiomático do JUnit 5
        assertThrows(Exception.class, () -> {
            mockMvc.perform(post("/admin/categories/update")
                    .with(csrf())
                    .param("id", "999")
                    .param("name", "Inexistente")
                    .param("description", "Teste"));
        });

        verify(categoryService).updateCategoryById(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteCategory_NotFound() throws Exception {
        doThrow(new RuntimeException("Categoria não encontrada"))
            .when(categoryService).deleteById(999);

        mockMvc.perform(post("/admin/categories/delete/999")
                .with(csrf())) // Adicionado token de segurança
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/products?type=products"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateCategory_EmptyName() throws Exception {
        mockMvc.perform(post("/admin/categories/create")
                .with(csrf()) // Adicionado token de segurança
                .param("name", "")
                .param("description", "Descrição"))
            .andExpect(status().is3xxRedirection());

        verify(categoryService).createCategory(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateCategory_OnlyName() throws Exception {
        mockMvc.perform(post("/admin/categories/create")
                .with(csrf()) // Adicionado token de segurança
                .param("name", "Categoria Mínima"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/products?type=categories"));

        verify(categoryService).createCategory(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateCategory_EmptyDescription() throws Exception {
        mockMvc.perform(post("/admin/categories/update")
                .with(csrf()) // Adicionado token de segurança
                .param("id", "1")
                .param("name", "Nome Válido")
                .param("description", ""))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/products?type=categories"));

        verify(categoryService).updateCategoryById(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetCategoryById_ValidData() throws Exception {
        CategoryEditResponseDto response = new CategoryEditResponseDto(
            "Linguiças",
            "Linguiças artesanais de alta qualidade"
        );

        when(categoryService.getById(5)).thenReturn(response);

        mockMvc.perform(get("/admin/categories/5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Linguiças"))
            .andExpect(jsonPath("$.description").value("Linguiças artesanais de alta qualidade"));

        verify(categoryService).getById(5);
    }
}