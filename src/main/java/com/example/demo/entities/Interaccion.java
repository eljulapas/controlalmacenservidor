package com.example.demo.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;


// Esta clase es para futuras mejoras en la aplicación

@Entity
@Table(name = "interaccion")
public class Interaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private User usuario;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    private String accion;

    private LocalDateTime fecha;

    public Interaccion() {}

    public Interaccion(User usuario, Producto producto, String accion, LocalDateTime fecha) {
        this.usuario = usuario;
        this.producto = producto;
        this.accion = accion;
        this.fecha = fecha;
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUsuario() { return usuario; }
    public void setUsuario(User usuario) { this.usuario = usuario; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}
