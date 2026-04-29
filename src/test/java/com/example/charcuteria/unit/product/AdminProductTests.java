package com.example.charcuteria.unit.product;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.charcuteria.config.SecurityConfig;
import com.example.charcuteria.controller.product.AdminProductController;
import com.example.charcuteria.dto.product.ProductsEditResponseDto;
import com.example.charcuteria.service.product.FileStorageService;
import com.example.charcuteria.service.product.ProductService;

import java.math.BigDecimal;

@WebMvcTest(AdminProductController.class)
@Import(SecurityConfig.class)
public class AdminProductTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private FileStorageService fileStorageService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateProduct_Success() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile(
            "image", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "content".getBytes()
        );

        when(fileStorageService.saveFile(any())).thenReturn("stored_image.jpg");
        when(productService.getCategoryIdByName(anyString())).thenReturn(1);

        mockMvc.perform(multipart("/admin/product/create")
                .file(imageFile)
                .param("name", "Salame")
                .param("description", "Salame artesanal")
                .param("price", "50.00")
                .param("stock", "10")
                .param("category", "Charcutaria"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/products"));

        verify(fileStorageService).saveFile(any());
        verify(productService).createProduct(any(), anyInt(), anyString());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetProductById_ReturnsJsonResponse() throws Exception {
        ProductsEditResponseDto response = new ProductsEditResponseDto(
            "Copa", "Copa Lombo", "Charcutaria", new BigDecimal("80.00"), 5, "copa.jpg"
        );

        when(productService.getById(1)).thenReturn(response);

        mockMvc.perform(get("/admin/product/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Copa"))
            .andExpect(jsonPath("$.price").value(80.00));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateProduct_WithNewFile() throws Exception {
        MockMultipartFile newImage = new MockMultipartFile(
            "file", "new.jpg", MediaType.IMAGE_JPEG_VALUE, "new_content".getBytes()
        );

        when(productService.findFileNameById(1)).thenReturn("old_image.jpg");
        when(fileStorageService.saveFile(any())).thenReturn("new_image.jpg");

        mockMvc.perform(multipart("/admin/product/update")
                .file(newImage)
                .param("id", "1")
                .param("name", "Copa Atualizada")
                .param("category", "Charcutaria")
                .param("price", "85.00")
                .param("stock", "4"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/products"));

        verify(fileStorageService).deleteFile("old_image.jpg");
        verify(productService).updateProductById(any(), anyString());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteProduct_Success() throws Exception {
        when(productService.findFileNameById(1)).thenReturn("to_delete.jpg");

        mockMvc.perform(post("/admin/product/delete/1"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/products"));

        verify(productService).deleteProductById(1);
        verify(fileStorageService).deleteFile("to_delete.jpg");
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testCreateProduct_ForbiddenForCustomer() throws Exception {
        mockMvc.perform(post("/admin/product/create"))
            .andExpect(status().isForbidden());
    }
}
