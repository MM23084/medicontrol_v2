package com.proy.medicontrol.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.proy.medicontrol.R;
import com.proy.medicontrol.entities.Usuario;

import java.util.List;

public class PacienteAdapter
        extends RecyclerView.Adapter<PacienteAdapter.ViewHolder> {

    // Lista de pacientes (usuarios con rol PACIENTE) a mostrar.
    private final List<Usuario> lista;

    // Callback que el Fragment usa para reaccionar al click en un ítem.
    private final OnPacienteClick listener;

    public interface OnPacienteClick {
        void onClick(Usuario usuario);
    }

    public PacienteAdapter(List<Usuario> lista, OnPacienteClick listener) {
        this.lista = lista;
        this.listener = listener;
    }

    /** Infla el layout del ítem de paciente y crea el ViewHolder. */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_paciente, parent, false);

        return new ViewHolder(view);
    }

    /**
     * Enlaza los datos del paciente con las vistas del ítem.
     * La carga de la foto verifica el esquema de la URI para evitar
     * intentar cargar URIs de esquema inválido (por ejemplo "DEFAULT").
     * Ante cualquier error, muestra la imagen por defecto.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Usuario usuario = lista.get(position);

        holder.txtNombre.setText(usuario.getNombre());
        holder.txtCorreo.setText(usuario.getCorreo());

        // Limpiar la imagen anterior del ViewHolder reciclado
        // antes de asignar la nueva para evitar imágenes residuales.
        holder.imgPaciente.setImageDrawable(null);
        holder.imgPaciente.setImageResource(R.drawable.paciente_default);

        String foto = usuario.getFotoPerfil();

        // ── Carga de foto con manejo de errores ───────────────
        if (foto != null && !foto.trim().isEmpty()) {

            try {

                Uri uri = Uri.parse(foto);

                // Solo cargar URIs con esquema válido del sistema de archivos.
                // "DEFAULT" o cualquier texto no-URI tiene esquema null y es ignorado.
                if ("content".equals(uri.getScheme())
                        || "file".equals(uri.getScheme())) {

                    holder.imgPaciente.setImageURI(uri);

                } else {
                    // Esquema inválido o URI no de archivo: usar imagen por defecto.
                    holder.imgPaciente.setImageResource(
                            R.drawable.paciente_default
                    );
                }

            } catch (Exception e) {
                // La URI fue revocada o es inaccesible: usar imagen por defecto.
                holder.imgPaciente.setImageResource(
                        R.drawable.paciente_default
                );
            }

        } else {
            // Sin foto registrada: mostrar imagen por defecto.
            holder.imgPaciente.setImageResource(
                    R.drawable.paciente_default
            );
        }

        // Notificar al listener cuando el médico toca el ítem de un paciente.
        holder.itemView.setOnClickListener(v -> {

            if (listener != null) {
                listener.onClick(usuario);
            }
        });
    }

    /** Devuelve el número de pacientes en la lista. Null-safe. */
    @Override
    public int getItemCount() {
        return lista != null ? lista.size() : 0;
    }

    /**
     * ViewHolder con las referencias a los tres widgets del ítem.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtNombre;
        TextView txtCorreo;
        ImageView imgPaciente;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtCorreo = itemView.findViewById(R.id.txtCorreo);
            imgPaciente = itemView.findViewById(R.id.imgPaciente);
        }
    }
}
