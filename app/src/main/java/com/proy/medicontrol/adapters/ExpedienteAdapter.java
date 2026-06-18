package com.proy.medicontrol.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.proy.medicontrol.R;
import com.proy.medicontrol.database.AppDatabase;
import com.proy.medicontrol.entities.Expediente;
import com.proy.medicontrol.entities.Usuario;

import java.util.List;

public class ExpedienteAdapter
        extends RecyclerView.Adapter<ExpedienteAdapter.ViewHolder> {

    // Lista de expedientes. Contiene solo IDs de paciente y cita,
    // no los datos del paciente (se resuelven en onBindViewHolder).
    private final List<Expediente> lista;

    // Callback que el Fragment usa para reaccionar al click en un ítem.
    private final OnExpedienteClick listener;

    /**
     * Interfaz funcional para comunicar el click al fragmento contenedor.
     * El fragmento decide si mostrar detalle, editar, etc.
     */
    public interface OnExpedienteClick {
        void onClick(Expediente expediente);
    }

    public ExpedienteAdapter(
            List<Expediente> lista,
            OnExpedienteClick listener) {

        this.lista = lista;
        this.listener = listener;
    }

    /** Infla el layout del ítem de expediente y crea el ViewHolder. */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(
                                R.layout.item_expediente,
                                parent,
                                false
                        );

        return new ViewHolder(view);
    }

    /**
     * Enlaza los datos del expediente con las vistas del ítem.
     * Realiza una consulta a la BD para obtener nombre y correo
     * del paciente a partir del idUsuario guardado en el expediente.
     * Si el usuario ya no existe en la BD, muestra valores por defecto.
     */
    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        Expediente expediente =
                lista.get(position);

        // Consultar el usuario (paciente) asociado al expediente.
        // El expediente solo almacena el ID del paciente, no su nombre.
        Usuario usuario =
                AppDatabase.getInstance(
                                holder.itemView.getContext()
                        )
                        .usuarioDao()
                        .obtenerPorId(
                                expediente.getIdUsuario()
                        );

        if (usuario != null) {
            // Mostrar datos del paciente encontrado.
            holder.txtPaciente.setText(usuario.getNombre());
            holder.txtCorreo.setText(usuario.getCorreo());

        } else {
            // El paciente fue eliminado o el ID es inválido.
            holder.txtPaciente.setText("Paciente no encontrado");
            holder.txtCorreo.setText("-");
        }

        // Mostrar el diagnóstico y la fecha del expediente.
        holder.txtDiagnostico.setText(expediente.getDiagnostico());
        holder.txtFecha.setText(expediente.getFecha());

        // Al tocar el ítem, notificar al listener con el expediente completo.
        holder.itemView.setOnClickListener(v -> {

            if (listener != null) {
                listener.onClick(expediente);
            }

        });
    }

    /** Devuelve el número de expedientes. Null-safe. */
    @Override
    public int getItemCount() {
        return lista != null ? lista.size() : 0;
    }

    /**
     * ViewHolder con las referencias a los cuatro TextViews del ítem.
     * Almacena las vistas para evitar múltiples llamadas a findViewById().
     */
    static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtPaciente;
        TextView txtCorreo;
        TextView txtDiagnostico;
        TextView txtFecha;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            txtPaciente =
                    itemView.findViewById(R.id.txtPaciente);

            txtCorreo =
                    itemView.findViewById(R.id.txtCorreo);

            txtDiagnostico =
                    itemView.findViewById(R.id.txtDiagnostico);

            txtFecha =
                    itemView.findViewById(R.id.txtFecha);
        }
    }
}
