package com.example.demo.endpoints;

import com.example.demo.entities.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;

public interface UsuariosInterface {

    // Endpoint para obtener todos los usuarios
    @GET("/api/users/")
    Call<List<User>> getAllUsers();

    // Endpoint para obtener un usuario por ID
    @GET("/api/users/{id}")
    Call<User> getUserById(@Path("id") Long id);

    // Endpoint para crear un nuevo usuario
    @POST("/api/users/")
    Call<User> createUser(@Body User user);
}