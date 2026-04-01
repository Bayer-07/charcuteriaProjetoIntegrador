package com.example.charcuteria.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // essa chunk de codigo ta aqui ate eu ter certeza q a de baixo funciona <3
    // nao consigo testar agora, sem tempo irmao

    // @Bean
    // public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    //     http
    //         .csrf(csrf -> csrf.disable())
    //         .authorizeHttpRequests(auth -> auth
    //             .requestMatchers("/register", "/registerAdmin", "/login", "/loginAdmin", "/user/dashboard", "/user/dashboardAdmin", "/handleProfile", "/teste").permitAll()

    //             .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()

    //             .anyRequest().authenticated()
    //         )
    //         .formLogin(form -> form
    //             .loginPage("/index")
    //             .permitAll()
    //         );

    //     return http.build();
    // }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // rotas publicas
                .requestMatchers("/", "/index", "/login", "/loginAdmin", "/register", "/css/**", "/js/**", "/images/**").permitAll()

                // rotas admin where getAuthorities.rote = "ROLE_ADMIN"
                .requestMatchers("/user/dashboardAdmin/**", "/registerAdmin").hasRole("ADMIN")

                // rotas com permissao geral
                .requestMatchers("/user/dashboard/**").hasAnyRole("CUSTOMER", "ADMIN")

                // qualquer rota precisa tar logado (segurança)
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login") // vai retornar pra ca
                .loginProcessingUrl("/login") // URL que o Spring vai interceptar (o POST do seu form)
                .usernameParameter("email")    // <--- username == email
                .passwordParameter("password") // <--- password == password
                .defaultSuccessUrl("/user/dashboard", true) // redirect pra onde vc vai depois de logar
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/index") // limpa a sessao do mlk e volta pra ca
                .permitAll()
            );

        return http.build();
    }
}
