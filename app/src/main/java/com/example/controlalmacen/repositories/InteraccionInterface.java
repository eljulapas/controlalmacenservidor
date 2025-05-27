package com.example.controlalmacen.repositories;

import com.example.controlalmacen.entities.Interaccion;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

// Clase para futuras mejoras
public interface InteraccionInterface {

    @GET("/api/interacciones")
    Call<List<Interaccion>> getAllInteracciones();

    @POST("/api/interacciones")
    Call<Interaccion> createInteraccion(@Body Interaccion interaccion);

    @GET("/api/interacciones/usuario/{userId}/ultimas")
    Call<List<Interaccion>> getUltimasInteracciones(@Path("userId") Long userId);



}
