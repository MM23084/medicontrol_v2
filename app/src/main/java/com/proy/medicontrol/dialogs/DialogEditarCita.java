package com.proy.medicontrol.dialogs;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.proy.medicontrol.R;
import com.proy.medicontrol.entities.Cita;

import java.util.Calendar;
import java.util.Locale;

public class DialogEditarCita {

    public interface OnGuardar {
        void guardar(String fecha,
                     String hora,
                     String motivo);
    }

    public static void mostrar(
            Context context,
            Cita cita,
            OnGuardar listener
    ) {

        // Reutiliza el mismo layout del formulario de creación.
        View view = LayoutInflater.from(context)
                .inflate(R.layout.dialog_cita, null);

        // Referencias a los widgets del formulario.
        TextView txtPacienteInfo =
                view.findViewById(R.id.txtPacienteInfo);

        TextInputEditText txtFecha =
                view.findViewById(R.id.txtFecha);

        TextInputEditText txtHora =
                view.findViewById(R.id.txtHora);

        TextInputEditText txtMotivo =
                view.findViewById(R.id.txtMotivo);

        // Mostrar el nombre del paciente como información (no editable).
        // Editar el paciente no está permitido en esta versión.
        txtPacienteInfo.setText(
                "Paciente: " + cita.getPaciente()
        );

        // Pre-cargar los valores actuales de la cita en los campos.
        txtFecha.setText(cita.getFecha());
        txtHora.setText(cita.getHora());
        txtMotivo.setText(cita.getMotivo());

        // Desactivar el teclado en fecha y hora para forzar el uso
        // de los pickers y garantizar el formato correcto.
        txtFecha.setKeyListener(null);
        txtHora.setKeyListener(null);

        // ── Selector de fecha ─────────────────────────────────
        // Abre DatePickerDialog con la fecha actual del dispositivo.
        // Formato de salida: "yyyy-MM-dd" (ISO 8601).
        txtFecha.setOnClickListener(v -> {

            Calendar c = Calendar.getInstance();

            DatePickerDialog picker =
                    new DatePickerDialog(
                            context,
                            (view1, year, month, day) -> {

                                // month es 0-indexed, sumar 1 para el formato correcto.
                                txtFecha.setText(
                                        String.format(
                                                Locale.getDefault(),
                                                "%04d-%02d-%02d",
                                                year,
                                                month + 1,
                                                day
                                        )
                                );
                            },
                            c.get(Calendar.YEAR),
                            c.get(Calendar.MONTH),
                            c.get(Calendar.DAY_OF_MONTH)
                    );

            picker.show();
        });

        // ── Selector de hora ──────────────────────────────────
        // Abre TimePickerDialog en formato 24 horas.
        // Formato de salida: "HH:mm".
        txtHora.setOnClickListener(v -> {

            Calendar c = Calendar.getInstance();

            TimePickerDialog picker =
                    new TimePickerDialog(
                            context,
                            (view12, h, m) -> {

                                txtHora.setText(
                                        String.format(
                                                Locale.getDefault(),
                                                "%02d:%02d",
                                                h,
                                                m
                                        )
                                );
                            },
                            c.get(Calendar.HOUR_OF_DAY),
                            c.get(Calendar.MINUTE),
                            true // formato 24 horas
                    );

            picker.show();
        });

        // Crear el diálogo. A diferencia de DialogCita, aquí no se
        // necesita validación adicional al guardar (los campos ya
        // tienen valores previos de la cita).
        AlertDialog dialog =
                new AlertDialog.Builder(context)
                        .setTitle("Editar Cita")
                        .setView(view)
                        .setPositiveButton("Guardar", null)
                        .setNegativeButton("Cancelar", null)
                        .create();

        dialog.show();

        // Al guardar, notificar con los tres campos modificables.
        // El Fragment actualiza el objeto Cita y lo persiste en la BD.
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(v -> {

                    listener.guardar(
                            txtFecha.getText().toString().trim(),
                            txtHora.getText().toString().trim(),
                            txtMotivo.getText().toString().trim()
                    );

                    dialog.dismiss();
                });
    }
}
