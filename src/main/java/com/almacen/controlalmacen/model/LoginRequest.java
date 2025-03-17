package com.almacen.controlalmacen.model;

public class LoginRequest {
	private String nombre;
	
	private String password;
    
    
    
    

    public LoginRequest() {
    }

    public LoginRequest(String nombre, String password) {
        this.nombre = nombre;
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    

}
