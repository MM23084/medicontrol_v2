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

public class CitaAdapter extends RecyclerView.Adapter<CitaAdapter.ViewHolder> {

    private final List<Cita> lista;
    private final OnCitaClick listener;

    public interface OnCitaClick {
        void onClick(Cita cita);
    }

    public CitaAdapter(List<Cita> lista, OnCitaClick listener) {
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cita, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cita c = lista.get(position);

        holder.txtMotivo.setText(c.getMotivo() != null ? c.getMotivo() : "Sin motivo");
        holder.txtPaciente.setText("Paciente: " + (c.getPaciente() != null ? c.getPaciente() : ""));
        
        // Mostrar el nombre del médico asignado
        holder.txtMedico.setText("Dr. " + (c.getNombreMedico() != null ? c.getNombreMedico() : "No asignado"));

        holder.txtFecha.setText("Fecha: " + (c.getFecha() != null ? c.getFecha() : ""));
        holder.txtHora.setText("Hora: " + (c.getHora() != null ? c.getHora() : ""));
        holder.txtEstado.setText(c.getEstado() != null ? c.getEstado() : "PENDIENTE");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(c);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lista != null ? lista.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtMotivo;
        TextView txtPaciente;
        TextView txtMedico; // Referencia al nuevo TextView
        TextView txtFecha;
        TextView txtHora;
        TextView txtEstado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMotivo = itemView.findViewById(R.id.txtMotivo);
            txtPaciente = itemView.findViewById(R.id.txtPaciente);
            txtMedico = itemView.findViewById(R.id.txtMedico);
            txtFecha = itemView.findViewById(R.id.txtFecha);
            txtHora = itemView.findViewById(R.id.txtHora);
            txtEstado = itemView.findViewById(R.id.txtEstado);
        }
    }
}
