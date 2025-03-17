package com.almacen.controlalmacen.controller;

import com.almacen.controlalmacen.exception.UserNotFoundException;
import com.almacen.controlalmacen.model.LoginRequest;
import com.almacen.controlalmacen.model.JwtResponse;
import com.almacen.controlalmacen.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    // Endpoint para registrar un usuario
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody LoginRequest loginRequest) {
        try {
            if (loginRequest.getNombre() == null || loginRequest.getNombre().isEmpty() || 
                loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El nombre y la contraseña no pueden estar vacíos.");
            }

            userService.registerUser(loginRequest.getNombre(), loginRequest.getPassword());
            return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al registrar el usuario: " + e.getMessage());
        }
    }



    // Endpoint para hacer login y obtener el token JWT
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            if (loginRequest.getNombre() == null || loginRequest.getNombre().isEmpty() || 
                loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new JwtResponse("El nombre y la contraseña no pueden estar vacíos."));
            }

            String token = userService.loginUser(loginRequest.getNombre(), loginRequest.getPassword());
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new JwtResponse("Credenciales incorrectas."));
            }

            return ResponseEntity.ok(new JwtResponse(token));  // Devuelve el token JWT en la respuesta
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new JwtResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new JwtResponse("Error al iniciar sesión: " + e.getMessage()));
        }
    }
}
