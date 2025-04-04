package com.example.controlalmacen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.example.controlalmacen.entities.Producto;
import com.example.controlalmacen.instances.ProductoInstance;
import com.example.controlalmacen.repositories.ProductosInterface;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {

    private List<Producto> productos;
    private ProductosInterface productosInterface;

    public ProductoAdapter(List<Producto> productos) {
        this.productos = productos;
        this.productosInterface = ProductoInstance.getRetrofitInstance().create(ProductosInterface.class);
    }

    @Override
    public ProductoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductoViewHolder holder, int position) {
        Producto producto = productos.get(position);

        // Establecer los valores iniciales
        holder.nombreTextView.setText(producto.getNombre());
        holder.cantidadTextView.setText("Cantidad: " + producto.getCantidad());
        holder.minimoTextView.setText("Mínimo: " + producto.getMinimo());

        // Ocultar mensaje de alerta por defecto
        holder.alertaTextView.setVisibility(View.GONE);

        // Si el producto no tiene imagen, usa la predeterminada
        if (producto.getImagen() == null || producto.getImagen().isEmpty()) {
            holder.imagenImageView.setImageResource(R.drawable.diet);
        }

        // Verificar si la cantidad está en el límite mínimo
        if (producto.getCantidad() <= producto.getMinimo()) {
            holder.imagenImageView.setImageResource(R.drawable.outofstock);
            holder.alertaTextView.setText("⚠ ¡Stock bajo!");  // Mensaje de alerta
            holder.alertaTextView.setVisibility(View.VISIBLE);
        } else {
            holder.imagenImageView.setImageResource(R.drawable.diet);
            holder.alertaTextView.setVisibility(View.GONE);
        }

        // Botón para sumar cantidad
        holder.btnSumar.setOnClickListener(v -> {
            int nuevaCantidad = producto.getCantidad() + 1;
            producto.setCantidad(nuevaCantidad);
            holder.cantidadTextView.setText("Cantidad: " + nuevaCantidad);
            actualizarCantidad(producto.getId(), nuevaCantidad, holder);
            // Mostrar mensaje si el stock está bajo
            if (nuevaCantidad <= producto.getMinimo()) {
                Toast.makeText(holder.itemView.getContext(), "⚠ ¡Atención! El stock de " + producto.getNombre() + " está bajo.", Toast.LENGTH_SHORT).show();
            }

        });

        // Botón para restar cantidad
        holder.btnRestar.setOnClickListener(v -> {
            if (producto.getCantidad() > 0) {
                int nuevaCantidad = producto.getCantidad() - 1;
                producto.setCantidad(nuevaCantidad);
                holder.cantidadTextView.setText("Cantidad: " + nuevaCantidad);
                actualizarCantidad(producto.getId(), nuevaCantidad, holder);

                // Mostrar mensaje si el stock está bajo
                if (nuevaCantidad <= producto.getMinimo()) {
                    Toast.makeText(holder.itemView.getContext(), "⚠ ¡Atención! El stock de " + producto.getNombre() + " está bajo.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    // Método para actualizar la lista filtrada
    public void actualizarLista(List<Producto> nuevaLista) {
        this.productos = nuevaLista;
        notifyDataSetChanged();
    }


    // Clase ViewHolder
    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        TextView nombreTextView, cantidadTextView, minimoTextView, alertaTextView;
        ImageView imagenImageView;
        Button btnSumar, btnRestar;

        public ProductoViewHolder(View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.producto_nombre);
            imagenImageView = itemView.findViewById(R.id.producto_imagen);
            cantidadTextView = itemView.findViewById(R.id.producto_cantidad);
            minimoTextView = itemView.findViewById(R.id.producto_minimo);
            alertaTextView = itemView.findViewById(R.id.producto_alerta);
            btnSumar = itemView.findViewById(R.id.btnSumar);
            btnRestar = itemView.findViewById(R.id.btnRestar);
        }
    }

    // Método para actualizar la cantidad en la API
    private void actualizarCantidad(Long id, int nuevaCantidad, ProductoViewHolder holder) {
        Call<Producto> call = productosInterface.updateCantidad(id, nuevaCantidad);
        call.enqueue(new Callback<Producto>() {
            @Override
            public void onResponse(Call<Producto> call, Response<Producto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Producto productoActualizado = response.body();
                    holder.cantidadTextView.setText("Cantidad: " + productoActualizado.getCantidad());
                } else {
                    Toast.makeText(holder.itemView.getContext(), "Error al actualizar cantidad", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Producto> call, Throwable t) {
                Toast.makeText(holder.itemView.getContext(), "Fallo en la conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
