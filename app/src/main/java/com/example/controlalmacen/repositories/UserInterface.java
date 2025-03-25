// User Interface
package com.example.controlalmacen.repositories;

import com.example.controlalmacen.entities.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface UserInterface {
    @GET("users")
    Call<List<User>> getAllUsers();
}