package com.example.controlalmacen;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.controlalmacen.entities.Proveedor;
import com.example.controlalmacen.instances.ProveedorInstance;
import com.example.controlalmacen.repositories.ProveedorInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class ProveedorActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button btnAgregarProveedor;
    private ProveedorInterface proveedorInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proveedor);

        recyclerView = findViewById(R.id.recyclerViewProveedores);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnAgregarProveedor = findViewById(R.id.btnAgregarProveedor);
        btnAgregarProveedor.setOnClickListener(v -> mostrarDialogoAgregarProveedor());

        proveedorInterface = ProveedorInstance.getInstance().create(ProveedorInterface.class);
        cargarProveedores();
    }

    private void cargarProveedores() {
        proveedorInterface.getAllProveedores().enqueue(new Callback<List<Proveedor>>() {
            @Override
            public void onResponse(Call<List<Proveedor>> call, Response<List<Proveedor>> response) {
                if (response.isSuccessful()) {
                    List<Proveedor> lista = response.body();
                    recyclerView.setAdapter(new ProveedorAdapter(lista, ProveedorActivity.this));
                }
            }

            @Override
            public void onFailure(Call<List<Proveedor>> call, Throwable t) {
                Toast.makeText(ProveedorActivity.this, "Error al cargar proveedores", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogoAgregarProveedor() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_agregar_proveedor, null);
        AlertDialog dialog = new AlertDialog.Builder(this).setView(dialogView).create();

        EditText nombre = dialogView.findViewById(R.id.editNombreProveedor);
        EditText email = dialogView.findViewById(R.id.editEmailProveedor);
        EditText telefono = dialogView.findViewById(R.id.editTelefonoProveedor);
        EditText cif = dialogView.findViewById(R.id.editCifProveedor);
        Button btnGuardar = dialogView.findViewById(R.id.btnGuardarProveedor);

        btnGuardar.setOnClickListener(v -> {
            Proveedor nuevo = new Proveedor(null, nombre.getText().toString(), email.getText().toString(), telefono.getText().toString(), cif.getText().toString());

            proveedorInterface.agregarProveedor(nuevo).enqueue(new Callback<Proveedor>() {
                @Override
                public void onResponse(Call<Proveedor> call, Response<Proveedor> response) {
                    Toast.makeText(ProveedorActivity.this, "Proveedor agregado", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    cargarProveedores();
                }

                @Override
                public void onFailure(Call<Proveedor> call, Throwable t) {
                    Toast.makeText(ProveedorActivity.this, "Error al agregar", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }
}
