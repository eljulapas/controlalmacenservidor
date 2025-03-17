package com.almacen.controlalmacen.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig implements WebMvcConfigurer {  // Implementa WebMvcConfigurer para configurar CORS

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()  
            .authorizeRequests()
            .requestMatchers("/auth/**").permitAll()  // Permitir acceso sin autenticación en rutas de login     // ESTO ES OTRO QUE TE LOGEAS TE DA EL TOKEN Y LUEGO CON EL TOKEN HACES ALGO EN EL POSTMAN Y TE SALE PRODUCTOS Y USERS .requestMatchers("/api/users/").hasRole("USER")  // Ruta de usuarios solo accesible por un rol USER
            .requestMatchers("/api/users/**", "/api/productos/**").permitAll()  // Permitir acceso sin autenticación a users y productos
            .requestMatchers("/hello").permitAll()  // Permitir acceso sin autenticación a /hello
            .anyRequest().authenticated();  // El resto de las rutas requieren autenticación
        return http.build();
    }

    // Configuracion CORS
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("Authorization", "Content-Type");
    }
}
