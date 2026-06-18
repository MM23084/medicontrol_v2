package com.proy.medicontrol.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.proy.medicontrol.R;
import com.proy.medicontrol.entities.Cita;

import java.util.List;

public class DashboardCitaAdapter extends RecyclerView.Adapter<DashboardCitaAdapter.VH> {

    // Lista de citas a mostrar. Puede ser lista completa o solo próximas.
    private final List<Cita> lista;

    public DashboardCitaAdapter(List<Cita> lista) {
        this.lista = lista;
    }

    /** Infla el layout simplificado del dashboard para cada ítem. */
    @NonNull
    @Override
    public VH onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cita_dashboard, parent, false);

        return new VH(v);
    }

    /**
     * Muestra los cuatro campos principales de la cita en un único
     * TextView. Este formato compacto es apropiado para el dashboard
     * donde el espacio es limitado y no se requieren acciones.
     */
    @Override
    public void onBindViewHolder(
            @NonNull VH holder,
            int position
    ) {

        Cita c = lista.get(position);

        // Mostrar todos los campos relevantes en un bloque de texto.
        holder.txt.setText(
                "Motivo: " + c.getMotivo() +
                        "\nFecha: " + c.getFecha() +
                        "\nHora: " + c.getHora() +
                        "\nEstado: " + c.getEstado()
        );
    }

    /** Devuelve el número de citas en la lista. Null-safe. */
    @Override
    public int getItemCount() {
        return lista != null ? lista.size() : 0;
    }

    /**
     * ViewHolder con un único TextView para el resumen de la cita.
     * Diseño intencional: este adapter es solo de visualización.
     */
    static class VH extends RecyclerView.ViewHolder {

        TextView txt;

        public VH(@NonNull View itemView) {
            super(itemView);
            txt = itemView.findViewById(R.id.txtItemCita);
        }
    }
}
