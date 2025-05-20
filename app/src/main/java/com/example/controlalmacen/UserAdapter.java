package com.example.controlalmacen;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.controlalmacen.entities.Producto;
import com.example.controlalmacen.entities.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserAdapterViewHolder> {

    private List<User> users;
    private boolean modoEditar = false; // ✅ Nueva variable para modo edición

    public UserAdapter(List<User> users) {
        this.users = users;
    }

    // ✅ Setter para cambiar el modo desde MainActivity
    public void setModoEditar(boolean modoEditar) {
        this.modoEditar = modoEditar;
    }

    @Override
    public UserAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflar el layout para cada item de usuario
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuario, parent, false);
        return new UserAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserAdapterViewHolder holder, int position) {
        User user = users.get(position);

        // Establecer el nombre del usuario
        holder.nombreTextView.setText(user.getNombre());

        // Establecer la imagen del usuario o una predeterminada
        String userImage = user.getFoto(); // Obtener la foto del usuario
        if (userImage == null || userImage.isEmpty()) {
            holder.fotoImageView.setImageResource(R.drawable.user); // Imagen predeterminada
        } else {

        }

        // Mostrar el estado habilitado del usuario
        /*if (user.getHabilitado()) {
            holder.habilitadoTextView.setText("Habilitado");
            holder.habilitadoTextView.setTextColor(Color.GREEN);
        } else {
            holder.habilitadoTextView.setText("Deshabilitado");
            holder.habilitadoTextView.setTextColor(Color.RED);
        }*/

        // Al clickar la foto del usuario
        holder.fotoImageView.setOnClickListener(v -> {
            if (modoEditar) {
                // Modo edición: Ir a EditarUsuarioActivity
                Intent intent = new Intent(holder.itemView.getContext(), EditarUsuarioActivity.class);

                // Pasa los datos individualmente (esto NO da error)
                intent.putExtra("usuarioId", user.getId());
                intent.putExtra("usuarioNombre", user.getNombre());
                intent.putExtra("usuarioEmail", user.getEmail());
                intent.putExtra("usuarioIsAdmin", user.getIsAdmin());
                intent.putExtra("usuarioFoto", user.getFoto());
                intent.putExtra("usuarioPassword", user.getPassword());

                // Iniciar actividad para obtener un resultado
                ((Activity) holder.itemView.getContext()).startActivityForResult(intent, 1); // 1 es el código de solicitud
            } else {
                // Modo normal: Ir a ProductActivity
                Intent intent = new Intent(holder.itemView.getContext(), ProductActivity.class);
                holder.itemView.getContext().startActivity(intent);
            }
        });

        // ✅ Al clickar el icono de editar (siempre va a editar, no depende de modoEditar)
        holder.editImageView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), EditarUsuarioActivity.class);

            // ✅ Pasamos los datos individualmente (sin error)
            intent.putExtra("usuarioId", user.getId());
            intent.putExtra("usuarioNombre", user.getNombre());
            intent.putExtra("usuarioEmail", user.getEmail());
            intent.putExtra("usuarioIsAdmin", user.getIsAdmin());
            intent.putExtra("usuarioFoto", user.getFoto());
            intent.putExtra("usuarioPassword", user.getPassword());

            // Iniciar actividad para obtener un resultado
            ((Activity) holder.itemView.getContext()).startActivityForResult(intent, 1); // 1 es el código de solicitud
        });

    }





    @Override
    public int getItemCount() {
        return users.size();
    }

    // Método para actualizar la lista filtrada
    public void actualizarLista(List<User> nuevaLista) {
        this.users = nuevaLista;
        notifyDataSetChanged();
    }

    // Clase ViewHolder
    public static class UserAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView nombreTextView;
        ImageView fotoImageView;

        ImageView editImageView;

        public UserAdapterViewHolder(View itemView) {
            super(itemView);
            // Enlazar las vistas
            nombreTextView = itemView.findViewById(R.id.user_nombre);
            fotoImageView = itemView.findViewById(R.id.user_foto);
            editImageView = itemView.findViewById(R.id.user_edit);
        }
    }
}
