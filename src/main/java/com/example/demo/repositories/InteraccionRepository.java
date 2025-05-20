package com.example.demo.repositories;

import com.example.demo.entities.Interaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InteraccionRepository extends JpaRepository<Interaccion, Long> {
    List<Interaccion> findTop10ByUsuarioIdOrderByFechaDesc(Long usuarioId);
}
