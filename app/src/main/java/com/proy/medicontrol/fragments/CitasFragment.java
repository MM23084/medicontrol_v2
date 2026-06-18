package com.proy.medicontrol.fragments;

import android.app.AlertDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.proy.medicontrol.R;
import com.proy.medicontrol.adapters.CitaAdapter;
import com.proy.medicontrol.database.AppDatabase;
import com.proy.medicontrol.databinding.FragmentCitasBinding;
import com.proy.medicontrol.dialogs.DialogCita;
import com.proy.medicontrol.entities.Cita;
import com.proy.medicontrol.utils.SessionManager;
import com.proy.medicontrol.dialogs.DialogEditarCita;

import java.util.ArrayList;
import java.util.List;

public class CitasFragment extends Fragment {

    private FragmentCitasBinding binding;
    private AppDatabase db;
    private List<Cita> listaOriginal = new ArrayList<>();
    private CitaAdapter adapter;
    private SessionManager session;
    private String rol;
    private int idUsuario; // ID del usuario en sesión (Paciente o Médico)

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCitasBinding.inflate(inflater, container, false);
        db = AppDatabase.getInstance(requireContext());
        session = new SessionManager(requireContext());

        rol = session.obtenerRol() != null ? session.obtenerRol().toUpperCase() : "PACIENTE";
        idUsuario = session.obtenerIdUsuario();

        setupUI();
        setupRecycler();
        setupSearch();
        cargarCitas();

        //  Crear nueva cita (solo PACIENTE)
        binding.btnNuevaCita.setOnClickListener(v -> {
            DialogCita.mostrar(requireContext(), (idMedico, nombreMedico, paciente, fecha, hora, motivo) -> {
                Cita nueva = new Cita(
                        idUsuario,
                        idMedico,
                        nombreMedico,
                        paciente,
                        fecha,
                        hora,
                        motivo
                );
                db.citaDao().insertar(nueva);
                cargarCitas();
                Toast.makeText(requireContext(), "Cita solicitada correctamente", Toast.LENGTH_SHORT).show();
            });
        });

