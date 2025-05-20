package com.example.demo.endpoints;

import com.example.demo.entities.Producto;
import com.example.demo.repositories.ProductoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoRepository productoRepository;

    public ProductoController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // Obtener todos los productos desde la base de datos
    @GetMapping
    public List<Producto> getAllProductos() {
        return productoRepository.findAll();
    }

    // Obtener un producto por ID
    @GetMapping("/{id}")
    public Producto getProductoById(@PathVariable Long id) {
        return productoRepository.findById(id).orElse(null);
    }

    // Agregar un nuevo producto a la base de datos
    @PostMapping
    public Producto createProducto(@RequestBody Producto producto) {
        return productoRepository.save(producto);
    }

    // Actualizar un producto completo
    @PutMapping("/{id}")
    public Producto updateProducto(@PathVariable Long id, @RequestBody Producto productoActualizado) {
        return productoRepository.findById(id)
                .map(producto -> {
                    producto.setNombre(productoActualizado.getNombre());
                    producto.setImagen(productoActualizado.getImagen());
                    producto.setCantidad(productoActualizado.getCantidad());
                    producto.setMinimo(productoActualizado.getMinimo());
                    producto.setHabilitado(productoActualizado.getHabilitado());
                    return productoRepository.save(producto);
                })
                .orElse(null);
    }

    // Eliminar un producto
    @DeleteMapping("/{id}")
    public void deleteProducto(@PathVariable Long id) {
        productoRepository.deleteById(id);
    }

    @PutMapping("/{id}/cantidad")
    public Producto updateCantidad(@PathVariable Long id, @RequestParam Integer cantidad) {
        return productoRepository.findById(id)
                .map(producto -> {
                    producto.setCantidad(cantidad);
                    return productoRepository.save(producto);
                })
                .orElse(null);
    }


}
