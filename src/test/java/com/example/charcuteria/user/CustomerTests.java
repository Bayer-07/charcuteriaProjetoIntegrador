package com.example.charcuteria.user;

import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.example.charcuteria.config.SecurityConfig;
import com.example.charcuteria.controller.user.UserController;
import com.example.charcuteria.dto.user.UserLoginDto;
import com.example.charcuteria.dto.user.UserRegistrationDto;
import com.example.charcuteria.enums.UserRoleEnum;
import com.example.charcuteria.service.user.UserService;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
public class CustomerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    // testing get view returns
    @Test
    void testRegisterUserView() throws Exception {
        mockMvc.perform(get("/register"))
            .andExpect(status().isOk())
            .andExpect(view().name("user/registration-view"))
            .andExpect(model().attributeExists("userDto"))
            .andExpect(model().attribute("userDto", instanceOf(UserRegistrationDto.class)));
    }

    @Test
    void testLoginUserView() throws Exception {
        mockMvc.perform(get("/login"))
            .andExpect(status().isOk())
            .andExpect(view().name("login"))
            .andExpect(model().attributeExists("userDto"))
            .andExpect(model().attribute("userDto", instanceOf(UserLoginDto.class)));
    }

    // testing register customer
    @Test
    void testRegisterUser_Sucess() throws Exception {
        mockMvc.perform(post("/register")
                .param("name", "test1")
                .param("email" ,"test1@gmail.com")
                .param("password" ,"123456789")
                .param("passwordControl" ,"123456789"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login"));

            verify(userService).createUser(any(UserRegistrationDto.class), eq(UserRoleEnum.CUSTOMER));
        }

    @Test
    void testRegisterUser_MissmatchPasswords() throws Exception {
        mockMvc.perform(post("/register")
                .param("name", "test1")
                .param("email", "test1@gmail.com")
                .param("password", "123456789")
                .param("passwordControl", "987654321")
            )
            .andExpect(status().isOk())
            .andExpect(view().name("user/registration-view"))
            .andExpect(model().attributeExists("registrationError"))
            .andExpect(model().attribute("registrationError", "Different passwords"));
    }

    @Test
    void testRegisterUser_BeanValidationErrors() throws Exception {
        mockMvc.perform(post("/register")
                .param("name", "te")
                .param("email", "invalid-email")
                .param("password", "")
                .param("passwordControl", "12")
            )
            .andExpect(status().isOk())
            .andExpect(view().name("user/registration-view"))
            .andExpect(model().hasErrors());
    }

    @Test
    void testRegisterUser_InternalServerError() throws Exception {
        doThrow(new RuntimeException("Database down"))
            .when(userService).createUser(any(), any());

        mockMvc.perform(post("/register")
            .param("name", "teste")
            .param("email", "test@gmail.com")
            .param("password", "123456789")
            .param("passwordControl", "123456789")
        )
        .andExpect(status().isOk())
        .andExpect(view().name("user/registration-view"))
        .andExpect(model().attribute("registrationError", "Internal server error, try again later please"));
    }

    // testing login customer
    @Test
    void testLoginUserSucess() throws Exception {
        mockMvc.perform(post("/login"))
            .andExpect(status().is3xxRedirection());
    }
}
