package com.example.controlalmacen.entities;

import com.google.gson.annotations.SerializedName;

public class Proveedor {
    @SerializedName("id")
    private Long id;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("email")
    private String email;

    @SerializedName("telefono")
    private String telefono;

    @SerializedName("cif")
    private String cif;

    public Proveedor() {}

    public Proveedor(Long id, String nombre, String email, String telefono, String cif) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.cif = cif;
    }



    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCif() { return cif; }
    public void setCif(String cif) { this.cif = cif; }

}