        return binding.getRoot();
    }

    private void setupUI() {
        if (rol.equals("PACIENTE")) {
            binding.btnNuevaCita.setVisibility(View.VISIBLE);
            binding.spinnerFiltro.setVisibility(View.GONE);
            binding.spinnerEstado.setVisibility(View.GONE);
            binding.txtFiltroLabel.setVisibility(View.GONE);
        } else {
            binding.btnNuevaCita.setVisibility(View.GONE);
            setupFiltro();
        }
    }

    private void setupRecycler() {
        binding.recyclerCitas.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void setupSearch() {
        binding.txtBuscarCita.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { filtrar(s.toString()); }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void setupFiltro() {
        // Obtenemos solo los pacientes que tienen alguna cita con este médico para el filtro rápido
        List<String> pacientes = db.usuarioDao().obtenerNombresPacientes();
        if (pacientes == null) pacientes = new ArrayList<>();
        pacientes.add(0, "Todos");

        ArrayAdapter<String> adapterPacientes = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, pacientes);
        binding.spinnerFiltro.setAdapter(adapterPacientes);
        binding.spinnerFiltro.setOnClickListener(v -> binding.spinnerFiltro.showDropDown());

        String[] estados = {"Todos", Cita.ESTADO_PENDIENTE, Cita.ESTADO_ACEPTADA, Cita.ESTADO_RECHAZADA, Cita.ESTADO_CANCELADA, Cita.ESTADO_FINALIZADA};
        ArrayAdapter<String> adapterEstados = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, estados);
        binding.spinnerEstado.setAdapter(adapterEstados);
        binding.spinnerEstado.setOnClickListener(v -> binding.spinnerEstado.showDropDown());

        binding.spinnerFiltro.setOnItemClickListener((parent, view, position, id) -> aplicarFiltros());
        binding.spinnerEstado.setOnItemClickListener((parent, view, position, id) -> aplicarFiltros());
    }

    private void aplicarFiltros() {
        String paciente = binding.spinnerFiltro.getText().toString();
        String estado = binding.spinnerEstado.getText().toString();
        List<Cita> filtradas = new ArrayList<>();

        for (Cita c : listaOriginal) {
            boolean coincidePaciente = paciente.isEmpty() || paciente.equals("Todos") || paciente.equalsIgnoreCase(c.getPaciente());
            boolean coincideEstado = estado.isEmpty() || estado.equals("Todos") || estado.equalsIgnoreCase(c.getEstado());
            if (coincidePaciente && coincideEstado) filtradas.add(c);
        }
        adapter = new CitaAdapter(filtradas, this::mostrarDetalle);
        binding.recyclerCitas.setAdapter(adapter);
    }

    private void cargarCitas() {
        if (rol.equals("PACIENTE")) {
            // El paciente ve sus propias citas (donde él es el solicitante)
            listaOriginal = db.citaDao().obtenerPorUsuario(idUsuario);
        } else {
            // El médico ve únicamente las citas que han sido solicitadas para él
            listaOriginal = db.citaDao().obtenerPorMedico(idUsuario);
        }
        
        if (listaOriginal == null) listaOriginal = new ArrayList<>();

        adapter = new CitaAdapter(listaOriginal, this::mostrarDetalle);
        binding.recyclerCitas.setAdapter(adapter);
    }

    private void filtrar(String texto) {
        List<Cita> filtradas = new ArrayList<>();
        for (Cita c : listaOriginal) {
            if (c == null) continue;
            boolean coincide = texto == null || texto.trim().isEmpty() ||
                    (c.getPaciente() != null && c.getPaciente().toLowerCase().contains(texto.toLowerCase())) ||
                    (c.getNombreMedico() != null && c.getNombreMedico().toLowerCase().contains(texto.toLowerCase())) ||
                    (c.getMotivo() != null && c.getMotivo().toLowerCase().contains(texto.toLowerCase())) ||
                    (c.getFecha() != null && c.getFecha().toLowerCase().contains(texto.toLowerCase()));
            if (coincide) filtradas.add(c);
        }
        adapter = new CitaAdapter(filtradas, this::mostrarDetalle);
        binding.recyclerCitas.setAdapter(adapter);
    }

    private void mostrarDetalle(Cita c) {
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_detalle, null);
        TextView txt = view.findViewById(R.id.txtDetalle);
        Button btnAceptar = view.findViewById(R.id.btnAceptar);
        Button btnRechazar = view.findViewById(R.id.btnRechazar);
        Button btnFinalizar = view.findViewById(R.id.btnFinalizar);
        Button btnCancelar = view.findViewById(R.id.btnCancelar);
        Button btnEditar = view.findViewById(R.id.btnEditar);
        Button btnEliminar = view.findViewById(R.id.btnEliminar);

        SpannableStringBuilder detalle = new SpannableStringBuilder();
        agregarCampo(detalle, "Paciente:", c.getPaciente());
        agregarCampo(detalle, "Médico:", c.getNombreMedico());
        agregarCampo(detalle, "Fecha:", c.getFecha());
        agregarCampo(detalle, "Hora:", c.getHora());
        agregarCampo(detalle, "Motivo:", c.getMotivo());
        agregarCampo(detalle, "Estado:", c.getEstado());

        txt.setText(detalle);
        AlertDialog dialog = new AlertDialog.Builder(requireContext()).setView(view).create();

        if (rol.equals("MEDICO")) {
            btnCancelar.setVisibility(View.GONE);
            btnEliminar.setVisibility(View.VISIBLE);
            btnAceptar.setVisibility(Cita.ESTADO_PENDIENTE.equals(c.getEstado()) ? View.VISIBLE : View.GONE);
            btnRechazar.setVisibility(Cita.ESTADO_PENDIENTE.equals(c.getEstado()) ? View.VISIBLE : View.GONE);
            btnFinalizar.setVisibility(Cita.ESTADO_ACEPTADA.equals(c.getEstado()) ? View.VISIBLE : View.GONE);
            btnEditar.setVisibility(View.GONE);

            btnAceptar.setOnClickListener(v -> { c.setEstado(Cita.ESTADO_ACEPTADA); db.citaDao().actualizar(c); cargarCitas(); dialog.dismiss(); });
            btnRechazar.setOnClickListener(v -> { c.setEstado(Cita.ESTADO_RECHAZADA); db.citaDao().actualizar(c); cargarCitas(); dialog.dismiss(); });
            btnFinalizar.setOnClickListener(v -> { c.setEstado(Cita.ESTADO_FINALIZADA); db.citaDao().actualizar(c); cargarCitas(); dialog.dismiss(); });
            btnEliminar.setOnClickListener(v -> { db.citaDao().eliminar(c); cargarCitas(); dialog.dismiss(); });
        } else {
            btnAceptar.setVisibility(View.GONE);
            btnRechazar.setVisibility(View.GONE);
            btnFinalizar.setVisibility(View.GONE);
            btnEliminar.setVisibility(View.GONE);

            btnCancelar.setOnClickListener(v -> {
                if (!Cita.ESTADO_FINALIZADA.equals(c.getEstado())) {
                    c.setEstado(Cita.ESTADO_CANCELADA);
                    db.citaDao().actualizar(c);
                    cargarCitas();
                }
                dialog.dismiss();
            });
            btnEditar.setOnClickListener(v -> {
                if (Cita.ESTADO_PENDIENTE.equals(c.getEstado())) {
                    DialogEditarCita.mostrar(requireContext(), c, (fecha, hora, motivo) -> {
                        c.setFecha(fecha); c.setHora(hora); c.setMotivo(motivo);
                        db.citaDao().actualizar(c); cargarCitas();
                    });
                }
                dialog.dismiss();
            });
        }
        view.findViewById(R.id.btnCerrar).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void agregarCampo(SpannableStringBuilder builder, String titulo, String valor) {
        int inicio = builder.length();
        builder.append(titulo);
        builder.setSpan(new StyleSpan(Typeface.BOLD), inicio, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(" ").append(valor != null ? valor : "").append("\n\n");
    }
}
