package com.example.demo.endpoints;

import com.example.demo.entities.Albaran;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface AlbaranInterface {

    @GET("/api/albaranes")
    Call<List<Albaran>> getAll();

    @GET("/api/albaranes/proveedor/{proveedorId}")
    Call<List<Albaran>> getByProveedor(@Path("proveedorId") Long id);

    @POST("/api/albaranes")
    Call<Albaran> create(@Body Albaran albaran);

    @PUT("/api/albaranes/{id}/pagar")
    Call<Albaran> markAsPaid(@Path("id") Long id);

    @Multipart
    @POST("/api/albaranes/{id}/foto")
    Call<Albaran> uploadFoto(@Path("id") Long id, @Part MultipartBody.Part file);
}
