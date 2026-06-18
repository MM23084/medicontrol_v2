package com.proy.medicontrol.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.proy.medicontrol.R;
import com.proy.medicontrol.activities.LoginActivity;
import com.proy.medicontrol.database.AppDatabase;
import com.proy.medicontrol.databinding.FragmentSettingsBinding;
import com.proy.medicontrol.entities.Usuario;
import com.proy.medicontrol.utils.SessionManager;
import com.proy.medicontrol.utils.ThemeManager;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private AppDatabase db;
    private Usuario usuarioActual;

    // ── Lanzador para seleccionar nueva foto de perfil ───────
    private final ActivityResultLauncher<String> seleccionarImagen =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {
                        if (uri != null && usuarioActual != null) {
                            actualizarFotoPerfil(uri);
                        }
                    }
            );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        db = AppDatabase.getInstance(requireContext());

        cargarUsuario();
        marcarTemaActual();

        // Al tocar la imagen de perfil, se abre la galería para cambiarla.
        binding.imgPerfil.setOnClickListener(v -> seleccionarImagen.launch("image/*"));

        // Listeners para el cambio de tema.
        binding.rbClaro.setOnClickListener(v -> {
            ThemeManager.guardarTema(requireContext(), AppCompatDelegate.MODE_NIGHT_NO);
            requireActivity().recreate();
        });

        binding.rbOscuro.setOnClickListener(v -> {
            ThemeManager.guardarTema(requireContext(), AppCompatDelegate.MODE_NIGHT_YES);
            requireActivity().recreate();
        });

        binding.rbSistema.setOnClickListener(v -> {
            ThemeManager.guardarTema(requireContext(), AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            requireActivity().recreate();
        });

        binding.btnCerrarSesion.setOnClickListener(v -> {
            new SessionManager(requireContext()).cerrarSesion();
            Intent i = new Intent(requireContext(), LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });

        return binding.getRoot();
    }

    /**
     * Actualiza la foto de perfil en la base de datos y en la UI.
     */
    private void actualizarFotoPerfil(Uri uri) {
        try {
            // Solicitar permiso persistente para la nueva imagen.
            requireContext().getContentResolver().takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
            );

            // Guardar la nueva URI en el objeto usuario y actualizar la BD.
            usuarioActual.setFotoPerfil(uri.toString());
            db.usuarioDao().actualizar(usuarioActual);

            // Refrescar la imagen en la vista usando Glide para mejor rendimiento.
            Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.paciente_default)
                    .circleCrop()
                    .into(binding.imgPerfil);

            Toast.makeText(requireContext(), "Foto de perfil actualizada", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error al actualizar la foto", Toast.LENGTH_SHORT).show();
        }
    }

    private void marcarTemaActual() {
        int modo = ThemeManager.obtenerTema(requireContext());
        if (modo == AppCompatDelegate.MODE_NIGHT_NO) {
            binding.rbClaro.setChecked(true);
        } else if (modo == AppCompatDelegate.MODE_NIGHT_YES) {
            binding.rbOscuro.setChecked(true);
        } else {
            binding.rbSistema.setChecked(true);
        }
    }

    private void cargarUsuario() {
        SessionManager session = new SessionManager(requireContext());
        int id = session.obtenerIdUsuario();

        usuarioActual = db.usuarioDao().obtenerPorId(id);

        if (usuarioActual == null) return;

        binding.txtNombre.setText(usuarioActual.getNombre());
        binding.txtCorreo.setText(usuarioActual.getCorreo());
        binding.txtRol.setText(usuarioActual.getRol());

        // Cargar la foto actual.
        String foto = usuarioActual.getFotoPerfil();
        if (foto != null && !foto.equals("DEFAULT")) {
            Glide.with(this)
                    .load(Uri.parse(foto))
                    .placeholder(R.drawable.paciente_default)
                    .error(R.drawable.paciente_default)
                    .circleCrop()
                    .into(binding.imgPerfil);
        } else {
            binding.imgPerfil.setImageResource(R.drawable.paciente_default);
        }
    }
}
