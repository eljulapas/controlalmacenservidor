package com.example.controlalmacen.repositories;

import com.example.controlalmacen.entities.Proveedor;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface ProveedorInterface {

    @GET("proveedores")
    Call<List<Proveedor>> getAllProveedores();

    @POST("proveedores")
    Call<Proveedor> agregarProveedor(@Body Proveedor proveedor);

    @GET("proveedores/{id}")
    Call<Proveedor> getProveedor(@Path("id") Long id);
}
