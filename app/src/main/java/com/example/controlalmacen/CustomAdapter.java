package com.example.controlalmacen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.controlalmacen.entities.Producto;

import java.util.List;
/*
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ProductoViewHolder> {

    private List<Producto> productos;

    public CustomAdapter(List<Producto> productos) {
        this.productos = productos;
    }

    @Override
    public ProductoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductoViewHolder holder, int position) {
        Producto producto = productos.get(position);
        holder.nombreTextView.setText(producto.getNombre());
        holder.cantidadTextView.setText(String.valueOf(producto.getCantidad()));
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public static class ProductoViewHolder extends RecyclerView.ViewHolder {

        TextView nombreTextView;
        TextView cantidadTextView;

        public ProductoViewHolder(View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.producto_nombre);
            cantidadTextView = itemView.findViewById(R.id.producto_cantidad);
        }
    }
}
*/