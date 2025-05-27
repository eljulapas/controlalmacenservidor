package com.example.controlalmacen;

import android.app.DatePickerDialog;
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
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import java.text.SimpleDateFormat;
import java.util.*;
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

    private List<Albaran> listaAlbaranesOriginal; // Lista original de albaranes
    private List<Albaran> listaAlbaranesFiltrados; // Lista de albaranes filtrados
    private ActivityResultLauncher<Intent> agregarAlbaranLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informe_albaranes);

        rvAlbaranes = findViewById(R.id.rvAlbaranes);
        tvResumen = findViewById(R.id.tvResumen);
        Button btnGenerar = findViewById(R.id.btnGenerar);
        Button btnEnviar = findViewById(R.id.btnEnviar);
        Button btnMesActual = findViewById(R.id.btnMesActual);
        Button btnSemanaActual = findViewById(R.id.btnSemanaActual);
        Button btnFechaPersonalizada = findViewById(R.id.btnFechaPersonalizada);
        Button btnMostrarTodos = findViewById(R.id.btnMostrarTodos);
        Button btnAgregarAlbaran = findViewById(R.id.btnAgregarAlbaran);

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

        agregarAlbaranLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        cargarAlbaranesDesdeAPI(); // 🔁 Recarga la lista con el nuevo albarán
                    }
                }
        );


        btnAgregarAlbaran.setOnClickListener(v -> {
            Intent intent = new Intent(InformeAlbaranesActivity.this, AgregarAlbaranActivity.class);
            agregarAlbaranLauncher.launch(intent);
        });

        Button btnAtras = findViewById(R.id.btn_atras);
        btnAtras.setOnClickListener(v -> {
            Intent intent = new Intent(InformeAlbaranesActivity.this, ProductActivity.class);
            startActivity(intent);
            finish();
        });

        btnMesActual.setOnClickListener(v -> filtrarPorMesActual());
        btnSemanaActual.setOnClickListener(v -> filtrarPorSemanaActual());
        btnFechaPersonalizada.setOnClickListener(v -> seleccionarRangoFechas());
        btnMostrarTodos.setOnClickListener(v -> mostrarTodosAlbaranes());
    }

    private void cargarAlbaranesDesdeAPI() {
        albaranInterface.getAllAlbaranes().enqueue(new Callback<List<Albaran>>() {
            @Override
            public void onResponse(Call<List<Albaran>> call, Response<List<Albaran>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaAlbaranesOriginal = response.body();  // Guardamos los albaranes originales
                    listaAlbaranesFiltrados = new ArrayList<>(listaAlbaranesOriginal);  // Inicializamos los filtrados con todos
                    adapter = new AlbaranSeleccionAdapter(listaAlbaranesFiltrados);
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


    // Filtrar por mes actual
    private void filtrarPorMesActual() {
        Calendar calendar = Calendar.getInstance();
        int mesActual = calendar.get(Calendar.MONTH); // Obtiene el mes actual
        List<Albaran> albaranesFiltrados = new ArrayList<>();

        for (Albaran albaran : listaAlbaranesOriginal) {
            try {
                // Convertir la fecha de String a Date
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date fechaAlbaran = sdf.parse(albaran.getFecha());

                Calendar cal = Calendar.getInstance();
                cal.setTime(fechaAlbaran);
                int mesAlbaran = cal.get(Calendar.MONTH); // Obtener el mes del albarán

                if (mesAlbaran == mesActual) {
                    albaranesFiltrados.add(albaran);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        listaAlbaranesFiltrados = albaranesFiltrados;
        adapter = new AlbaranSeleccionAdapter(listaAlbaranesFiltrados);
        rvAlbaranes.setAdapter(adapter);
        adapter.notifyDataSetChanged(); // Actualiza el RecyclerView con los albaranes filtrados
    }


    private void filtrarPorSemanaActual() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY); // Establecer lunes como el primer día de la semana
        int semanaActual = calendar.get(Calendar.WEEK_OF_YEAR); // Obtiene la semana actual
        int añoActual = calendar.get(Calendar.YEAR); // Obtiene el año actual
        List<Albaran> albaranesFiltrados = new ArrayList<>();

        Log.d("SemanaActual", "Semana actual: " + semanaActual + " Año actual: " + añoActual);

        for (Albaran albaran : listaAlbaranesOriginal) {
            try {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date fechaAlbaran = sdf.parse(albaran.getFecha());


                Log.d("AlbaranFecha", "Fecha albaran: " + albaran.getFecha());

                Calendar cal = Calendar.getInstance();
                cal.setFirstDayOfWeek(Calendar.MONDAY);
                cal.setTime(fechaAlbaran);
                int semanaAlbaran = cal.get(Calendar.WEEK_OF_YEAR);
                int añoAlbaran = cal.get(Calendar.YEAR);


                Log.d("SemanaAlbaran", "Semana del albarán: " + semanaAlbaran + " Año del albarán: " + añoAlbaran);

                // Comparar la semana actual con la semana del albarán (esto era una comprobación porque había problemas)
                if (semanaAlbaran == semanaActual && añoAlbaran == añoActual) {
                    Log.d("FiltroSemana", "Albarán filtrado: " + albaran.getFecha());
                    albaranesFiltrados.add(albaran);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        listaAlbaranesFiltrados = albaranesFiltrados;
        adapter = new AlbaranSeleccionAdapter(listaAlbaranesFiltrados);
        rvAlbaranes.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }



    // Selección del rango de fechas personalizado
    private void seleccionarRangoFechas() {
        Calendar calendarInicio = Calendar.getInstance();
        Calendar calendarFin = Calendar.getInstance();

        DatePickerDialog datePickerInicio = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    calendarInicio.set(year, monthOfYear, dayOfMonth);

                    DatePickerDialog datePickerFin = new DatePickerDialog(this,
                            (view1, year1, monthOfYear1, dayOfMonth1) -> {
                                calendarFin.set(year1, monthOfYear1, dayOfMonth1);
                                filtrarPorRangoFechas(calendarInicio.getTime(), calendarFin.getTime());
                            },
                            calendarFin.get(Calendar.YEAR),
                            calendarFin.get(Calendar.MONTH),
                            calendarFin.get(Calendar.DAY_OF_MONTH)
                    );

                    datePickerFin.show();
                },
                calendarInicio.get(Calendar.YEAR),
                calendarInicio.get(Calendar.MONTH),
                calendarInicio.get(Calendar.DAY_OF_MONTH)
        );

        datePickerInicio.show();
    }

       private void filtrarPorRangoFechas(Date fechaInicio, Date fechaFin) {
        List<Albaran> albaranesFiltrados = new ArrayList<>();

        for (Albaran albaran : listaAlbaranesOriginal) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date fechaAlbaran = sdf.parse(albaran.getFecha());

                if (!fechaAlbaran.before(fechaInicio) && !fechaAlbaran.after(fechaFin)) {
                    albaranesFiltrados.add(albaran);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        listaAlbaranesFiltrados = albaranesFiltrados;
        adapter = new AlbaranSeleccionAdapter(listaAlbaranesFiltrados);
        rvAlbaranes.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    private void mostrarTodosAlbaranes() {
        listaAlbaranesFiltrados = new ArrayList<>(listaAlbaranesOriginal);
        adapter = new AlbaranSeleccionAdapter(listaAlbaranesFiltrados); // Crear nuevo adaptador
        rvAlbaranes.setAdapter(adapter); // Asignar el nuevo adaptador al RecyclerView
        adapter.notifyDataSetChanged();
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 14 o superior
            if (checkSelfPermission(android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_MEDIA_IMAGES}, 1);
                Toast.makeText(this, "Otorga permisos y vuelve a intentarlo", Toast.LENGTH_SHORT).show();
            } else {

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
}
