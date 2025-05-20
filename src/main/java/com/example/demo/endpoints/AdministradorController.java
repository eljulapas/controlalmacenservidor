package com.example.demo.endpoints;

import com.example.demo.entities.Administrador;
import com.example.demo.repositories.AdministradorRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/administradores")
public class AdministradorController {

    private final AdministradorRepository administradorRepository;

    public AdministradorController(AdministradorRepository administradorRepository) {
        this.administradorRepository = administradorRepository;
    }

    @GetMapping
    public List<Administrador> getAllAdministradores() {
        return administradorRepository.findAll();
    }

    @GetMapping("/{id}")
    public Administrador getAdministradorById(@PathVariable Long id) {
        return administradorRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Administrador createAdministrador(@RequestBody Administrador administrador) {
        return administradorRepository.save(administrador);
    }
}
