package com.almacen.controlalmacen.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.almacen.controlalmacen.model.Producto;
import com.almacen.controlalmacen.repository.ProductoRepository;

@Service
public class ProductoService {
	
	@Autowired
    private ProductoRepository productoRepository;
	
	
	public List<Producto> getAllProductos() {
        return productoRepository.findAll();
    }

    public Producto getProductoById(Long id) {
        return productoRepository.findById(id).orElse(null);
    }

    public Producto saveProducto(Producto producto) {
        return productoRepository.save(producto);
    }

}
