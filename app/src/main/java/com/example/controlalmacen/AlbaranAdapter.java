package com.example.controlalmacen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.controlalmacen.entities.Albaran;
import com.example.controlalmacen.instances.AlbaranInstance;
import com.example.controlalmacen.repositories.AlbaranInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class AlbaranAdapter extends RecyclerView.Adapter<AlbaranAdapter.ViewHolder> {

    private List<Albaran> albaranes;
    private Context context;

    public AlbaranAdapter(List<Albaran> albaranes, Context context) {
        this.albaranes = albaranes;
        this.context = context;
    }

    @NonNull
    @Override
    public AlbaranAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_albaran, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbaranAdapter.ViewHolder holder, int position) {
        Albaran albaran = albaranes.get(position);
        holder.txtFecha.setText("Fecha: " + albaran.getFecha());
        holder.txtCantidad.setText("Importe: " + albaran.getCantidad() + " €");
        holder.btnPagar.setVisibility(albaran.getPagado() ? View.GONE : View.VISIBLE);

        holder.btnPagar.setOnClickListener(v -> {
            AlbaranInterface api = AlbaranInstance.getRetrofitInstance().create(AlbaranInterface.class);
            api.marcarPagado(albaran.getId()).enqueue(new Callback<Albaran>() {
                @Override
                public void onResponse(Call<Albaran> call, Response<Albaran> response) {
                    Toast.makeText(context, "Albarán pagado", Toast.LENGTH_SHORT).show();
                    holder.btnPagar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<Albaran> call, Throwable t) {
                    Toast.makeText(context, "Error al pagar", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return albaranes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtFecha, txtCantidad;
        Button btnPagar;

        ViewHolder(View view) {
            super(view);
            txtFecha = view.findViewById(R.id.tvFechaAlbaran);
            txtCantidad = view.findViewById(R.id.tvImporteAlbaran);
            btnPagar = view.findViewById(R.id.btnMarcarPagado);
        }
    }
}

