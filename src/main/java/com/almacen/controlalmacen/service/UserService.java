package com.almacen.controlalmacen.service;

import com.almacen.controlalmacen.exception.UserNotFoundException;
import com.almacen.controlalmacen.model.User;
import com.almacen.controlalmacen.repository.UserRepository;
import com.almacen.controlalmacen.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    
    public void registerUser(String nombre, String password) {
       
        if (userRepository.findByNombre(nombre).isPresent()) {
            throw new RuntimeException("El usuario ya existe");
        }

        
        User user = new User();
        user.setNombre(nombre);
        user.setPassword(passwordEncoder.encode(password));  // Aquí ciframos la contraseña antes de guardarla
        userRepository.save(user);
    }

    // Método para hacer login y obtener el token JWT
    public String loginUser(String nombre, String password) {
    
        User user = userRepository.findByNombre(nombre)
            .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        // Generar el token JWT
        return jwtTokenProvider.generateToken(user);
    }

   
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

   
    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
    }

    
    public User saveUser(User user) {
        return userRepository.save(user);
    }
}
