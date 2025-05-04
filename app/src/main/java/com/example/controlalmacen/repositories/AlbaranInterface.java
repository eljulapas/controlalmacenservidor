package com.example.controlalmacen.repositories;

import com.example.controlalmacen.entities.Albaran;
import java.util.List;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface AlbaranInterface {

    @GET("albaranes")
    Call<List<Albaran>> getAllAlbaranes();

    @GET("albaranes/proveedor/{proveedorId}")
    Call<List<Albaran>> getByProveedor(@Path("proveedorId") Long proveedorId);

    @POST("albaranes")
    Call<Albaran> agregarAlbaran(@Body Albaran albaran);

    @PUT("albaranes/{id}/pagar")
    Call<Albaran> marcarPagado(@Path("id") Long id);

    @Multipart
    @POST("albaranes/{id}/foto")
    Call<Albaran> subirFoto(@Path("id") Long id, @Part MultipartBody.Part file);
}
