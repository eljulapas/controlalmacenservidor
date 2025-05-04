package com.example.controlalmacen.entities;

import com.google.gson.annotations.SerializedName;

public class Albaran {
    @SerializedName("id")
    private Long id;

    @SerializedName("fecha")
    private String fecha;

    @SerializedName("fechaPago")
    private String fechaPago;

    @SerializedName("cantidad")
    private Double cantidad;

    @SerializedName("pagado")
    private Boolean pagado;

    @SerializedName("fotoUrl")
    private String fotoUrl;

    @SerializedName("proveedor")
    private Proveedor proveedor;

    public Albaran() {}

    public Albaran(Long id, String fecha, String fechaPago, Double cantidad, Boolean pagado, String fotoUrl, Proveedor proveedor) {
        this.id = id;
        this.fecha = fecha;
        this.fechaPago = fechaPago;
        this.cantidad = cantidad;
        this.pagado = pagado;
        this.fotoUrl = fotoUrl;
        this.proveedor = proveedor;
    }

    // Getters y Setters
    // ...

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(String fechaPago) {
        this.fechaPago = fechaPago;
    }

    public Double getCantidad() {
        return cantidad;
    }

    public void setCantidad(Double cantidad) {
        this.cantidad = cantidad;
    }

    public Boolean getPagado() {
        return pagado;
    }

    public void setPagado(Boolean pagado) {
        this.pagado = pagado;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

}
