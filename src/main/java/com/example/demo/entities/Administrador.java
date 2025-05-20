package com.example.demo.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "Administradores")
public class Administrador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Integer claveNumerica;

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getClaveNumerica() {
        return claveNumerica;
    }

    public void setClaveNumerica(Integer claveNumerica) {
        this.claveNumerica = claveNumerica;
    }
}
