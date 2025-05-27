package com.example.controlalmacen;

import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.controlalmacen.R;
import com.example.controlalmacen.entities.Producto;

import java.util.List;

public class InventarioAdapter extends RecyclerView.Adapter<InventarioAdapter.InventarioViewHolder> {

    private List<Producto> productos;

    public InventarioAdapter(List<Producto> productos) {
        this.productos = productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
        notifyDataSetChanged();
    }

    public List<Producto> getProductos() {
        return productos;
    }

    @NonNull
    @Override
    public InventarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inventario, parent, false);
        return new InventarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventarioViewHolder holder, int position) {
        Producto p = productos.get(position);
        holder.tvNombre.setText(p.getNombre());
        holder.tvCantidad.setText("Cantidad: " + p.getCantidad());
        holder.tvCantidadMinima.setText("Mínimo: " + p.getMinimo());
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    static class InventarioViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvCantidad, tvCantidadMinima;

        public InventarioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tv_nombre);
            tvCantidad = itemView.findViewById(R.id.tv_cantidad);
            tvCantidadMinima = itemView.findViewById(R.id.tv_cantidad_minima);
        }
    }
}
