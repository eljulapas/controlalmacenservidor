package com.example.controlalmacen.instances;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserInstance {

    private static volatile Retrofit retrofit = null;
    private static final String BASE_URL = "http://10.0.2.2:8080"; // IP loopback para emulador Android

    // Método para obtener una instancia de Retrofit
    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            synchronized (UserInstance.class) {
                if (retrofit == null) {  // Doble verificación para evitar múltiples instancias
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
