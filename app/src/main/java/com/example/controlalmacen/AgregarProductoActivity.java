package com.example.controlalmacen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.controlalmacen.entities.Producto;
import com.example.controlalmacen.instances.ProductoInstance;

import com.example.controlalmacen.repositories.ProductosInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgregarProductoActivity extends AppCompatActivity {

    private ImageView imageProducto;
    private Button btnSeleccionarFoto, btnGuardarProducto, btnCancelar;
    private EditText etNombre, etCantidad, etMinimo;
    private String imageUri = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_producto);

        imageProducto = findViewById(R.id.image_producto);
        btnSeleccionarFoto = findViewById(R.id.btn_seleccionar_foto);
        etNombre = findViewById(R.id.et_nombre);
        etCantidad = findViewById(R.id.et_cantidad);
        etMinimo = findViewById(R.id.et_minimo);
        btnGuardarProducto = findViewById(R.id.btn_guardar_producto);
        btnCancelar = findViewById(R.id.btn_cancelar);

        btnSeleccionarFoto.setOnClickListener(v -> abrirSelectorDeImagen());
        btnGuardarProducto.setOnClickListener(v -> guardarProducto());
        btnCancelar.setOnClickListener(v -> finish());
    }

    private void abrirSelectorDeImagen() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            imageProducto.setImageURI(selectedImage);
            imageUri = selectedImage.toString();
        }
    }

    private void guardarProducto() {
        String nombre = etNombre.getText().toString().trim();
        String cantidadStr = etCantidad.getText().toString().trim();
        String minimoStr = etMinimo.getText().toString().trim();

        if (nombre.isEmpty() || cantidadStr.isEmpty() || minimoStr.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios.", Toast.LENGTH_SHORT).show();
            return;
        }

        int cantidad, minimo;
        try {
            cantidad = Integer.parseInt(cantidadStr);
            minimo = Integer.parseInt(minimoStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Cantidad o mínimo no válidos.", Toast.LENGTH_SHORT).show();
            return;
        }

        Producto nuevoProducto = new Producto();
        nuevoProducto.setNombre(nombre);
        nuevoProducto.setCantidad(cantidad);
        nuevoProducto.setMinimo(minimo);
        nuevoProducto.setImagen(imageUri);

        agregarProductoAPI(nuevoProducto);
    }

    private void agregarProductoAPI(Producto producto) {
        ProductosInterface productoInterface = ProductoInstance.getRetrofitInstance().create(ProductosInterface.class);
        Call<Producto> call = productoInterface.agregarProducto(producto);

        call.enqueue(new Callback<Producto>() {
            @Override
            public void onResponse(Call<Producto> call, Response<Producto> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AgregarProductoActivity.this, "Producto guardado con éxito.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AgregarProductoActivity.this, "Error al guardar producto.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Producto> call, Throwable t) {
                Toast.makeText(AgregarProductoActivity.this, "Error en la conexión.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
