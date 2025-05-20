package com.example.demo.endpoints;

import com.example.demo.entities.Interaccion;
import com.example.demo.repositories.InteraccionRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interacciones")
public class InteraccionController {

    private final InteraccionRepository interaccionRepository;

    public InteraccionController(InteraccionRepository interaccionRepository) {
        this.interaccionRepository = interaccionRepository;
    }

    @GetMapping
    public List<Interaccion> getAll() {
        return interaccionRepository.findAll();
    }

    @PostMapping
    public Interaccion save(@RequestBody Interaccion interaccion) {
        return interaccionRepository.save(interaccion);
    }

    @GetMapping("/usuario/{userId}/ultimas")
    public List<Interaccion> getUltimasInteracciones(@PathVariable Long userId) {
        return interaccionRepository.findTop10ByUsuarioIdOrderByFechaDesc(userId);
    }



}
