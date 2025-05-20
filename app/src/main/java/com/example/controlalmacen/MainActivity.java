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
import com.example.controlalmacen.entities.Producto;
import com.example.controlalmacen.entities.User;
import com.example.controlalmacen.instances.ProductoInstance;
import com.example.controlalmacen.instances.UserInstance;
import com.example.controlalmacen.repositories.ProductosInterface;
import com.example.controlalmacen.repositories.UserInterface;
import com.google.android.material.navigation.NavigationView;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private UserInterface userInterface;
    private ProductosInterface productosInterface;

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private boolean adminAutenticado = false;

    private User adminActual = null; // El administrador que se autentica

    // 🔐 AÑADIDO: SharedPreferences
    private SharedPreferences prefs;


    private Handler handlerInactividad = new Handler();
    private Runnable tareaInactividad;
    private static final long TIEMPO_INACTIVIDAD_MS = 60 * 1000; // 60 segundos

    private EditText editTextBuscarUsuario;
    private List<User> listaUsuarios = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 🔐 AÑADIDO: inicializar prefs
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // 🔐 AÑADIDO: verificar si ha pasado el tiempo de inactividad
        long ahora = System.currentTimeMillis();
        long ultimaVerificacion = prefs.getLong("clave_verificada_timestamp", 0);
        if (ahora - ultimaVerificacion >  60 * 1000) {
            prefs.edit().putBoolean("clave_verificada", false).apply();
        }

        adminAutenticado = prefs.getBoolean("clave_verificada", false);


        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView.setNavigationItemSelectedListener(item -> {
            if (!adminAutenticado) {
                mostrarDialogoSeleccionAdmin(() -> manejarOpcionAdmin(item.getItemId()));
            } else {
                manejarOpcionAdmin(item.getItemId());
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        userInterface = UserInstance.getRetrofitInstance().create(UserInterface.class);
        productosInterface = ProductoInstance.getRetrofitInstance().create(ProductosInterface.class);

        recyclerView = findViewById(R.id.recyclerViewUsuarios);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        editTextBuscarUsuario = findViewById(R.id.editTextBuscarUsuario);

        editTextBuscarUsuario.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarUsuarios(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        fetchUsers();
        iniciarTemporizadorInactividad();

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

    // 🔐 Seleccionar admin y pedir clave
    private void mostrarDialogoSeleccionAdmin(Runnable onSuccess) {
        Call<List<User>> call = userInterface.getAllUsers();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(MainActivity.this, "Error al obtener usuarios", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<User> admins = new ArrayList<>();
                for (User u : response.body()) {
                    if (Boolean.TRUE.equals(u.getIsAdmin())) {
                        admins.add(u);
                    }
                }

                if (admins.isEmpty()) {
                    Toast.makeText(MainActivity.this, "No hay administradores disponibles", Toast.LENGTH_SHORT).show();
                    return;
                }

                String[] nombres = new String[admins.size()];
                for (int i = 0; i < admins.size(); i++) {
                    nombres[i] = admins.get(i).getNombre() + " (" + admins.get(i).getEmail() + ")";
                }

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Selecciona un administrador")
                        .setItems(nombres, (dialog, which) -> {
                            User seleccionado = admins.get(which);
                            pedirClaveParaAdmin(seleccionado, onSuccess);
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void pedirClaveParaAdmin(User admin, Runnable onSuccess) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_clave_admin, null);
        EditText claveInput = view.findViewById(R.id.editTextClave);

        new AlertDialog.Builder(this)
                .setView(view)
                .setTitle("Clave para " + admin.getNombre())
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    String clave = claveInput.getText().toString();
                    userInterface.login(admin.getEmail(), clave).enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                adminAutenticado = true;
                                adminActual = admin;

                                // 🔐 AÑADIDO: guardar estado de verificación y timestamp
                                prefs.edit()
                                        .putBoolean("clave_verificada", true)
                                        .putLong("clave_verificada_timestamp", System.currentTimeMillis())
                                        .apply();

                                onSuccess.run();
                            } else {
                                Toast.makeText(MainActivity.this, "Clave incorrecta", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Toast.makeText(MainActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void manejarOpcionAdmin(int itemId) {
        Intent intent = null;


        if (itemId == R.id.nav_nuevo_perfil) {
            intent = new Intent(this, AgregarUsuarioActivity.class);
        } else if (itemId == R.id.nav_editar_perfil) {
            intent = new Intent(this, EditarUsuarioActivity.class);
        } else if (itemId == R.id.nav_nuevo_albaran) {
            intent = new Intent(this, AgregarAlbaranActivity.class);
        } else if (itemId == R.id.nav_generar_informes) {
            intent = new Intent(this, InformeAlbaranesActivity.class);
        } else if (itemId == R.id.nav_perfil) {
            intent = new Intent(this, PerfilesActivity.class);

        } else if (itemId == R.id.nav_cerrar_sesion) {
            prefs.edit().clear().apply();
            adminAutenticado = false;
            adminActual = null;
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
        }


        if (intent != null) startActivity(intent);
    }


    private void iniciarTemporizadorInactividad() {
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
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            fetchUsers();
            Toast.makeText(this, "Usuario actualizado correctamente.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void filtrarUsuarios(String texto) {
        List<User> usuariosFiltrados = new ArrayList<>();
        for (User p : listaUsuarios) {
            if (p.getNombre().toLowerCase().contains(texto.toLowerCase())) {
                usuariosFiltrados.add(p);
            }
        }
        userAdapter.actualizarLista(usuariosFiltrados);
    }


    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        reiniciarTemporizadorInactividad();
    }

}
