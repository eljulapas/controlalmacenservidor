package com.example.controlalmacen.entities;

import com.google.gson.annotations.SerializedName;

public class Producto {

    @SerializedName("id")
    private Long id;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("imagen")
    private String imagen;

    @SerializedName("cantidad")
    private Integer cantidad;

    @SerializedName("minimo")
    private Integer minimo;


    public Producto() {}

    public Producto(Long id, String nombre, String imagen, Integer cantidad, Integer minimo) {
        this.id = id;
        this.nombre = nombre;
        this.imagen = imagen;
        this.cantidad = cantidad;
        this.minimo = minimo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public Integer getMinimo() { return minimo; }
    public void setMinimo(Integer minimo) { this.minimo = minimo; }
}
