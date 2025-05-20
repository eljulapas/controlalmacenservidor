package com.example.controlalmacen.repositories;

import com.example.controlalmacen.entities.Producto;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface ProductosInterface {

    @GET("productos")
    Call<List<Producto>> getAllProductos();

    @PUT("productos/{id}/cantidad") //Modificar un producto
    Call<Producto> updateCantidad(@Path("id") Long id, @Query("cantidad") Integer cantidad);

    @POST("productos") // Añadir un producto
    Call<Producto> agregarProducto(@Body Producto producto);

    @GET("productos/nombre/{nombre}") //Mirar si hay algún nombre repetido en la base de datos
    Call<Boolean> productoExiste(@Path("nombre") String nombre);

    // Actualizar un producto (PUT)
    @PUT("productos/{id}")
    Call<Producto> updateProducto(@Path("id") Long id, @Body Producto producto);

    // Eliminar un producto (DELETE)
    @DELETE("productos/{id}")
    Call<Void> deleteProducto(@Path("id") Long id);


}
