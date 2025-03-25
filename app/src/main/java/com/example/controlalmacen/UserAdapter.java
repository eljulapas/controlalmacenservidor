package com.example.controlalmacen;

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

    public UserAdapter(List<User> users) {
        this.users = users;
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
        if (user.getHabilitado()) {
            holder.habilitadoTextView.setText("Habilitado");
            holder.habilitadoTextView.setTextColor(Color.GREEN);
        } else {
            holder.habilitadoTextView.setText("Deshabilitado");
            holder.habilitadoTextView.setTextColor(Color.RED);
        }

        // Al clickar la foto del usuario
        holder.fotoImageView.setOnClickListener(v -> {

            Intent intent = new Intent(holder.itemView.getContext(), ProductActivity.class);
            holder.itemView.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    // Clase ViewHolder
    public static class UserAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView nombreTextView;
        ImageView fotoImageView;
        TextView habilitadoTextView;

        public UserAdapterViewHolder(View itemView) {
            super(itemView);
            // Enlazar las vistas
            nombreTextView = itemView.findViewById(R.id.user_nombre);
            fotoImageView = itemView.findViewById(R.id.user_foto);
            habilitadoTextView = itemView.findViewById(R.id.user_habilitado);
        }
    }
}
