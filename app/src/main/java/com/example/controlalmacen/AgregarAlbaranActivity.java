package com.example.controlalmacen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.controlalmacen.entities.Albaran;
import com.example.controlalmacen.entities.Proveedor;
import com.example.controlalmacen.instances.AlbaranInstance;
import com.example.controlalmacen.repositories.AlbaranInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AgregarAlbaranActivity extends AppCompatActivity {

    private ImageView imageAlbaran;
    private Button btnSeleccionarFoto, btnGuardarAlbaran, btnCancelar;
    private EditText etCIF, etNombre, etImporte, etFechaPago;
    private CheckBox checkPagado;
    private String imageUri = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_albaran);

        imageAlbaran = findViewById(R.id.image_albaran);
        btnSeleccionarFoto = findViewById(R.id.btn_seleccionar_foto);
        etCIF = findViewById(R.id.et_cif);
        etNombre = findViewById(R.id.et_nombre);
        etImporte = findViewById(R.id.et_importe);
        etFechaPago = findViewById(R.id.et_fecha_pago);
        checkPagado = findViewById(R.id.check_pagado);
        btnGuardarAlbaran = findViewById(R.id.btn_guardar_albaran);
        btnCancelar = findViewById(R.id.btn_cancelar);

        btnSeleccionarFoto.setOnClickListener(v -> abrirSelectorDeImagen());
        btnGuardarAlbaran.setOnClickListener(v -> guardarAlbaran());
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
            imageAlbaran.setImageURI(selectedImage);
            imageUri = selectedImage.toString();
        }
    }

    private void guardarAlbaran() {
        String cif = etCIF.getText().toString().trim();
        String nombre = etNombre.getText().toString().trim();
        String importeStr = etImporte.getText().toString().trim();
        String fechaPagoStr = etFechaPago.getText().toString().trim();

        if (cif.isEmpty() || nombre.isEmpty() || importeStr.isEmpty()) {
            Toast.makeText(this, "Los campos CIF, Nombre y Importe son obligatorios.", Toast.LENGTH_SHORT).show();
            return;
        }

        double importe;
        try {
            importe = Double.parseDouble(importeStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Importe no válido.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean pagado = checkPagado.isChecked();
        String fechaHoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Crear proveedor
        Proveedor proveedor = new Proveedor();
        proveedor.setCif(cif);
        proveedor.setNombre(nombre);

        // Crear albarán
        Albaran nuevoAlbaran = new Albaran();
        nuevoAlbaran.setFecha(fechaHoy);
        nuevoAlbaran.setCantidad(importe);
        nuevoAlbaran.setPagado(pagado);
        nuevoAlbaran.setFotoUrl(imageUri);
        nuevoAlbaran.setProveedor(proveedor);

        if (!fechaPagoStr.isEmpty()) {
            nuevoAlbaran.setFechaPago(fechaPagoStr);
        }

        agregarAlbaranAPI(nuevoAlbaran);
    }

    private void agregarAlbaranAPI(Albaran albaran) {
        AlbaranInterface albaranInterface = AlbaranInstance.getRetrofitInstance().create(AlbaranInterface.class);
        Call<Albaran> call = albaranInterface.agregarAlbaran(albaran);

        call.enqueue(new Callback<Albaran>() {
            @Override
            public void onResponse(Call<Albaran> call, Response<Albaran> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AgregarAlbaranActivity.this, "Albarán guardado con éxito.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AgregarAlbaranActivity.this, "Error al guardar albarán.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Albaran> call, Throwable t) {
                Toast.makeText(AgregarAlbaranActivity.this, "Error en la conexión.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
