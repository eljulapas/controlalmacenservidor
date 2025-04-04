package com.example.controlalmacen;

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
    private Button btnAgregarProducto;
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

        // Botón para agregar producto
        btnAgregarProducto = findViewById(R.id.btnAgregarProducto);
        btnAgregarProducto.setOnClickListener(v -> mostrarDialogoAgregarProducto());

        productosInterface = ProductoInstance.getRetrofitInstance().create(ProductosInterface.class);

        // Cargar los usuarios al abrir la app
        fetchUsers();
    }

    private void mostrarDialogoAgregarProducto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_agregar_producto, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        EditText editNombre = dialogView.findViewById(R.id.editNombre);
        EditText editCantidad = dialogView.findViewById(R.id.editCantidad);
        EditText editMinimo = dialogView.findViewById(R.id.editMinimo);
        EditText editImagenUrl = dialogView.findViewById(R.id.editImagenUrl); // Nuevo campo para la URL de la imagen
        Button btnGuardar = dialogView.findViewById(R.id.btnGuardar);

        btnGuardar.setOnClickListener(v -> {
            String nombre = editNombre.getText().toString().trim();
            String cantidadStr = editCantidad.getText().toString().trim();
            String minimoStr = editMinimo.getText().toString().trim();
            String imagenUrl = editImagenUrl.getText().toString().trim(); // Tomar el valor de la URL

            if (nombre.isEmpty() || cantidadStr.isEmpty()) {
                Toast.makeText(MainActivity.this, "Nombre y cantidad son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            int cantidad = Integer.parseInt(cantidadStr);
            int minimo = minimoStr.isEmpty() ? 0 : Integer.parseInt(minimoStr);

            agregarProducto(nombre, cantidad, minimo, imagenUrl, dialog);
        });

        dialog.show();
    }

    private void agregarProducto(String nombre, int cantidad, int minimo, String imagenUrl, AlertDialog dialog) {
        // Verificar si el producto con el mismo nombre ya existe
        Call<Boolean> call = productosInterface.productoExiste(nombre);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean existeProducto = response.body();

                    if (existeProducto) {
                        // Mostrar un mensaje indicando que el producto ya existe
                        Toast.makeText(MainActivity.this, "¡El producto ya existe en la base de datos!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss(); // Cerrar el diálogo de agregar producto
                    } else {
                        // Si no existe, proceder a agregar el nuevo producto
                        Producto nuevoProducto = new Producto(null, nombre, imagenUrl, cantidad, minimo);

                        // Llamada para agregar el producto
                        Call<Producto> agregarCall = productosInterface.agregarProducto(nuevoProducto);
                        agregarCall.enqueue(new Callback<Producto>() {
                            @Override
                            public void onResponse(Call<Producto> call, Response<Producto> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "Producto agregado con éxito", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(MainActivity.this, "Error al agregar producto", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Producto> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Fallo en la conexión", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Error al verificar existencia del producto", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Fallo en la conexión", Toast.LENGTH_SHORT).show();
            }
        });
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
