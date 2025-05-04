package com.example.controlalmacen;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.controlalmacen.entities.Proveedor;

import java.util.List;

public class ProveedorAdapter extends RecyclerView.Adapter<ProveedorAdapter.ViewHolder> {

    private List<Proveedor> proveedores;
    private Context context;

    public ProveedorAdapter(List<Proveedor> proveedores, Context context) {
        this.proveedores = proveedores;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_proveedor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Proveedor proveedor = proveedores.get(position);
        holder.txtNombre.setText(proveedor.getNombre());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AlbaranesProveedorActivity.class);
            intent.putExtra("proveedorId", proveedor.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return proveedores.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre;

        ViewHolder(View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombreProveedor);
        }
    }
}
