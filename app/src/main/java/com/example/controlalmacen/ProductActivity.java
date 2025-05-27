package com.example.controlalmacen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
    private List<Producto> productos = new ArrayList<>();


    private EditText searchProduct;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Intent intent = getIntent();
        String usuarioNombre = intent.getStringExtra("usuarioNombre");
        boolean isAdmin = intent.getBooleanExtra("usuarioIsAdmin", false);


        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        recyclerView = findViewById(R.id.recyclerViewProductos);
        searchProduct = findViewById(R.id.search_product);
        Button btnAgregarNuevoProducto = findViewById(R.id.btnAgregarProducto);

        // Mostrar u ocultar menú lateral

        if (isAdmin) {
            navigationView.setVisibility(View.VISIBLE);

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            navigationView.setNavigationItemSelectedListener(item -> {
                int id = item.getItemId();
                Intent navIntent = null;

                if (id == R.id.nav_nuevo_perfil) {
                    navIntent = new Intent(this, AgregarUsuarioActivity.class);
                } else if (id == R.id.nav_generar_informes) {
                    navIntent = new Intent(this, InformeAlbaranesActivity.class);
                } else if (id == R.id.nav_perfil) {
                    navIntent = new Intent(this, PerfilesActivity.class);
                } else if (id == R.id.nav_inventario) {
                    navIntent = new Intent(this, InventarioActivity.class);
                }

                if (navIntent != null) startActivity(navIntent);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            });
        } else {
            navigationView.setVisibility(View.GONE); // Ocultar para no-admins
        }


        // Configurar Retrofit
        productosInterface = ProductoInstance.getRetrofitInstance().create(ProductosInterface.class);

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.recyclerViewProductos);

        //que el comportamiento sea más dinámico (horizontal en tablets o landscape, vertical en móviles o portrait
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        }



        // Configurar DrawerLayout y NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);

        // Cargar productos al abrir la actividad
        fetchProductos();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout,  R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

         btnAgregarNuevoProducto.setOnClickListener(v -> {
            Intent agregarProductoIntent  = new Intent(ProductActivity.this, AgregarProductoActivity.class);
            startActivity(agregarProductoIntent );
        });

        Button btnAtras = findViewById(R.id.btn_atras);
        btnAtras.setOnClickListener(v -> {
            Intent VolverAtrasIntent = new Intent(ProductActivity.this, MainActivity.class);
            startActivity(VolverAtrasIntent);
            finish();
        });


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


    //Código para futura mejora

    /*private void fetchProductosFrecuentes(Long userId) {
    InteraccionInterface interaccionInterface = ApiClient.getClient().create(InteraccionInterface.class);

    Call<List<Interaccion>> call = interaccionInterface.getUltimasInteracciones(userId);
    call.enqueue(new Callback<List<Interaccion>>() {
        @Override
        public void onResponse(Call<List<Interaccion>> call, Response<List<Interaccion>> response) {
            if (response.isSuccessful() && response.body() != null) {
                List<Producto> productosUsados = new ArrayList<>();
                for (Interaccion i : response.body()) {
                    productosUsados.add(i.getProducto());
                }


                adapter.actualizarLista(productosUsados);
            } else {
                Log.e("API", "Error en la respuesta: " + response.code());
            }
        }

        @Override
        public void onFailure(Call<List<Interaccion>> call, Throwable t) {
            Log.e("API", "Error de conexión: " + t.getMessage());
        }
    });
}
*/

    private void filtrarProductos(String texto) {
        if (adapter == null) {
            Log.w("ProductActivity", "Adapter aún no inicializado. No se puede filtrar.");
            return;
        }
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
        fetchProductos(); // Recarga los productos al volver
    }



}
