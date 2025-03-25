package com.example.controlalmacen;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.controlalmacen.entities.User;
import com.example.controlalmacen.instances.UserInstance;
import com.example.controlalmacen.repositories.UserInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private UserInterface userInterface;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configurar Retrofit
        userInterface = UserInstance.getRetrofitInstance().create(UserInterface.class);

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.recyclerViewUsuarios);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Cargar los usuarios al abrir la app
        fetchUsers();
    }

    private void fetchUsers() {
        Call<List<User>> call = userInterface.getAllUsers();

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<User> users = response.body();
                    Log.d("MainActivity", "Usuarios obtenidos: " + users.size());

                    // Adaptador con los datos
                    UserAdapter adapter = new UserAdapter(users);
                    recyclerView.setAdapter(adapter);
                } else {
                    Log.e("MainActivity", "Error en la respuesta: " + response.code());
                    Toast.makeText(MainActivity.this, "No se encontraron usuarios", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.e("MainActivity", "Error en la solicitud: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Algo salió mal... Inténtalo nuevamente.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
