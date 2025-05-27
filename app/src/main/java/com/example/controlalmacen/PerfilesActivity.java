package com.example.controlalmacen;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.controlalmacen.entities.User;
import com.example.controlalmacen.instances.UserInstance;
import com.example.controlalmacen.repositories.UserInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText editTextBuscarUsuario;
    private UserAdapter userAdapter;
    private UserInterface userInterface;

    private List<User> listaCompletaOrdenada = new ArrayList<>();  // Lista con habilitados + deshabilitados ordenados

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfiles);

        recyclerView = findViewById(R.id.recyclerViewPerfiles);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        editTextBuscarUsuario = findViewById(R.id.editTextBuscarUsuario);

        userInterface = UserInstance.getRetrofitInstance().create(UserInterface.class);
        cargarUsuarios();

        // Botón atrás
        Button btnAtras = findViewById(R.id.btn_atras);
        btnAtras.setOnClickListener(v -> {
            Intent intent = new Intent(PerfilesActivity.this, ProductActivity.class);
            startActivity(intent);
            finish();
        });

        editTextBuscarUsuario.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarUsuarios(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void cargarUsuarios() {
        userInterface.getAllUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<User> habilitados = new ArrayList<>();
                    List<User> deshabilitados = new ArrayList<>();

                    for (User u : response.body()) {
                        if (Boolean.TRUE.equals(u.getHabilitado())) {
                            habilitados.add(u);
                        } else {
                            deshabilitados.add(u);
                        }
                    }

                    Comparator<User> comparador = Comparator.comparing(User::getNombre, String.CASE_INSENSITIVE_ORDER);
                    Collections.sort(habilitados, comparador);
                    Collections.sort(deshabilitados, comparador);

                    listaCompletaOrdenada.clear();
                    listaCompletaOrdenada.addAll(habilitados);
                    listaCompletaOrdenada.addAll(deshabilitados);

                    userAdapter = new UserAdapter(listaCompletaOrdenada);
                    recyclerView.setAdapter(userAdapter);
                } else {
                    Toast.makeText(PerfilesActivity.this, "Error al cargar usuarios", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(PerfilesActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filtrarUsuarios(String texto) {
        List<User> filtrados = new ArrayList<>();
        for (User u : listaCompletaOrdenada) {
            if (u.getNombre().toLowerCase().contains(texto.toLowerCase())) {
                filtrados.add(u);
            }
        }
        userAdapter.actualizarLista(filtrados);
    }


    @Override
    protected void onResume() {
        super.onResume();
        cargarUsuarios(); //  Recargaa los usuarios cuando se vuelve a la actividad
    }

}
