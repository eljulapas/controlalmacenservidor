package com.example.demo.repositories;

import com.example.demo.entities.Albaran;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbaranRepository extends JpaRepository<Albaran, Long> {
    List<Albaran> findByProveedorId(Long proveedorId);
}
