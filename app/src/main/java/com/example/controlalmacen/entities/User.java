
package com.example.controlalmacen.entities;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    private Long id;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("foto")
    private String foto;

    @SerializedName("password")
    private String password;

    @SerializedName("habilitado")
    private Boolean habilitado;


    public User() {}


    public User(Long id, String nombre, String foto, String password, Boolean habilitado) {
        this.id = id;
        this.nombre = nombre;
        this.foto = foto;
        this.password = password;
        this.habilitado = habilitado;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Boolean getHabilitado() { return habilitado; }
    public void setHabilitado(Boolean habilitado) { this.habilitado = habilitado; }
}
