package com.proy.medicontrol.dialogs;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.proy.medicontrol.R;
import com.proy.medicontrol.database.AppDatabase;
import com.proy.medicontrol.entities.Cita;
import com.proy.medicontrol.entities.Usuario;
import com.proy.medicontrol.utils.SessionManager;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DialogCita {

    public interface OnGuardar {
        void guardar(int idMedico, String nombreMedico, String paciente, String fecha, String hora, String motivo);
    }

    public static void mostrar(Context context, OnGuardar listener) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.dialog_cita, null);

        AppDatabase db = AppDatabase.getInstance(context);
        SessionManager session = new SessionManager(context);
        String paciente = session.obtenerNombreCompleto();

        TextView txtPacienteInfo = view.findViewById(R.id.txtPacienteInfo);
        AutoCompleteTextView spinnerMedicos = view.findViewById(R.id.spinnerMedicos);
        TextInputEditText txtFecha = view.findViewById(R.id.txtFecha);
        TextInputEditText txtHora = view.findViewById(R.id.txtHora);
        TextInputEditText txtMotivo = view.findViewById(R.id.txtMotivo);

        txtPacienteInfo.setText("Paciente: " + paciente);

        // ── Cargar lista de Médicos ───────────────────────────
        List<Usuario> medicos = db.usuarioDao().obtenerPorRol("MEDICO");
        String[] nombresMedicos = new String[medicos.size()];
        for (int i = 0; i < medicos.size(); i++) {
            nombresMedicos[i] = medicos.get(i).getNombre();
        }

        ArrayAdapter<String> adapterMedicos = new ArrayAdapter<>(
                context,
                android.R.layout.simple_dropdown_item_1line,
                nombresMedicos
        );
        spinnerMedicos.setAdapter(adapterMedicos);
        spinnerMedicos.setOnClickListener(v -> spinnerMedicos.showDropDown());

        txtFecha.setKeyListener(null);
        txtHora.setKeyListener(null);

        txtFecha.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(context, (view1, year, month, dayOfMonth) -> {
                String fecha = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                txtFecha.setText(fecha);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        txtHora.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new TimePickerDialog(context, (view12, hourOfDay, minute) -> {
                String hora = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                txtHora.setText(hora);
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
        });

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Programar Cita")
                .setView(view)
                .setPositiveButton("Guardar", null)
                .setNegativeButton("Cancelar", null)
                .create();

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {

            String nombreMedicoSel = spinnerMedicos.getText().toString();
            String fecha = txtFecha.getText().toString().trim();
            String hora = txtHora.getText().toString().trim();
            String motivo = txtMotivo.getText().toString().trim();

            if (nombreMedicoSel.isEmpty() || fecha.isEmpty() || hora.isEmpty() || motivo.isEmpty()) {
                Toast.makeText(context, "Complete todos los campos.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Buscar el ID del médico seleccionado
            int idMedico = -1;
            for (Usuario u : medicos) {
                if (u.getNombre().equals(nombreMedicoSel)) {
                    idMedico = u.getId();
                    break;
                }
            }

            // ── VALIDACIÓN DE DISPONIBILIDAD ─────────────────────
            Cita citaExistente = db.citaDao().verificarDisponibilidad(idMedico, fecha, hora);

            if (citaExistente != null) {
                Toast.makeText(context, 
                    "El Dr. " + nombreMedicoSel + " ya tiene una cita programada a esa hora. Por favor, elija otro horario.", 
                    Toast.LENGTH_LONG).show();
                return;
            }

            listener.guardar(idMedico, nombreMedicoSel, paciente, fecha, hora, motivo);
            dialog.dismiss();
        });
    }
}
