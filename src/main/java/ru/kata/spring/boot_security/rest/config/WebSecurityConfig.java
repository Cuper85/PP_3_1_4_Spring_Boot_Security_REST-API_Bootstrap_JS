package ru.kata.spring.boot_security.rest.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.kata.spring.boot_security.rest.security.UserDetailsServiceImpl;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final SuccessUserHandler successUserHandler;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    public WebSecurityConfig(SuccessUserHandler successUserHandler, UserDetailsServiceImpl userDetailsServiceImpl) {
        this.successUserHandler = successUserHandler;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsServiceImpl);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())                                                             // Отключаем CSRF полностью
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/logout").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/user").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/api/**").denyAll()
                        .anyRequest().authenticated()
                )
//                .csrf(csrf -> csrf                                                                      // Настраиваем CSRF
//                        .ignoringRequestMatchers("/api/**")                                             // Игнорируем CSRF для всех URL начинающихся с /api/**
//                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler(successUserHandler)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .permitAll()
                );

        return http.build();
    }
}


