package com.example.demo.instances;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProveedorInstance {

    private static volatile Retrofit retrofit = null;
    private static final String BASE_URL = "http://192.168.0.18:8080";

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            synchronized (ProveedorInstance.class) {
                if (retrofit == null) {
                    retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }
            }
        }
        return retrofit;
    }
}
