package com.example.controlalmacen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.controlalmacen.entities.User;
import com.example.controlalmacen.instances.UserInstance;
import com.example.controlalmacen.repositories.UserInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgregarUsuarioActivity extends AppCompatActivity {

    private ImageView imageUsuario;
    private Button btnSeleccionarFoto, btnGuardarUsuario, btnCancelarUsuario;
    private EditText etNombreUsuario, etEmail, etPassword;
    private CheckBox checkAdmin;
    private String imageUri = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_usuario);

        imageUsuario = findViewById(R.id.image_usuario);
        btnSeleccionarFoto = findViewById(R.id.btn_seleccionar_foto_usuario);
        etNombreUsuario = findViewById(R.id.et_nombre_usuario);
        checkAdmin = findViewById(R.id.check_admin);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnGuardarUsuario = findViewById(R.id.btn_guardar_usuario);
        btnCancelarUsuario = findViewById(R.id.btn_cancelar_usuario);

        btnSeleccionarFoto.setOnClickListener(v -> abrirSelectorDeImagen());
        btnGuardarUsuario.setOnClickListener(v -> guardarUsuario());
        btnCancelarUsuario.setOnClickListener(v -> finish());

        checkAdmin.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                etEmail.setVisibility(View.VISIBLE);
                etPassword.setVisibility(View.VISIBLE);
            } else {
                etEmail.setVisibility(View.GONE);
                etPassword.setVisibility(View.GONE);
            }
        });
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
            imageUsuario.setImageURI(selectedImage);
            imageUri = selectedImage.toString();
        }
    }

    private void guardarUsuario() {
        String nombre = etNombreUsuario.getText().toString().trim();
        boolean esAdmin = checkAdmin.isChecked();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (nombre.isEmpty()) {
            Toast.makeText(this, "El nombre es obligatorio.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (esAdmin && (email.isEmpty() || password.isEmpty())) {
            Toast.makeText(this, "Email y password son obligatorios para administradores.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear usuario
        User nuevoUsuario = new User();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setFoto(imageUri);
        nuevoUsuario.setIsAdmin(esAdmin);
        nuevoUsuario.setHabilitado(true); // siempre habilitado por defecto

        if (esAdmin) {
            nuevoUsuario.setEmail(email);
            nuevoUsuario.setPassword(password);
        }

        agregarUsuarioAPI(nuevoUsuario);
    }

    private void agregarUsuarioAPI(User usuario) {
        UserInterface userInterface = UserInstance.getRetrofitInstance().create(UserInterface.class);
        Call<User> call = userInterface.agregarUsuario(usuario);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AgregarUsuarioActivity.this, "Usuario guardado con éxito.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Sin cuerpo de error";
                        Toast.makeText(AgregarUsuarioActivity.this, "Error al guardar usuario. Código: " + response.code() + "\n" + errorBody, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(AgregarUsuarioActivity.this, "Error desconocido al guardar usuario.", Toast.LENGTH_SHORT).show();
                    }
                }
            }


            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(AgregarUsuarioActivity.this, "Error en la conexión.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
