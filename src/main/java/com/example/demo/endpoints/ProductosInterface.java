package com.example.demo.endpoints;


import com.example.demo.entities.Producto;
import org.springframework.web.bind.annotation.GetMapping;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;

public interface ProductosInterface {

    @GET("/api/productos/")
    Call<List<Producto>> getAllProductos();


    // Endpoint para obtener un producto por ID
    @GET("/api/productos/{id}")
    Call<Producto> getProductoById(@Path("id") Long id);


    // Endpoint para crear un nuevo producto
    @POST("/api/productos/")
    Call<Producto> createProducto(@Body Producto producto);





}
