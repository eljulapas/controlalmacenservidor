package com.example.controlalmacen;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.controlalmacen.entities.Albaran;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.example.controlalmacen.instances.AlbaranInstance;
import com.example.controlalmacen.repositories.AlbaranInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InformeAlbaranesActivity extends AppCompatActivity {

    private RecyclerView rvAlbaranes;
    private AlbaranSeleccionAdapter adapter;

    private TextView tvResumen;
    private File pdfFile;

    private AlbaranInterface albaranInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informe_albaranes);

        rvAlbaranes = findViewById(R.id.rvAlbaranes);
        tvResumen = findViewById(R.id.tvResumen);
        Button btnGenerar = findViewById(R.id.btnGenerar);
        Button btnEnviar = findViewById(R.id.btnEnviar);

        rvAlbaranes.setLayoutManager(new LinearLayoutManager(this));

        albaranInterface = AlbaranInstance.getRetrofitInstance().create(AlbaranInterface.class);


        cargarAlbaranesDesdeAPI();

        btnGenerar.setOnClickListener(v -> {

            verificarPermisosYGenerarInforme();
        });

        btnEnviar.setOnClickListener(v -> {
            if (pdfFile != null) {
                enviarCorreoConPDF(pdfFile, tvResumen.getText().toString());
            } else {
                Toast.makeText(this, "Primero genera el informe", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarAlbaranesDesdeAPI() {
        albaranInterface.getAllAlbaranes().enqueue(new Callback<List<Albaran>>() {
            @Override
            public void onResponse(Call<List<Albaran>> call, Response<List<Albaran>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Albaran> albaranes = response.body();
                    adapter = new AlbaranSeleccionAdapter(albaranes);
                    rvAlbaranes.setAdapter(adapter);
                } else {
                    Toast.makeText(InformeAlbaranesActivity.this, "Error al cargar albaranes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Albaran>> call, Throwable t) {
                Toast.makeText(InformeAlbaranesActivity.this, "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarResumenEnPantalla(String resumen) {
        tvResumen.setText(resumen);
    }

    private void generarInforme(List<Albaran> albaranesSeleccionados) {
        int totalAlbaranes = albaranesSeleccionados.size();
        int pagados = 0;
        double totalImporte = 0;
        Set<String> cifs = new HashSet<>();
        Set<String> nombres = new HashSet<>();

        for (Albaran a : albaranesSeleccionados) {
            totalImporte += a.getCantidad();
            if (a.getPagado() != null && a.getPagado()) pagados++;
            if (a.getProveedor() != null) {
                cifs.add(a.getProveedor().getCif());
                nombres.add(a.getProveedor().getNombre());
            }
        }

        int noPagados = totalAlbaranes - pagados;

        String resumen = "Resumen de Albaranes\n\n" +
                "Total albaranes: " + totalAlbaranes + "\n" +
                "Importe total: " + totalImporte + " €\n" +
                "Pagados: " + pagados + "\n" +
                "No pagados: " + noPagados + "\n\n" +
                "CIFs:\n" + TextUtils.join(", ", cifs) + "\n\n" +
                "Proveedores:\n" + TextUtils.join(", ", nombres);

        mostrarResumenEnPantalla(resumen);

        generarPDFConImagenes(albaranesSeleccionados, resumen, pdfFile -> {
            this.pdfFile = pdfFile;
            enviarCorreoConPDF(pdfFile, resumen);
        });
    }

    private void generarPDFConImagenes(List<Albaran> albaranes,String resumen, Consumer<File> callback) {
        PdfDocument document = new PdfDocument();

        // Añadimos la primera página con el resumen
        PdfDocument.PageInfo pageInfoResumen = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page pageResumen = document.startPage(pageInfoResumen);
        Canvas canvasResumen = pageResumen.getCanvas();

        Paint paint = new Paint();
        paint.setTextSize(14);
        int x = 20;
        int y = 40;

        for (String linea : resumen.split("\n")) {
            canvasResumen.drawText(linea, x, y, paint);
            y += 20;
        }

        document.finishPage(pageResumen);


        for (int i = 0; i < albaranes.size(); i++) {
            String uriStr = albaranes.get(i).getFotoUrl();
            if (uriStr == null || uriStr.isEmpty()) continue;

            try {
                Uri imageUri = Uri.parse(uriStr);

                mostrarResumenEnPantalla(resumen);

                // Método para cargar la imagen
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                if (inputStream != null) inputStream.close();

                // Aquí i + 2 porque las fotos empiezan en la página 2
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, i + 2).create();
                PdfDocument.Page page = document.startPage(pageInfo);
                Canvas canvas = page.getCanvas();

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 595, 842, true);
                canvas.drawBitmap(scaledBitmap, 0, 0, null);

                document.finishPage(page);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File file = new File(getExternalFilesDir(null), "albaranes.pdf");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            document.writeTo(fos);
            callback.accept(file);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
    }

    private void enviarCorreoConPDF(File pdfFile, String resumen) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(
                this, getPackageName() + ".fileprovider", pdfFile));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Informe de Albaranes");
        intent.putExtra(Intent.EXTRA_TEXT, resumen);
        startActivity(Intent.createChooser(intent, "Enviar correo"));
    }

    // Método para gestionar la respuesta de la solicitud de permisos
    private void verificarPermisosYGenerarInforme() {
        // Verifica si la versión de Android es al menos 14 (Android 14+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 14 o superior
            if (checkSelfPermission(android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_MEDIA_IMAGES}, 1);
                Toast.makeText(this, "Otorga permisos y vuelve a intentarlo", Toast.LENGTH_SHORT).show();
            } else {
                // El permiso ya está concedido, genera el informe
                generarInforme(adapter != null ? adapter.getSeleccionados() : new ArrayList<>());
            }
        } else {
            // Android 13 o inferior, usa el permiso antiguo
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                Toast.makeText(this, "Otorga permisos y vuelve a intentarlo", Toast.LENGTH_SHORT).show();
            } else {
                // El permiso ya está concedido, genera el informe
                generarInforme(adapter != null ? adapter.getSeleccionados() : new ArrayList<>());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, genera el informe
                generarInforme(adapter != null ? adapter.getSeleccionados() : new ArrayList<>());
            } else {
                // Permiso denegado, muestra un mensaje
                Toast.makeText(this, "Permiso denegado para acceder a las imágenes", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Ya no se usa
    private void seleccionarImagenes() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Para seleccionar imágenes
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // Si quieres permitir seleccionar varias imágenes
            startActivityForResult(intent, 100);
        } else {
            // Para versiones anteriores a Android 14
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 100);
        }
    }
}
