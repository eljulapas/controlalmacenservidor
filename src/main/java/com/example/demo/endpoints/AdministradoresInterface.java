package com.example.demo.endpoints;

import com.example.demo.entities.Administrador;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;

public interface AdministradoresInterface {

    @GET("/api/administradores/")
    Call<List<Administrador>> getAllAdministradores();

    @GET("/api/administradores/{id}")
    Call<Administrador> getAdministradorById(@Path("id") Long id);

    @POST("/api/administradores/")
    Call<Administrador> createAdministrador(@Body Administrador administrador);
}
