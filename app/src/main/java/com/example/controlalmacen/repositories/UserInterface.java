// User Interface
package com.example.controlalmacen.repositories;

import com.example.controlalmacen.entities.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface UserInterface {
    @GET("users")
    Call<List<User>> getAllUsers();

    @POST("users")  // Agregar un Usuario
    Call<User> agregarUsuario(@Body User user);

    @PUT("users/{id}")  // Actualizar un Usuario por ID
    Call<User> actualizarUsuario(@Path("id") Long id, @Body User user);

    @DELETE("users/{id}")  // Eliminar un Usuario por ID
    Call<Void> eliminarUsuario(@Path("id") Long id);

    @POST("/api/users/login")  // ⬅ CORRIGE ESTO
    @FormUrlEncoded
    Call<User> login(@Field("email") String email, @Field("password") String password);


}