package com.example.demo.endpoints;

import com.example.demo.entities.User;
import com.example.demo.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Obtener todos los usuarios desde la base de datos
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Obtener un usuario por ID
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userRepository.findById(id).orElse(null);
    }

    // Agregar un nuevo usuario a la base de datos
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    // Actualizar un usuario existente
    @PutMapping("/{id}")
    public ResponseEntity<User> actualizarUsuario(@PathVariable Long id, @RequestBody User user) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setNombre(user.getNombre());
                    existingUser.setFoto(user.getFoto());
                    existingUser.setPassword(user.getPassword());
                    existingUser.setHabilitado(user.getHabilitado());
                    existingUser.setIsAdmin(user.getIsAdmin());
                    existingUser.setEmail(user.getEmail());
                    User updatedUser = userRepository.save(existingUser);
                    return ResponseEntity.ok(updatedUser);
                })
                .orElse(ResponseEntity.status(404).build()); // NO devuelvas ResponseEntity<String>
    }





    // Eliminar un usuario
    @DeleteMapping("/{id}")
    public void eliminarUsuario(@PathVariable Long id) {
        System.out.println("Eliminando usuario con ID: " + id);
        userRepository.deleteById(id);
    }

    // ⚠ AÑADE ESTO EN UserController
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestParam String email, @RequestParam String password) {
        User user = userRepository.findByEmail(email);

        if (user != null && user.getPassword().equals(password)) {
            return ResponseEntity.ok(user); // Login exitoso
        } else {
            return ResponseEntity.status(401).build(); // No autorizado
        }
    }




}
