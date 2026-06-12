package com.example.charcuteria.unit.product;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;

import jakarta.servlet.ServletException;

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

@WebMvcTest(AdminProductController.class)
@Import(SecurityConfig.class)
public class AdminProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private FileStorageService fileStorageService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateProduct_Success_RedirectsToProducts() throws Exception {
        when(fileStorageService.saveFile(any())).thenReturn("imagem.jpg");
        when(productService.getCategoryIdByName(anyString())).thenReturn(1);

        MockMultipartFile image = new MockMultipartFile("image", "imagem.jpg", MediaType.IMAGE_JPEG_VALUE, "bytes".getBytes());

        mockMvc.perform(multipart("/admin/product/create")
                .file(image)
                .with(csrf())
                .param("name", "Salame")
                .param("description", "Salame italiano")
                .param("price", "29.90")
                .param("category", "Frios"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/products"));

        verify(productService).createProduct(any(), anyInt(), anyString());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateProduct_ValidationErrors_RedirectsToProducts() throws Exception {
        mockMvc.perform(multipart("/admin/product/create")
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/products"));

        verify(productService, never()).createProduct(any(), anyInt(), anyString());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateProduct_ServiceException_RedirectsToDashboard() throws Exception {
        when(fileStorageService.saveFile(any())).thenThrow(new RuntimeException("Erro ao salvar arquivo"));

        MockMultipartFile image = new MockMultipartFile("image", "imagem.jpg", MediaType.IMAGE_JPEG_VALUE, "bytes".getBytes());

        mockMvc.perform(multipart("/admin/product/create")
                .file(image)
                .with(csrf())
                .param("name", "Salame")
                .param("description", "Salame italiano")
                .param("price", "29.90")
                .param("category", "Frios"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/dashboard"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateProduct_PriceWithComma_ParsedCorrectly() throws Exception {
        when(fileStorageService.saveFile(any())).thenReturn("imagem.jpg");
        when(productService.getCategoryIdByName(anyString())).thenReturn(1);

        MockMultipartFile image = new MockMultipartFile("image", "imagem.jpg", MediaType.IMAGE_JPEG_VALUE, "bytes".getBytes());

        mockMvc.perform(multipart("/admin/product/create")
                .file(image)
                .with(csrf())
                .param("name", "Salame")
                .param("description", "Salame italiano")
                .param("price", "29,90")
                .param("category", "Frios"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/products"));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testCreateProduct_ForbiddenForCustomer() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "imagem.jpg", MediaType.IMAGE_JPEG_VALUE, "bytes".getBytes());

        mockMvc.perform(multipart("/admin/product/create")
                .file(image)
                .with(csrf())
                .param("name", "Salame")
                .param("price", "29.90")
                .param("category", "Frios"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetProductById_ReturnsProductJson() throws Exception {
        ProductsEditResponseDto dto = new ProductsEditResponseDto("Salame", "Italiano", "Frios", new BigDecimal("29.90"), 10, "imagem.jpg");
        when(productService.getById(1)).thenReturn(dto);

        mockMvc.perform(get("/admin/product/1"))
            .andExpect(status().isOk());

        verify(productService).getById(1);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetProductById_NotFound_ThrowsException() throws Exception {
        when(productService.getById(99)).thenReturn(null);

        assertThrows(ServletException.class, () ->
            mockMvc.perform(get("/admin/product/99"))
        );
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testGetProductById_ForbiddenForCustomer() throws Exception {
        mockMvc.perform(get("/admin/product/1"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateProduct_Success_RedirectsToProducts() throws Exception {
        when(productService.findFileNameById(anyInt())).thenReturn("antiga.jpg");

        mockMvc.perform(post("/admin/product/update")
                .with(csrf())
                .param("id", "1")
                .param("name", "Salame Atualizado")
                .param("description", "Descrição atualizada")
                .param("price", "39.90")
                .param("category", "Frios"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/products"));

        verify(productService).updateProductById(any(), anyString());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateProduct_WithNewImage_SavesNewAndDeletesOld() throws Exception {
        when(productService.findFileNameById(anyInt())).thenReturn("antiga.jpg");
        when(fileStorageService.saveFile(any())).thenReturn("nova.jpg");

        MockMultipartFile newImage = new MockMultipartFile("file", "nova.jpg", MediaType.IMAGE_JPEG_VALUE, "bytes".getBytes());

        mockMvc.perform(multipart("/admin/product/update")
                .file(newImage)
                .with(request -> { request.setMethod("POST"); return request; })
                .with(csrf())
                .param("id", "1")
                .param("name", "Salame Atualizado")
                .param("description", "Descrição atualizada")
                .param("price", "39.90")
                .param("category", "Frios"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/products"));

        verify(fileStorageService).saveFile(any());
        verify(fileStorageService).deleteFile("antiga.jpg");
        verify(productService).updateProductById(any(), anyString());
    }

    // @Test
    // @WithMockUser(roles = "ADMIN")
    // void testUpdateProduct_SaveFileThrows_RedirectsToProducts() throws Exception {
    //     when(productService.findFileNameById(anyInt())).thenReturn("antiga.jpg");
    //     when(fileStorageService.saveFile(any())).thenThrow(new RuntimeException("Cannot save file"));

    //     MockMultipartFile newImage = new MockMultipartFile("file", "nova.jpg", MediaType.IMAGE_JPEG_VALUE, "bytes".getBytes());

    //     mockMvc.perform(multipart("/admin/product/update")
    //             .file(newImage)
    //             .with(request -> { request.setMethod("POST"); return request; })
    //             .with(csrf())
    //             .param("id", "1")
    //             .param("name", "Salame")
    //             .param("description", "Descrição")
    //             .param("price", "39.90")
    //             .param("category", "Frios"))
    //         .andExpect(status().is3xxRedirection())
    //         .andExpect(redirectedUrl("/admin/products"));
    // }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testUpdateProduct_ForbiddenForCustomer() throws Exception {
        mockMvc.perform(post("/admin/product/update")
                .with(csrf())
                .param("id", "1")
                .param("name", "Salame"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteProduct_Success_RedirectsToProducts() throws Exception {
        when(productService.findFileNameById(1)).thenReturn("imagem.jpg");

        mockMvc.perform(post("/admin/product/delete/1")
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/products"));

        verify(productService).deleteProductById(1);
        verify(fileStorageService).deleteFile("imagem.jpg");
    }

    // @Test
    // @WithMockUser(roles = "ADMIN")
    // void testDeleteProduct_DeleteFileThrows_RedirectsToProducts() throws Exception {
    //     when(productService.findFileNameById(1)).thenReturn("imagem.jpg");
    //     doThrow(new RuntimeException("Cannot delete file")).when(fileStorageService).deleteFile(anyString());

    //     mockMvc.perform(post("/admin/product/delete/1")
    //             .with(csrf()))
    //         .andExpect(status().is3xxRedirection())
    //         .andExpect(redirectedUrl("/admin/products"));
    // }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteProduct_FindFileNameThrows_ServletException() throws Exception {
        when(productService.findFileNameById(1)).thenThrow(new RuntimeException("Produto não encontrado"));

        assertThrows(ServletException.class, () ->
            mockMvc.perform(post("/admin/product/delete/1").with(csrf()))
        );
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testDeleteProduct_ForbiddenForCustomer() throws Exception {
        mockMvc.perform(post("/admin/product/delete/1")
                .with(csrf()))
            .andExpect(status().isForbidden());
    }
}