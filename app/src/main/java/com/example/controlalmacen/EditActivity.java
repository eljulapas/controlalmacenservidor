package com.example.controlalmacen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.bumptech.glide.Glide;
import com.example.controlalmacen.R;
import com.example.controlalmacen.entities.Producto;
import com.example.controlalmacen.instances.ProductoInstance;
import com.example.controlalmacen.repositories.ProductosInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditActivity extends AppCompatActivity {

    // Declarar las variables
    private EditText etNombre, etCantidad, etCantidadMinima;
    private ImageView imageViewProducto;
    private CheckBox checkBoxHabilitado;
    private Button btnEliminar, btnEditarFoto;

    private ProductosInterface productosInterface;

    private long productoId;

    private Button btnActualizar;


    // para guardar la imagen seleccionada
    private String imageUri = "";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);


        productoId = getIntent().getLongExtra("PRODUCTO_ID", -1);
        String nombre = getIntent().getStringExtra("PRODUCTO_NOMBRE");
        int cantidad = getIntent().getIntExtra("PRODUCTO_CANTIDAD", 0);
        int cantidadMinima = getIntent().getIntExtra("PRODUCTO_MINIMO", 0);
        String imagenUrl = getIntent().getStringExtra("PRODUCTO_IMAGEN_URL");


        etNombre = findViewById(R.id.et_nombre);
        etCantidad = findViewById(R.id.et_cantidad);
        etCantidadMinima = findViewById(R.id.et_cantidad_minima);
        imageViewProducto = findViewById(R.id.image_producto);
        checkBoxHabilitado = findViewById(R.id.checkbox_habilitado);
        btnEliminar = findViewById(R.id.btn_eliminar);
        btnActualizar = findViewById(R.id.btn_actualizar);
        btnEditarFoto = findViewById(R.id.btn_editar_foto);




        etNombre.setText(nombre);
        etCantidad.setText(String.valueOf(cantidad));
        etCantidadMinima.setText(String.valueOf(cantidadMinima));

        // Guardar la url inicial
        imageUri = imagenUrl != null ? imagenUrl : "";

        productosInterface = ProductoInstance.getRetrofitInstance().create(ProductosInterface.class);



        if (imagenUrl != null && !imagenUrl.isEmpty()) {
            Glide.with(this)
                    .load(Uri.parse(imagenUrl))
                    .placeholder(R.drawable.diet)
                    .error(R.drawable.diet)
                    .into(imageViewProducto);
        } else {
            Glide.with(this)
                    .load(R.drawable.diet)
                    .into(imageViewProducto);
        }



        btnEliminar.setOnClickListener(v -> eliminarProducto());

        btnActualizar.setOnClickListener(v -> actualizarProducto(productoId));

        //Botón para seleccionar foto
        btnEditarFoto.setOnClickListener(v -> abrirSelectorDeImagen());

    }

    // Abrir galería
    private void abrirSelectorDeImagen() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 100);
    }

    // Recibir la foto seleccionada
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            imageViewProducto.setImageURI(selectedImage);
            imageUri = selectedImage.toString(); // Actualiza imageUri para guardar luego
        }
    }

    // Menú superior
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_drawable, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_edit_product) {
            Toast.makeText(this, "Editar producto", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.menu_disable_product) {
            checkBoxHabilitado.setChecked(!checkBoxHabilitado.isChecked());
            Toast.makeText(this, "Habilitado: " + checkBoxHabilitado.isChecked(), Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.menu_delete_product) {
            btnEliminar.performClick();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    // Método para eliminar producto
    private void eliminarProducto() {
        if (productoId == -1) {
            Toast.makeText(this, "Error: Producto no válido", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<Void> call = productosInterface.deleteProducto(productoId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditActivity.this, "Producto eliminado correctamente", Toast.LENGTH_SHORT).show();
                    finish(); // Vuelve a la pantalla anterior
                } else {
                    Toast.makeText(EditActivity.this, "Error al eliminar producto", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EditActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void actualizarProducto(long productoId) {
        String nombre = etNombre.getText().toString().trim();
        int cantidad = Integer.parseInt(etCantidad.getText().toString().trim());
        int cantidadMinima = Integer.parseInt(etCantidadMinima.getText().toString().trim());
        //boolean habilitado = checkBoxHabilitado.isChecked();

        Producto productoActualizado = new Producto();
        productoActualizado.setId(productoId);
        productoActualizado.setNombre(nombre);
        productoActualizado.setCantidad(cantidad);
        productoActualizado.setMinimo(cantidadMinima);
        productoActualizado.setImagen(imageUri);
        //productoActualizado.setHabilitado(habilitado);

        productosInterface.updateProducto(productoId, productoActualizado).enqueue(new Callback<Producto>() {
            @Override
            public void onResponse(Call<Producto> call, Response<Producto> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditActivity.this, "Producto actualizado correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditActivity.this, "Error al actualizar producto", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Producto> call, Throwable t) {
                Toast.makeText(EditActivity.this, "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}