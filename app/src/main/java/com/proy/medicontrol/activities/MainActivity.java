package com.proy.medicontrol.activities;

import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;

import com.proy.medicontrol.R;
import com.proy.medicontrol.databinding.ActivityMainBinding;
import com.proy.medicontrol.fragments.*;
import com.proy.medicontrol.utils.SessionManager;
import com.proy.medicontrol.utils.ThemeManager;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SessionManager session;


    private String fragmentActual = "inicio";

    private String rol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ThemeManager.aplicarTema(this);

        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(true);

        session = new SessionManager(this);

        rol = session.obtenerRol();
        if (rol == null) rol = "PACIENTE";
        rol = rol.toUpperCase();


        if (savedInstanceState != null) {
            fragmentActual = savedInstanceState.getString("fragment", "inicio");
        }
        configurarMenuPorRol(rol);

        cargarFragmentSegunEstado(rol);

        binding.bottomNavigation.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.bottom_inicio) {
                fragmentActual = "inicio";
                // El Dashboard es diferente según el rol del usuario.
                cargarFragment(
                        rol.equals("PACIENTE")
                                ? new DashboardPacienteFragment()
                                : new DashboardFragment()
                );
                return true;
            }

            if (id == R.id.bottom_pacientes) {
                // Verificar acceso antes de cargar (solo MEDICO puede entrar).
                if (!puedeAcceder("PACIENTES")) return false;
                fragmentActual = "pacientes";
                cargarFragment(new PacientesFragment());
                return true;
            }

            if (id == R.id.bottom_citas) {
                // Citas es accesible para ambos roles pero con UI diferente.
                fragmentActual = "citas";
                cargarFragment(new CitasFragment());
                return true;
            }

            if (id == R.id.bottom_expedientes) {
                if (!puedeAcceder("EXPEDIENTES")) return false;
                fragmentActual = "expedientes";
                cargarFragment(new ExpedientesFragment());
                return true;
            }

            if (id == R.id.bottom_configuracion) {
                fragmentActual = "config";
                cargarFragment(new SettingsFragment());
                return true;
            }

            return false;
        });
    }


    private boolean puedeAcceder(String modulo) {

        if (rol.equals("MEDICO")) return true;

        switch (modulo) {
            case "PACIENTES":
            case "EXPEDIENTES":
                return false;   // Módulos exclusivos del MEDICO
            default:
                return true;
        }
    }


    private void cargarFragmentSegunEstado(String rol) {

        Fragment frag;

        switch (fragmentActual) {
            case "config":
                frag = new SettingsFragment();
                break;
            case "pacientes":
                frag = puedeAcceder("PACIENTES")
                        ? new PacientesFragment()
                        : new DashboardPacienteFragment();
                break;
            case "citas":
                frag = new CitasFragment();
                break;
            case "expedientes":
                frag = puedeAcceder("EXPEDIENTES")
                        ? new ExpedientesFragment()
                        : new DashboardPacienteFragment();
                break;
            case "inicio":
            default:
                frag = rol.equals("PACIENTE")
                        ? new DashboardPacienteFragment()
                        : new DashboardFragment();
                break;
        }

        cargarFragment(frag);
    }


    private void configurarMenuPorRol(String rol) {

        Menu menu = binding.bottomNavigation.getMenu();

        boolean esPaciente = rol.equals("PACIENTE");

        // Ocultar módulos exclusivos del médico en la UI.
        menu.findItem(R.id.bottom_pacientes).setVisible(!esPaciente);
        menu.findItem(R.id.bottom_expedientes).setVisible(!esPaciente);
    }

    private void cargarFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contenedor, fragment)
                .commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("fragment", fragmentActual);
    }
}
