package com.proy.medicontrol.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.proy.medicontrol.database.AppDatabase;
import com.proy.medicontrol.databinding.FragmentDashboardBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import com.proy.medicontrol.utils.SessionManager;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private AppDatabase db;
    private SessionManager session;

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentDashboardBinding.inflate(inflater, container, false);

        db = AppDatabase.getInstance(requireContext());
        session = new SessionManager(requireContext());

        cargarDatos();

        return binding.getRoot();
    }

    private void cargarDatos() {

        int idMedico = session.obtenerIdUsuario();
        String nombreMedico = session.obtenerNombreCompleto();
        
        binding.txtBienvenida.setText("Bienvenido Dr. " + nombreMedico);

        int citas = db.citaDao().contarCitasPorMedico(idMedico);

        int expedientes = db.expedienteDao().contarExpedientesPorMedico(idMedico);

        int totalPacientes = db.usuarioDao().contarUsuarios();

        binding.txtPacientes.setText(String.valueOf(totalPacientes));
        binding.txtCitas.setText(String.valueOf(citas));
        binding.txtExpedientes.setText(String.valueOf(expedientes));

        String fecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                .format(new Date());

        binding.txtFecha.setText("Fecha: " + fecha);
    }
}
