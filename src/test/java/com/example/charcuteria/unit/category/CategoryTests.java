package com.example.charcuteria.unit.category;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
        mockMvc.perform(post("/admin/categories/delete/1"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/products?type=categories"));

        verify(categoryService).deleteById(1);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteCategory_ConstraintViolation_RedirectsToProducts() throws Exception {
        doThrow(new RuntimeException("Cannot delete: active products linked."))
            .when(categoryService).deleteById(1);

        mockMvc.perform(post("/admin/categories/delete/1"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/products?type=products"));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testAccessDeniedForCustomer() throws Exception {
        mockMvc.perform(post("/admin/categories/create"))
            .andExpect(status().isForbidden());
    }
}
