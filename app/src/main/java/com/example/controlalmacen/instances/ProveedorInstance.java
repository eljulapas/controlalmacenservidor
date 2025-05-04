package com.example.controlalmacen.instances;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProveedorInstance {
    private static Retrofit retrofit;
    private static final String BASE_URL = "http://192.168.0.18:8080/api/";

    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
