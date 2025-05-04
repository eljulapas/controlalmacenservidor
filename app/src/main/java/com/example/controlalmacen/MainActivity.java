package com.example.controlalmacen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.controlalmacen.entities.Producto;
import com.example.controlalmacen.entities.User;
import com.example.controlalmacen.instances.ProductoInstance;
import com.example.controlalmacen.instances.UserInstance;
import com.example.controlalmacen.repositories.ProductosInterface;
import com.example.controlalmacen.repositories.UserInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private UserInterface userInterface;
    private RecyclerView recyclerView;
    private Button btnAgregarProducto, btnInformeAlbaranes;
    private ProductosInterface productosInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configurar Retrofit
        userInterface = UserInstance.getRetrofitInstance().create(UserInterface.class);

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.recyclerViewUsuarios);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        Button btnAlbaranes = findViewById(R.id.btnAlbaranes);
        btnAlbaranes.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AgregarAlbaranActivity.class);
            startActivity(intent);
        });

        // Botón para ir a Informe Albaranes
        Button btnInformeAlbaranes = findViewById(R.id.btnInformeAlbaranes);
        btnInformeAlbaranes.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, InformeAlbaranesActivity.class);
            startActivity(intent);
        });

        Button btnAgregarNuevoProducto = findViewById(R.id.btnAgregarProducto);
        btnAgregarNuevoProducto.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AgregarProductoActivity.class);
            startActivity(intent);
        });



        productosInterface = ProductoInstance.getRetrofitInstance().create(ProductosInterface.class);

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
