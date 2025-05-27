package com.example.demo.endpoints;

import com.example.demo.entities.User;
import retrofit2.Call;
import retrofit2.http.*;

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


    @PUT("/api/users/{id}")  // Actualizar un Usuario por ID
    Call<User> actualizarUsuario(@Path("id") Long id, @Body User user);

    @DELETE("/api/users/{id}")  // Eliminar un Usuario por ID
    Call<Void> eliminarUsuario(@Path("id") Long id);


    //Este era para para la forma que teniamos anteriormente para el menú de admin
    @POST("/login")
    @FormUrlEncoded
    Call<User> login(@Field("email") String email, @Field("password") String password);

}