package com.example.controlalmacen;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.controlalmacen.entities.Producto;
import com.example.controlalmacen.instances.ProductoInstance;
import com.example.controlalmacen.repositories.ProductosInterface;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import okhttp3.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RevisorCantidadStock extends Worker {

    //Clase para futuras implementaciones

    public RevisorCantidadStock(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        CountDownLatch latch = new CountDownLatch(1);

        ProductosInterface productoInterface = ProductoInstance.getRetrofitInstance().create(ProductosInterface.class);
        Call<List<Producto>> call = productoInterface.getAllProductos();

        call.enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Producto> stockBajo = new ArrayList<>();
                    for (Producto p : response.body()) {
                        if (p.getCantidad() <= p.getMinimo()) {
                            stockBajo.add(p);
                        }
                    }


                    generarExcel(stockBajo);
                }
                latch.countDown();
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                Log.e("RevisorCantidadStock", "Error cargando productos", t);
                latch.countDown();
            }
        });

        try {
            latch.await(); // Espera a que termine el hilo de Retrofit
        } catch (InterruptedException e) {
            return Result.failure();
        }

        return Result.success();
    }

    private void generarExcel(List<Producto> productos) {
        File file = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "stock_bajo.xls");

        try {
            WritableWorkbook workbook = Workbook.createWorkbook(file);
            WritableSheet sheet = workbook.createSheet("Stock Mínimo", 0);

            sheet.addCell(new Label(0, 0, "Nombre"));
            sheet.addCell(new Label(1, 0, "Cantidad"));
            sheet.addCell(new Label(2, 0, "Mínimo"));

            for (int i = 0; i < productos.size(); i++) {
                Producto p = productos.get(i);
                sheet.addCell(new Label(0, i + 1, p.getNombre()));
                sheet.addCell(new jxl.write.Number(1, i + 1, p.getCantidad()));
                sheet.addCell(new jxl.write.Number(2, i + 1, p.getMinimo()));
            }

            workbook.write();
            workbook.close();

            Log.i("RevisorCantidadStock", "Archivo generado: " + file.getAbsolutePath());

        } catch (Exception e) {
            Log.e("RevisorCantidadStock", "Error al generar Excel", e);
        }

        enviarExcelPorCorreo(file);
    }


    private void enviarExcelPorCorreo(File file) {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("archivo", file.getName(),
                        RequestBody.create(file, MediaType.parse("application/vnd.ms-excel")))
                .addFormDataPart("destinatario", "tuusuario@gmail.com")
                .build();

        Request request = new Request.Builder()
                .url("http://192.168.0.18:8080/api/enviar-correo")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                Log.e("RevisorCantidadStock", "Error al enviar el Excel por correo", e);
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) {
                Log.i("RevisorCantidadStock", "Correo enviado con respuesta: " + response.code());
            }
        });
    }

}
