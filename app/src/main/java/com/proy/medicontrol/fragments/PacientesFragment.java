package com.proy.medicontrol.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.proy.medicontrol.adapters.PacienteAdapter;
import com.proy.medicontrol.database.AppDatabase;
import com.proy.medicontrol.databinding.FragmentPacientesBinding;
import com.proy.medicontrol.entities.Usuario;

import java.util.List;

public class PacientesFragment extends Fragment {

    private FragmentPacientesBinding binding;
    private AppDatabase db;

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentPacientesBinding.inflate(inflater, container, false);

        db = AppDatabase.getInstance(requireContext());

        // Configurar el RecyclerView con un layout vertical antes
        // de asignar el adaptador.
        binding.recyclerPacientes.setLayoutManager(
                new LinearLayoutManager(getContext())
        );

        cargarPacientes();

        return binding.getRoot();
    }

    /**
     * Consulta todos los usuarios con rol PACIENTE y los muestra
     * en el RecyclerView. PacienteAdapter se encarga de renderizar
     * el nombre, correo y foto de cada paciente.
     */
    private void cargarPacientes() {

        // Obtener todos los usuarios con rol PACIENTE, ordenados por nombre.
        List<Usuario> lista = db.usuarioDao().obtenerPorRol("PACIENTE");

        PacienteAdapter adapter = new PacienteAdapter(
                lista,
                usuario -> {
                    // El click sobre un paciente está definido pero
                    // no ejecuta ninguna acción en esta versión.
                }
        );

        binding.recyclerPacientes.setAdapter(adapter);
    }
}
