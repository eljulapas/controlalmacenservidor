package com.example.controlalmacen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.controlalmacen.entities.Producto;
import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {

    private List<Producto> productos;

    public ProductoAdapter(List<Producto> productos) {
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

        // Establecer el nombre del producto
        holder.nombreTextView.setText(producto.getNombre());

        // Establecer la cantidad del producto
        holder.cantidadTextView.setText("Cantidad: " + producto.getCantidad());

        // Establecer la cantidad mínima del producto
        holder.minimoTextView.setText("Mínimo: " + producto.getMinimo());


        String productImage = producto.getImagen();
        if (productImage == null || productImage.isEmpty()) {
            holder.imagenImageView.setImageResource(R.drawable.diet); // Imagen predeterminada
        } else {

        }
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    // Clase ViewHolder
    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        TextView nombreTextView;
        ImageView imagenImageView;
        TextView cantidadTextView;
        TextView minimoTextView;

        public ProductoViewHolder(View itemView) {
            super(itemView);
            // Enlazar las vistas
            nombreTextView = itemView.findViewById(R.id.producto_nombre);
            imagenImageView = itemView.findViewById(R.id.producto_imagen);
            cantidadTextView = itemView.findViewById(R.id.producto_cantidad);
            minimoTextView = itemView.findViewById(R.id.producto_minimo);
        }
    }
}
