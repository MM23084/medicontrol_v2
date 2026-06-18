package com.proy.medicontrol.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.proy.medicontrol.adapters.DashboardCitaAdapter;
import com.proy.medicontrol.database.AppDatabase;
import com.proy.medicontrol.databinding.FragmentDashboardPacienteBinding;
import com.proy.medicontrol.entities.Cita;
import com.proy.medicontrol.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DashboardPacienteFragment extends Fragment {

    private FragmentDashboardPacienteBinding binding;
    private AppDatabase db;
    private SessionManager session;

    // Lista completa de citas del paciente. Se usa como fuente
    // para calcular el resumen y para poblar ambos RecyclerViews.
    private List<Cita> listaCitas = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentDashboardPacienteBinding.inflate(inflater, container, false);

        db = AppDatabase.getInstance(requireContext());
        session = new SessionManager(requireContext());

        // Configurar los LayoutManagers de los RecyclerViews antes
        // de asignar adaptadores para evitar errores de renderizado.
        setupRecycler();
        cargarDatos();

        return binding.getRoot();
    }

    /** Asigna LinearLayoutManager a ambos RecyclerViews del dashboard. */
    private void setupRecycler() {
        binding.recyclerMisCitas.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerProximas.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    /**
     * Carga las citas del paciente en sesión y luego actualiza
     * el resumen numérico y las dos listas de la pantalla.
     */
    private void cargarDatos() {

        // Obtener el nombre y ID del paciente desde la sesión activa.
        String nombrePaciente = session.obtenerNombreCompleto();
        int idUsuario = session.obtenerIdUsuario();

        // Saludo personalizado al paciente.
        binding.txtBienvenida.setText("Hola " + nombrePaciente);

        // Solo las citas de este paciente, ordenadas por fecha descendente.
        listaCitas = db.citaDao().obtenerPorUsuario(idUsuario);

        if (listaCitas == null) {
            listaCitas = new ArrayList<>();
        }

        mostrarResumen();
        cargarProximas();
        cargarListaCompleta();
    }

    /**
     * Calcula y muestra los contadores del panel:
     * - Total de citas del paciente.
     * - Citas "próximas y activas": fecha >= hoy y estado no terminal.
     */
    private void mostrarResumen() {

        binding.txtTotalCitas.setText(String.valueOf(listaCitas.size()));

        int proximas = 0;

        // Fecha de hoy en formato ISO para comparación de strings.
        String hoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        for (Cita c : listaCitas) {

            if (c.getFecha() == null) continue;

            // La fecha de la cita es hoy o en el futuro.
            boolean fechaValida = c.getFecha().compareTo(hoy) >= 0;

            // La cita no está en un estado terminal (cancelada/rechazada/finalizada).
            boolean activa =
                    !Cita.ESTADO_CANCELADA.equals(c.getEstado()) &&
                    !Cita.ESTADO_RECHAZADA.equals(c.getEstado()) &&
                    !Cita.ESTADO_FINALIZADA.equals(c.getEstado());

            if (fechaValida && activa) {
                proximas++;
            }
        }

        binding.txtProximas.setText(String.valueOf(proximas));
    }

    /**
     * Filtra la lista completa y muestra solo las citas próximas
     * activas en el RecyclerView superior del dashboard.
     */
    private void cargarProximas() {

        List<Cita> proximas = new ArrayList<>();

        String hoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        for (Cita c : listaCitas) {

            if (c.getFecha() == null) continue;

            boolean fechaValida = c.getFecha().compareTo(hoy) >= 0;

            boolean activa =
                    !Cita.ESTADO_CANCELADA.equals(c.getEstado()) &&
                    !Cita.ESTADO_RECHAZADA.equals(c.getEstado()) &&
                    !Cita.ESTADO_FINALIZADA.equals(c.getEstado());

            if (fechaValida && activa) {
                proximas.add(c);
            }
        }

        binding.recyclerProximas.setAdapter(new DashboardCitaAdapter(proximas));
    }

    /** Muestra la lista completa de citas del paciente sin filtros. */
    private void cargarListaCompleta() {
        binding.recyclerMisCitas.setAdapter(new DashboardCitaAdapter(listaCitas));
    }

    /**
     * onResume se ejecuta cada vez que el Fragment vuelve a ser visible.
     */
    @Override
    public void onResume() {
        super.onResume();
        cargarDatos();
    }
}
