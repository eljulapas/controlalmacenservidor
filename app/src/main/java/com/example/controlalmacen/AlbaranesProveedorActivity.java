package com.example.controlalmacen;

import android.os.Bundle;

import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.controlalmacen.entities.Albaran;
import com.example.controlalmacen.instances.AlbaranInstance;
import com.example.controlalmacen.repositories.AlbaranInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class AlbaranesProveedorActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AlbaranInterface albaranInterface;
    private Long proveedorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albaranes_proveedor);

        proveedorId = getIntent().getLongExtra("proveedorId", -1);
        recyclerView = findViewById(R.id.recyclerViewAlbaranes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        albaranInterface = AlbaranInstance.getRetrofitInstance().create(AlbaranInterface.class);

        cargarAlbaranes();
    }

    private void cargarAlbaranes() {
        albaranInterface.getByProveedor(proveedorId).enqueue(new Callback<List<Albaran>>() {
            @Override
            public void onResponse(Call<List<Albaran>> call, Response<List<Albaran>> response) {
                if (response.isSuccessful()) {
                    recyclerView.setAdapter(new AlbaranAdapter(response.body(), AlbaranesProveedorActivity.this));
                }
            }

            @Override
            public void onFailure(Call<List<Albaran>> call, Throwable t) {
                Toast.makeText(AlbaranesProveedorActivity.this, "Error al cargar albaranes", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
