package com.example.controlalmacen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import com.example.controlalmacen.entities.Producto;
import com.example.controlalmacen.entities.User;
import com.example.controlalmacen.instances.ProductoInstance;
import com.example.controlalmacen.instances.UserInstance;
import com.example.controlalmacen.repositories.ProductosInterface;
import com.example.controlalmacen.repositories.UserInterface;
import com.google.android.material.navigation.NavigationView;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private UserInterface userInterface;
    private ProductosInterface productosInterface;

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;


    private EditText editTextBuscarUsuario;
    private List<User> listaUsuarios = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userInterface = UserInstance.getRetrofitInstance().create(UserInterface.class);
        productosInterface = ProductoInstance.getRetrofitInstance().create(ProductosInterface.class);

        recyclerView = findViewById(R.id.recyclerViewUsuarios);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchUsers();

        // Programar RevisorCantidadStock para ejecutarse cada 12 horas
        //pero no sabemos a qué correo hay que enviarlo
        PeriodicWorkRequest stockCheckRequest = new PeriodicWorkRequest.Builder(
                RevisorCantidadStock.class,
                12, TimeUnit.HOURS
        ).build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "revisarStockMinimo",
                ExistingPeriodicWorkPolicy.KEEP,
                stockCheckRequest
        );

    }

    private void fetchUsers() {
        Call<List<User>> call = userInterface.getAllUsers();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<User> habilitados = new ArrayList<>();
                    for (User u : response.body()) {
                        if (Boolean.TRUE.equals(u.getHabilitado())) {
                            habilitados.add(u);
                        }
                    }

                    listaUsuarios = habilitados;

                    userAdapter = new UserAdapter(habilitados);
                    recyclerView.setAdapter(userAdapter);
                } else {
                    Toast.makeText(MainActivity.this, "No se encontraron usuarios", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }







    //Esto de iniciarTemporizadorInactividad también hay que cambiarlo para posible mejora de futuro
   /* private void iniciarTemporizadorInactividad() {
        tareaInactividad = () -> {
            adminAutenticado = false;
            adminActual = null;
            prefs.edit().putBoolean("clave_verificada", false).apply();
            Toast.makeText(this, "Sesión expirada por inactividad", Toast.LENGTH_SHORT).show();
        };
        reiniciarTemporizadorInactividad();
    }

    private void reiniciarTemporizadorInactividad() {
        handlerInactividad.removeCallbacks(tareaInactividad);
        handlerInactividad.postDelayed(tareaInactividad, TIEMPO_INACTIVIDAD_MS);
    }*/


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            fetchUsers();
            Toast.makeText(this, "Usuario actualizado correctamente.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
    }

}
