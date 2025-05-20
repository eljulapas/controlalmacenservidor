package com.example.demo.endpoints;

import com.example.demo.entities.Proveedor;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface ProveedorInterface {

    @GET("/api/proveedores")
    Call<List<Proveedor>> getAll();

    @POST("/api/proveedores")
    Call<Proveedor> create(@Body Proveedor proveedor);

    @GET("/api/proveedores/{id}")
    Call<Proveedor> getById(@Path("id") Long id);
}
