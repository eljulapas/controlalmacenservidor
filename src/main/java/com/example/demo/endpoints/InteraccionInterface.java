package com.example.demo.endpoints;

import com.example.demo.entities.Interaccion;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

import java.util.List;

public interface InteraccionInterface {

    // Obtener todas las interacciones
    @GET("/api/interacciones")
    Call<List<Interaccion>> getAllInteracciones();

    // Crear una nueva interaccion
    @POST("/api/interacciones")
    Call<Interaccion> createInteraccion(@Body Interaccion interaccion);
}
