package com.example.controlalmacen.entities;

import com.google.gson.annotations.SerializedName;

public class Administrador {

    @SerializedName("id")
    private Long id;

    @SerializedName("userId")
    private Long userId;

    @SerializedName("claveNumerica")
    private Integer claveNumerica;


    public Administrador() {}

    public Administrador(Long id, Long userId, Integer claveNumerica) {
        this.id = id;
        this.userId = userId;
        this.claveNumerica = claveNumerica;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Integer getClaveNumerica() { return claveNumerica; }
    public void setClaveNumerica(Integer claveNumerica) { this.claveNumerica = claveNumerica; }
}
