package com.example.controlalmacen;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.controlalmacen.entities.Producto;
import com.example.controlalmacen.instances.ProductoInstance;
import com.example.controlalmacen.repositories.ProductosInterface;
import com.google.android.material.navigation.NavigationView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class ProductActivity extends AppCompatActivity {

    private ProductosInterface productosInterface;
    private RecyclerView recyclerView;
    private ProductoAdapter adapter;
    private List<Producto> productos = new ArrayList<>(); // Inicializar para evitar NullPointerException


    private EditText searchProduct;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        // Configurar Retrofit
        productosInterface = ProductoInstance.getRetrofitInstance().create(ProductosInterface.class);

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.recyclerViewProductos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar EditText de búsqueda
        searchProduct = findViewById(R.id.search_product);
        searchProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarProductos(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Configurar DrawerLayout y NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);

        // Configurar la Toolbar
        setSupportActionBar(toolbar);

        // Configurar el item seleccionado del menú lateral
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        // Cargar productos al abrir la actividad
        fetchProductos();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Aquí se gestiona la apertura y cierre del Drawer al hacer click en el icono de la Toolbar
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public boolean onNavigationItemSelected(MenuItem menuItem) {

        int id = menuItem.getItemId();

        if (id == R.id.menu_edit_product) {
            Toast.makeText(this, "Editar producto", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.menu_disable_product) {
            Toast.makeText(this, "Deshabilitar producto", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.menu_delete_product) {
            Toast.makeText(this, "Eliminar producto", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.menu_admin_option) {
            Toast.makeText(this, "Opción solo admin", Toast.LENGTH_SHORT).show();

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    private void fetchProductos() {
        Call<List<Producto>> call = productosInterface.getAllProductos();

        call.enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productos = response.body();
                    Log.d("ProductActivity", "Productos obtenidos: " + productos.size());

                    // Adaptador con los datos de los productos
                    adapter = new ProductoAdapter(productos);
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

    private void filtrarProductos(String texto) {
        List<Producto> productosFiltrados = new ArrayList<>();
        for (Producto p : productos) {
            if (p.getNombre().toLowerCase().contains(texto.toLowerCase())) {
                productosFiltrados.add(p);
            }
        }
        adapter.actualizarLista(productosFiltrados);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchProductos(); // Refresca los productos al volver
    }
}
