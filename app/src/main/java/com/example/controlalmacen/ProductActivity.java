package com.example.controlalmacen;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.controlalmacen.entities.Producto;
import com.example.controlalmacen.instances.ProductoInstance;
import com.example.controlalmacen.instances.UserInstance;
import com.example.controlalmacen.repositories.ProductosInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class ProductActivity extends AppCompatActivity {

    private ProductosInterface productosInterface;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        // Configurar Retrofit
        productosInterface = ProductoInstance.getRetrofitInstance().create(ProductosInterface.class);

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.recyclerViewProductos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Cargar los productos al abrir la actividad
        fetchProductos();
    }

    private void fetchProductos() {
        Call<List<Producto>> call = productosInterface.getAllProductos();

        call.enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Producto> productos = response.body();
                    Log.d("ProductActivity", "Productos obtenidos: " + productos.size());

                    // Adaptador con los datos de los productos
                    ProductoAdapter adapter = new ProductoAdapter(productos);
                    recyclerView.setAdapter(adapter);
                } else {
                    Log.e("ProductActivity", "Error en la respuesta: " + response.code());
                    Toast.makeText(ProductActivity.this, "No se encontraron productos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                Log.e("ProductActivity", "Error en la solicitud: " + t.getMessage());
                Toast.makeText(ProductActivity.this, "Algo salió mal... Inténtalo nuevamente.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
