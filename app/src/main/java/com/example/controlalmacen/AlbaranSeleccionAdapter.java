package com.example.controlalmacen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.controlalmacen.entities.Albaran;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlbaranSeleccionAdapter extends RecyclerView.Adapter<AlbaranSeleccionAdapter.ViewHolder> {

    private List<Albaran> albaranes;
    private Set<Albaran> seleccionados = new HashSet<>();

    public AlbaranSeleccionAdapter(List<Albaran> albaranes) {
        this.albaranes = albaranes;
    }

    public List<Albaran> getSeleccionados() {
        return new ArrayList<>(seleccionados);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_albaran_checkbox, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Albaran a = albaranes.get(position);
        holder.cb.setText("Albarán #" + a.getId() + " - " + a.getCantidad() + "€");
        holder.cb.setOnCheckedChangeListener(null);
        holder.cb.setChecked(seleccionados.contains(a));
        holder.cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) seleccionados.add(a);
            else seleccionados.remove(a);
        });
    }

    @Override
    public int getItemCount() {
        return albaranes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox cb;
        ViewHolder(View view) {
            super(view);
            cb = view.findViewById(R.id.cbAlbaran);
        }
    }
}
