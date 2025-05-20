package com.example.demo.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "albaranes")
public class Albaran {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;
    private LocalDate fechaPago;
    private Double cantidad;
    private Boolean pagado;
    private String fotoUrl;

    @ManyToOne
    @JoinColumn(name = "proveedor_id")
    @JsonIgnoreProperties("albaranes") // evita bucles recursivos
    private Proveedor proveedor;

    public Albaran() {}

    public Albaran(LocalDate fecha, LocalDate fechaPago, Double cantidad, Boolean pagado, String fotoUrl, Proveedor proveedor) {
        this.fecha = fecha;
        this.fechaPago = fechaPago;
        this.cantidad = cantidad;
        this.pagado = pagado;
        this.fotoUrl = fotoUrl;
        this.proveedor = proveedor;
    }

    // Getters y Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalDate getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDate fechaPago) { this.fechaPago = fechaPago; }

    public Double getCantidad() { return cantidad; }
    public void setCantidad(Double cantidad) { this.cantidad = cantidad; }

    public Boolean getPagado() { return pagado; }
    public void setPagado(Boolean pagado) { this.pagado = pagado; }

    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }

    public Proveedor getProveedor() { return proveedor; }
    public void setProveedor(Proveedor proveedor) { this.proveedor = proveedor; }
}
