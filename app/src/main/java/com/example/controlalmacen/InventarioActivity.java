package com.example.controlalmacen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.controlalmacen.entities.Producto;
import com.example.controlalmacen.instances.ProductoInstance;
import com.example.controlalmacen.repositories.ProductosInterface;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;


import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventarioActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private InventarioAdapter adapter;
    private List<Producto> productos = new ArrayList<>();

    private Button btnTodos, btnBajoStock, btnDeshabilitados, btnEnviarCorreo, btnAtras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario);

        recyclerView = findViewById(R.id.recycler_inventario);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new InventarioAdapter(productos);
        recyclerView.setAdapter(adapter);

        btnTodos = findViewById(R.id.btn_todos);
        btnBajoStock = findViewById(R.id.btn_bajo_stock);
        btnDeshabilitados = findViewById(R.id.btn_deshabilitados);
        btnEnviarCorreo = findViewById(R.id.btn_enviar_correo);
        btnAtras = findViewById(R.id. btn_atras);

        cargarProductos();

        btnTodos.setOnClickListener(v -> mostrarTodos());
        btnBajoStock.setOnClickListener(v -> mostrarStockBajo());
        btnDeshabilitados.setOnClickListener(v -> mostrarDeshabilitados());
        btnEnviarCorreo.setOnClickListener(v -> generarYEnviarExcel());

        btnAtras.setOnClickListener(v -> {
            Intent intent = new Intent(InventarioActivity.this, PerfilesActivity.class);
            startActivity(intent);
            finish();
        });

    }

    private void cargarProductos() {
        ProductosInterface productoInterface = ProductoInstance.getRetrofitInstance().create(ProductosInterface.class);
        Call<List<Producto>> call = productoInterface.getAllProductos();

        call.enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productos = response.body();
                    mostrarTodos();
                } else {
                    Toast.makeText(InventarioActivity.this, "Error al obtener productos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                Toast.makeText(InventarioActivity.this, "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarTodos() {
        List<Producto> ordenados = new ArrayList<>(productos);
        ordenados.sort(Comparator
                .comparing(Producto::getHabilitado).reversed()
                .thenComparing(p -> p.getCantidad() <= p.getMinimo() ? 0 : 1)); // los con bajo stock primero
        adapter.setProductos(ordenados);
    }

    private void mostrarStockBajo() {
        List<Producto> filtrados = productos.stream()
                .filter(p -> p.getCantidad() <= p.getMinimo())
                .collect(Collectors.toList());
        adapter.setProductos(filtrados);
    }

    private void mostrarDeshabilitados() {
        List<Producto> filtrados = productos.stream()
                .filter(p -> p.getHabilitado() != null && !p.getHabilitado())
                .collect(Collectors.toList());
        adapter.setProductos(filtrados);
    }

    private void generarYEnviarExcel() {
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "inventario.xls");

        try {
            WritableWorkbook workbook = Workbook.createWorkbook(file);
            WritableSheet sheet = workbook.createSheet("Inventario", 0);

            // Encabezados
            sheet.addCell(new Label(0, 0, "Nombre"));
            sheet.addCell(new Label(1, 0, "Cantidad"));
            sheet.addCell(new Label(2, 0, "Cantidad Mínima"));

            List<Producto> productosVisibles = adapter.getProductos();
            for (int i = 0; i < productosVisibles.size(); i++) {
                Producto p = productosVisibles.get(i);
                sheet.addCell(new Label(0, i + 1, p.getNombre()));
                sheet.addCell(new jxl.write.Number(1, i + 1, p.getCantidad()));
                sheet.addCell(new jxl.write.Number(2, i + 1, p.getMinimo()));
            }

            workbook.write();
            workbook.close();

            enviarCorreo(file);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al generar Excel", Toast.LENGTH_SHORT).show();
        }
    }


    private void enviarCorreo(File archivo) {
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", archivo);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/vnd.ms-excel");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Informe de Inventario");
        intent.putExtra(Intent.EXTRA_TEXT, "Adjunto el informe actual del inventario.");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(intent, "Enviar correo con..."));
    }

}
