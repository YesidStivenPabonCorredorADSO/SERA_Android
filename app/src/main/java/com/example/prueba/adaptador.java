package com.example.prueba;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class adaptador extends RecyclerView.Adapter<adaptador.ViewHolder> {

    private List<elemento> datosList;
    private OnUserStatusChangeListener statusChangeListener;

    public adaptador(List<elemento> datosList, OnUserStatusChangeListener statusChangeListener) {
        this.datosList = datosList;
        this.statusChangeListener = statusChangeListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        elemento datos = datosList.get(position);
        holder.bind(datos);
    }

    @Override
    public int getItemCount() {
        return datosList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textId;
        TextView textNombre;
        TextView textApellido;
        TextView textCorreo;
        TextView textGenero;
        Button buttonActivo;
        Button buttonInactivo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textId = itemView.findViewById(R.id.text_id);
            textNombre = itemView.findViewById(R.id.text_nombre);
            textApellido = itemView.findViewById(R.id.text_apellido);
            textCorreo = itemView.findViewById(R.id.text_correo);
            textGenero = itemView.findViewById(R.id.text_genero);
            buttonActivo = itemView.findViewById(R.id.button_activo);
            buttonInactivo = itemView.findViewById(R.id.button_inactivo);
        }

        public void bind(final elemento datos) {
            textId.setText(datos.getId());
            textNombre.setText(datos.getNombre_registro());
            textApellido.setText(datos.getApellido_registro());
            textCorreo.setText(datos.getCorreo_registro());
            textGenero.setText(datos.getGenero_registro());

            // Configurar el estado de los botones según el estado del usuario
            if (datos.getEstadoCuenta().equalsIgnoreCase("activado")) {
                buttonActivo.setEnabled(false);
                buttonInactivo.setEnabled(true);
            } else {
                buttonActivo.setEnabled(true);
                buttonInactivo.setEnabled(false);
            }

            // Configurar el listener para cambiar el estado del usuario a activado
            buttonActivo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    statusChangeListener.onStatusChange(datos, "activado");
                }
            });

            // Configurar el listener para cambiar el estado del usuario a desactivado
            buttonInactivo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    statusChangeListener.onStatusChange(datos, "desactivado");
                }
            });
        }
    }

    public interface OnUserStatusChangeListener {
        void onStatusChange(elemento datos, String nuevoEstado);
    }
}