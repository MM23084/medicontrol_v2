package com.proy.medicontrol.fragments;

import android.app.AlertDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.proy.medicontrol.R;
import com.proy.medicontrol.adapters.ExpedienteAdapter;
import com.proy.medicontrol.database.AppDatabase;
import com.proy.medicontrol.databinding.FragmentExpedientesBinding;
import com.proy.medicontrol.entities.Cita;
import com.proy.medicontrol.entities.Expediente;
import com.proy.medicontrol.entities.Usuario;
import com.proy.medicontrol.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpedientesFragment extends Fragment {

    private FragmentExpedientesBinding binding;
    private AppDatabase db;
    private SessionManager session;
    private int idMedico;

    private List<Expediente> listaOriginal = new ArrayList<>();
    private ExpedienteAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentExpedientesBinding.inflate(inflater, container, false);
        db = AppDatabase.getInstance(requireContext());
        session = new SessionManager(requireContext());
        idMedico = session.obtenerIdUsuario();

        setupRecycler();
        setupFiltroPacientes();
        cargarExpedientes();

        binding.btnNuevoExpediente.setOnClickListener(v -> mostrarCrear());

        return binding.getRoot();
    }

    private void setupRecycler() {
        binding.recyclerExpedientes.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerExpedientes.setHasFixedSize(true);
        binding.recyclerExpedientes.setItemAnimator(new DefaultItemAnimator());
    }

    private void setupFiltroPacientes() {
        List<String> pacientes = db.usuarioDao().obtenerNombresPacientes();
        if (pacientes == null) pacientes = new ArrayList<>();
        pacientes.add(0, "Todos");

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, pacientes);
        binding.spinnerFiltro.setAdapter(adapterSpinner);
        binding.spinnerFiltro.setOnItemClickListener((parent, view, position, id) -> filtrar());
    }

    private void cargarExpedientes() {
        // AHORA: Solo cargamos los expedientes generados por este médico
        listaOriginal = db.expedienteDao().obtenerPorMedico(idMedico);
        if (listaOriginal == null) listaOriginal = new ArrayList<>();

        adapter = new ExpedienteAdapter(listaOriginal, this::mostrarDetalleFullscreen);
        binding.recyclerExpedientes.setAdapter(adapter);
    }

    private void filtrar() {
        String pacienteFiltro = binding.spinnerFiltro.getText().toString();
        List<Expediente> filtrados = new ArrayList<>();

        for (Expediente e : listaOriginal) {
            Usuario usuario = db.usuarioDao().obtenerPorId(e.getIdUsuario());
            String nombrePaciente = usuario != null ? usuario.getNombre() : "";

            if (pacienteFiltro.equals("Todos") || pacienteFiltro.isEmpty() || nombrePaciente.equalsIgnoreCase(pacienteFiltro)) {
                filtrados.add(e);
            }
        }
        adapter = new ExpedienteAdapter(filtrados, this::mostrarDetalleFullscreen);
        binding.recyclerExpedientes.setAdapter(adapter);
    }

    private void mostrarDetalleFullscreen(Expediente expediente) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_detalle_expediente, null);
        TextView txt = view.findViewById(R.id.txtDetalle);

        Usuario usuario = db.usuarioDao().obtenerPorId(expediente.getIdUsuario());
        String nombre = (usuario != null) ? usuario.getNombre() : "No encontrado";
        String correo = (usuario != null) ? usuario.getCorreo() : "-";

        String texto = "Paciente: " + nombre + "\n\nCorreo: " + correo + "\n\nDiagnóstico: " + expediente.getDiagnostico() + "\n\nTratamiento: " + expediente.getTratamiento() + "\n\nMedicamentos: " + expediente.getMedicamentos() + "\n\nObservaciones: " + expediente.getObservaciones() + "\n\nFecha: " + expediente.getFecha();

        SpannableString ss = new SpannableString(texto);
        aplicarNegrita(ss, "Paciente:");
        aplicarNegrita(ss, "Correo:");
        aplicarNegrita(ss, "Diagnóstico:");
        aplicarNegrita(ss, "Tratamiento:");
        aplicarNegrita(ss, "Medicamentos:");
        aplicarNegrita(ss, "Observaciones:");
        aplicarNegrita(ss, "Fecha:");

        txt.setText(ss);
        AlertDialog dialog = new AlertDialog.Builder(requireContext()).setView(view).create();
        view.findViewById(R.id.btnCerrar).setOnClickListener(v -> dialog.dismiss());
        view.findViewById(R.id.btnEliminar).setOnClickListener(v -> {
            db.expedienteDao().eliminar(expediente);
            cargarExpedientes();
            dialog.dismiss();
        });
        view.findViewById(R.id.btnEditar).setOnClickListener(v -> {
            dialog.dismiss();
            mostrarEditar(expediente);
        });
        dialog.show();
    }

    private void aplicarNegrita(SpannableString ss, String palabra) {
        int start = ss.toString().indexOf(palabra);
        if (start >= 0) ss.setSpan(new StyleSpan(Typeface.BOLD), start, start + palabra.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void mostrarCrear() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_expediente, null);
        MaterialAutoCompleteTextView spinner = view.findViewById(R.id.spPacientes);
        MaterialAutoCompleteTextView spCitas = view.findViewById(R.id.spCitas);

        List<String> pacientes = db.usuarioDao().obtenerNombresPacientes();
        spinner.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, pacientes));

        spinner.setOnItemClickListener((parent, v, position, id) -> {
            Usuario usuario = db.usuarioDao().obtenerPacientePorNombre(spinner.getText().toString());
            if (usuario == null) return;

            // AHORA: Solo cargamos citas finalizadas de este paciente QUE HAYAN SIDO CON ESTE MÉDICO
            List<Cita> citas = db.citaDao().obtenerFinalizadasPorUsuarioYMedico(usuario.getId(), idMedico);
            List<String> items = new ArrayList<>();
            for (Cita c : citas) {
                if (db.expedienteDao().obtenerPorCita(c.getId()) == null) {
                    items.add("Cita #" + c.getId() + " - " + c.getMotivo());
                }
            }
            spCitas.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, items));
        });

        EditText d = view.findViewById(R.id.txtDiagnostico), t = view.findViewById(R.id.txtTratamiento), m = view.findViewById(R.id.txtMedicamentos), o = view.findViewById(R.id.txtObservaciones);

        AlertDialog dialog = new AlertDialog.Builder(getContext()).setTitle("Nuevo Expediente").setView(view).setPositiveButton("Guardar", null).setNegativeButton("Cancelar", null).create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String citaTexto = spCitas.getText().toString();
            if (citaTexto.isEmpty() || d.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Complete los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            int idCita = Integer.parseInt(citaTexto.substring(citaTexto.indexOf("#") + 1, citaTexto.indexOf(" - ")));
            Usuario u = db.usuarioDao().obtenerPacientePorNombre(spinner.getText().toString());

            String fecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

            Expediente exp = new Expediente(u.getId(), idCita, idMedico, d.getText().toString(), t.getText().toString(), m.getText().toString(), o.getText().toString(), fecha);
            db.expedienteDao().insertar(exp);
            cargarExpedientes();
            dialog.dismiss();
        });
    }

    private void mostrarEditar(Expediente e) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_expediente, null);
        MaterialAutoCompleteTextView spPacientes = view.findViewById(R.id.spPacientes);
        EditText d = view.findViewById(R.id.txtDiagnostico), t = view.findViewById(R.id.txtTratamiento), m = view.findViewById(R.id.txtMedicamentos), o = view.findViewById(R.id.txtObservaciones);

        Usuario u = db.usuarioDao().obtenerPorId(e.getIdUsuario());
        spPacientes.setText(u != null ? u.getNombre() : "", false);
        spPacientes.setEnabled(false);

        d.setText(e.getDiagnostico()); t.setText(e.getTratamiento()); m.setText(e.getMedicamentos()); o.setText(e.getObservaciones());

        new AlertDialog.Builder(getContext()).setTitle("Editar Expediente").setView(view).setPositiveButton("Actualizar", (dialog, which) -> {
            e.setDiagnostico(d.getText().toString()); e.setTratamiento(t.getText().toString()); e.setMedicamentos(m.getText().toString()); e.setObservaciones(o.getText().toString());
            db.expedienteDao().actualizar(e);
            cargarExpedientes();
        }).setNegativeButton("Cancelar", null).show();
    }
}
