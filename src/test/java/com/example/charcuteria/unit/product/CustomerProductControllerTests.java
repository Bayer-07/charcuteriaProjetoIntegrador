package com.example.charcuteria.unit.product;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceView;

import com.example.charcuteria.config.SecurityConfig;
import com.example.charcuteria.controller.product.CustomerProductController;
import com.example.charcuteria.dto.product.ProductCatalogResponseDto;
import com.example.charcuteria.dto.product.ProductsEditResponseDto;
import com.example.charcuteria.service.product.ProductService;

@WebMvcTest(CustomerProductController.class)
@Import(SecurityConfig.class)
public class CustomerProductControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @TestConfiguration
    static class StandaloneMvcTestViewConfiguration {
        @Bean
        public ViewResolver viewResolver() {
            return new ViewResolver() {
                @Override
                public View resolveViewName(String viewName, Locale locale) throws Exception {
                    return new InternalResourceView("/WEB-INF/views/" + viewName + ".jsp");
                }
            };
        }
    }

    @Test
    @WithMockUser
    void testGetAllProducts_ReturnsPublicProdutosViewWithCatalogList() throws Exception {
        ProductCatalogResponseDto p1 = org.mockito.Mockito.mock(ProductCatalogResponseDto.class);
        ProductCatalogResponseDto p2 = org.mockito.Mockito.mock(ProductCatalogResponseDto.class);

        when(p1.getCategoryName()).thenReturn("Defumados");
        when(p2.getCategoryName()).thenReturn("Embutidos");

        List<ProductCatalogResponseDto> mockCatalog = List.of(p1, p2);

        when(productService.getProductsForCatalog()).thenReturn(mockCatalog);

        mockMvc.perform(get("/produtos"))
                .andExpect(status().isOk())
                .andExpect(view().name("public/produtos"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("products", mockCatalog));

        verify(productService).getProductsForCatalog();
    }

    @Test
    @WithMockUser
    void testGetProduct_WhenIdExists_ReturnsProductResponseBody() throws Exception {
        Integer productId = 1;
        ProductsEditResponseDto mockProduct = new ProductsEditResponseDto(
                "Salame Colonial",
                "Delicioso salame artesanal",
                "Defumados",
                new java.math.BigDecimal("45.90"),
                50,
                "salame.jpg");

        when(productService.getById(productId)).thenReturn(mockProduct);

        mockMvc.perform(get("/produtos/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Salame Colonial"))
                .andExpect(jsonPath("$.description").value("Delicioso salame artesanal"))
                .andExpect(jsonPath("$.category").value("Defumados"))
                .andExpect(jsonPath("$.price").value(45.90))
                .andExpect(jsonPath("$.stock").value(50))
                .andExpect(jsonPath("$.file").value("salame.jpg"));

        verify(productService).getById(productId);
    }

    @Test
    @WithMockUser
    void testGetProduct_WhenIdDoesNotExist_ThrowsRuntimeException() throws Exception {
        Integer invalidId = 99;
        when(productService.getById(invalidId)).thenReturn(null);

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> {
            mockMvc.perform(get("/produtos/{id}", invalidId));
        }).hasCauseInstanceOf(RuntimeException.class);

        verify(productService).getById(invalidId);
    }
}