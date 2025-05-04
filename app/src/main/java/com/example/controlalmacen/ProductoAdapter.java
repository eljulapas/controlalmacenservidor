package com.example.controlalmacen;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
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
import com.bumptech.glide.Glide;


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

        holder.nombreTextView.setText(producto.getNombre());
        holder.cantidadTextView.setText("Cantidad: " + producto.getCantidad());
        holder.minimoTextView.setText("Mínimo: " + producto.getMinimo());

        holder.alertaTextView.setVisibility(View.GONE);

        // Decide qué imagen mostrar
        String imagenMostrar = producto.getImagen();  // Por defecto, usa la URL

        if (producto.getCantidad() <= producto.getMinimo()) {
            // Alerta para si el stock está bajo
            holder.alertaTextView.setText("⚠ ¡Stock bajo!");
            holder.alertaTextView.setVisibility(View.VISIBLE);
            imagenMostrar = "android.resource://" + holder.itemView.getContext().getPackageName() + "/" + R.drawable.outofstock;   //Gestionar las imágenes locales
        }

        if (imagenMostrar != null && !imagenMostrar.isEmpty()) {
            Log.d("GlideDebug", "Cargando imagen: " + imagenMostrar);
            Glide.with(holder.itemView.getContext())
                    .load(Uri.parse(imagenMostrar))
                    .placeholder(R.drawable.diet)
                    .error(R.drawable.diet)
                    .into(holder.imagenImageView);
        } else {
            Log.d("GlideDebug", "URL imagen es null o vacía");
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.diet)
                    .into(holder.imagenImageView);
        }

        // Listener para editar
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), EditActivity.class);
            intent.putExtra("PRODUCTO_ID", producto.getId());
            intent.putExtra("PRODUCTO_NOMBRE", producto.getNombre());
            intent.putExtra("PRODUCTO_CANTIDAD", producto.getCantidad());
            intent.putExtra("PRODUCTO_MINIMO", producto.getMinimo());
            intent.putExtra("PRODUCTO_IMAGEN_URL", producto.getImagen());
            holder.itemView.getContext().startActivity(intent);
        });

        // Botones sumar/restar
        holder.btnSumar.setOnClickListener(v -> {
            int nuevaCantidad = producto.getCantidad() + 1;
            producto.setCantidad(nuevaCantidad);
            holder.cantidadTextView.setText("Cantidad: " + nuevaCantidad);
            actualizarCantidad(producto.getId(), nuevaCantidad, holder);

            if (nuevaCantidad <= producto.getMinimo()) {
                Toast.makeText(holder.itemView.getContext(), "⚠ ¡Atención! El stock de " + producto.getNombre() + " está bajo.", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnRestar.setOnClickListener(v -> {
            if (producto.getCantidad() > 0) {
                int nuevaCantidad = producto.getCantidad() - 1;
                producto.setCantidad(nuevaCantidad);
                holder.cantidadTextView.setText("Cantidad: " + nuevaCantidad);
                actualizarCantidad(producto.getId(), nuevaCantidad, holder);

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