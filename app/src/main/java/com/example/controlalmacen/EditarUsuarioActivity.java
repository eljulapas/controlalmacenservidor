package com.example.controlalmacen;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.controlalmacen.entities.User;
import com.example.controlalmacen.instances.UserInstance;
import com.example.controlalmacen.repositories.UserInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditarUsuarioActivity extends AppCompatActivity {

    private ImageView imageUsuario;
    private Button btnSeleccionarFoto, btnGuardarCambios, btnEliminarUsuario, btnCancelar;
    private EditText etNombreUsuario, etEmail, etPassword;
    private CheckBox checkAdmin;
    private String imageUri = "";
    private User usuarioActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_usuario);

        // Obtener usuario enviado
        // Recoger datos enviados por intent
        long usuarioId = getIntent().getLongExtra("usuarioId", -1L);
        String usuarioNombre = getIntent().getStringExtra("usuarioNombre");
        String usuarioEmail = getIntent().getStringExtra("usuarioEmail");
        boolean usuarioIsAdmin = getIntent().getBooleanExtra("usuarioIsAdmin", false);
        String usuarioFoto = getIntent().getStringExtra("usuarioFoto");
        String usuarioPassword = getIntent().getStringExtra("usuarioPassword");

        // Crear usuario actual temporal adaptado a la clase User
        usuarioActual = new User(
                 usuarioId,
                usuarioNombre,
                usuarioFoto,
                usuarioPassword,
                true, // habilitado por defecto
                usuarioIsAdmin,
                usuarioEmail
        );

        imageUsuario = findViewById(R.id.image_usuario_edit);
        btnSeleccionarFoto = findViewById(R.id.btn_seleccionar_foto_edit);
        etNombreUsuario = findViewById(R.id.et_nombre_usuario_edit);
        checkAdmin = findViewById(R.id.check_admin_edit);
        etEmail = findViewById(R.id.et_email_edit);
        etPassword = findViewById(R.id.et_password_edit);
        btnGuardarCambios = findViewById(R.id.btn_guardar_cambios);
        btnEliminarUsuario = findViewById(R.id.btn_eliminar_usuario);
        btnCancelar = findViewById(R.id.btn_cancelar_edit);

        // Mostrar datos actuales
        mostrarDatosUsuario();

        btnSeleccionarFoto.setOnClickListener(v -> abrirSelectorDeImagen());
        btnGuardarCambios.setOnClickListener(v -> guardarCambios());
        btnEliminarUsuario.setOnClickListener(v -> confirmarEliminar());
        btnCancelar.setOnClickListener(v -> finish());

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

    private void mostrarDatosUsuario() {
        etNombreUsuario.setText(usuarioActual.getNombre());
        checkAdmin.setChecked(usuarioActual.getIsAdmin() != null && usuarioActual.getIsAdmin());

        // Mostrar email y password solo si es admin
        if (usuarioActual.getIsAdmin() != null && usuarioActual.getIsAdmin()) {
            etEmail.setText(usuarioActual.getEmail());
            etPassword.setText(usuarioActual.getPassword());
            etEmail.setVisibility(View.VISIBLE);
            etPassword.setVisibility(View.VISIBLE);
        } else {
            etEmail.setVisibility(View.GONE);
            etPassword.setVisibility(View.GONE);
        }

        // Mostrar foto si existe
        imageUri = usuarioActual.getFoto();
        if (imageUri != null && !imageUri.isEmpty()) {
            imageUsuario.setImageURI(Uri.parse(imageUri));
        } else {
            imageUsuario.setImageResource(R.drawable.user);
        }
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

    private void guardarCambios() {
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

        // Actualizar datos del usuario
        usuarioActual.setNombre(nombre);
        usuarioActual.setFoto(imageUri);
        usuarioActual.setIsAdmin(esAdmin);

        if (esAdmin) {
            usuarioActual.setEmail(email);
            usuarioActual.setPassword(password);
        } else {
            usuarioActual.setEmail(null);
            usuarioActual.setPassword(null);
        }

        // Llamada API para actualizar
        UserInterface userInterface = UserInstance.getRetrofitInstance().create(UserInterface.class);
        Call<User> call = userInterface.actualizarUsuario(usuarioActual.getId(), usuarioActual);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditarUsuarioActivity.this, "Usuario actualizado.", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);
                    finish();

                } else {
                    String errorBody = "";
                    try {
                        errorBody = response.errorBody().string();  // Leer mensaje del servidor
                    } catch (Exception e) {
                        errorBody = "No se pudo leer el cuerpo del error.";
                    }
                    Toast.makeText(EditarUsuarioActivity.this, "Error al actualizar: " + response.code(), Toast.LENGTH_LONG).show();
                    android.util.Log.e("Retrofit", "Error al actualizar: " + response.code() + " -> " + errorBody);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(EditarUsuarioActivity.this, "Error en conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                android.util.Log.e("Retrofit", "Fallo en conexión", t);
            }
        });

    }

    private void confirmarEliminar() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar usuario")
                .setMessage("¿Estás seguro de que quieres eliminar este usuario?")
                .setPositiveButton("Sí", (dialog, which) -> eliminarUsuario())
                .setNegativeButton("No", null)
                .show();
    }

    private void eliminarUsuario() {
        UserInterface userInterface = UserInstance.getRetrofitInstance().create(UserInterface.class);
        Call<Void> call = userInterface.eliminarUsuario(usuarioActual.getId());

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditarUsuarioActivity.this, "Usuario eliminado.", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(EditarUsuarioActivity.this, "Error al eliminar.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EditarUsuarioActivity.this, "Error en conexión.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
