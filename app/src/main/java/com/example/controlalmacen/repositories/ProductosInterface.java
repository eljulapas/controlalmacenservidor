package com.example.controlalmacen.repositories;

import com.example.controlalmacen.entities.Producto;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProductosInterface {

    @GET("productos")
    Call<List<Producto>> getAllProductos();

    @PUT("productos/{id}/cantidad")
    Call<Producto> updateCantidad(@Path("id") Long id, @Query("cantidad") Integer cantidad);

}
