package com.example.controlalmacen.entities;

public class Interaccion {

    private Long id;
    private String tipo; // "EDICION", "AGREGADO", "ELIMINADO", etc.
    private Long userId;
    private Long productoId;

    public Interaccion() {}

    public Interaccion(Long id, String tipo, Long userId, Long productoId) {
        this.id = id;
        this.tipo = tipo;
        this.userId = userId;
        this.productoId = productoId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }
}
