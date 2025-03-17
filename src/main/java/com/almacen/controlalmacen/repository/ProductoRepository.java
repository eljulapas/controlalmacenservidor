package com.almacen.controlalmacen.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.almacen.controlalmacen.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long>{

}
