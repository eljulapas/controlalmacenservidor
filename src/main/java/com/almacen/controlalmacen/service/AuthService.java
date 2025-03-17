package com.almacen.controlalmacen.service;

import com.almacen.controlalmacen.security.JwtService;
import com.almacen.controlalmacen.model.User;
import com.almacen.controlalmacen.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;  

    public AuthService(UserRepository userRepository, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;  
    }

    // Método para logear al usuario y generar el JWT
    public String login(String username, String password) {
      
        User user = userRepository.findByNombre(username).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        // Si el usuario existe y la contraseña es correcta, se genera el JWT
        return jwtService.generateToken(user.getNombre());
    }
}
