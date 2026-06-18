package com.proy.medicontrol.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.proy.medicontrol.database.AppDatabase;
import com.proy.medicontrol.databinding.ActivityRegisterBinding;
import com.proy.medicontrol.entities.Usuario;
import com.proy.medicontrol.utils.ThemeManager;
import android.util.Patterns;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private Uri fotoSeleccionada;

    private final ActivityResultLauncher<String> seleccionarImagen =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {
                        if (uri == null) return;
                        fotoSeleccionada = uri;
                        binding.imgPerfil.setImageURI(uri);
                        try {
                            getContentResolver().takePersistableUriPermission(
                                    uri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                            );
                        } catch (Exception ignored) {}
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.aplicarTema(this);
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        WindowInsetsControllerCompat controller =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(true);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.imgPerfil.setImageResource(
                com.proy.medicontrol.R.drawable.paciente_default
        );

        // ── Lógica para mostrar/ocultar Cédula Profesional ──
        binding.rgRol.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == com.proy.medicontrol.R.id.rbMedico) {
                binding.layoutCedula.setVisibility(View.VISIBLE);
            } else {
                binding.layoutCedula.setVisibility(View.GONE);
                binding.txtCedula.setText(""); // Limpiar si cambia a paciente
            }
        });

        binding.btnSeleccionarFoto.setOnClickListener(v ->
                seleccionarImagen.launch("image/*")
        );

        binding.btnGuardar.setOnClickListener(v ->
                registrarUsuario()
        );
    }

    private void registrarUsuario() {
        String nombre   = binding.txtNombre.getText().toString().trim();
        String correo   = binding.txtCorreo.getText().toString().trim();
        String password = binding.txtPassword.getText().toString().trim();
        String cedula   = binding.txtCedula.getText().toString().trim();

        boolean esMedico = binding.rbMedico.isChecked();
        String rol = esMedico ? "MEDICO" : "PACIENTE";

        // ── Validaciones generales ───────────────────────────
        if (nombre.isEmpty()) {
            Toast.makeText(this, "Ingrese un nombre.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (correo.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(this, "Ingrese un correo válido.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres.", Toast.LENGTH_SHORT).show();
            return;
        }

        // ── Validación específica para Médicos ───────────────
        if (esMedico && cedula.isEmpty()) {
            Toast.makeText(this, "La cédula profesional es obligatoria para médicos.", Toast.LENGTH_SHORT).show();
            return;
        }

        AppDatabase db = AppDatabase.getInstance(this);

        if (db.usuarioDao().buscarCorreo(correo) != null) {
            Toast.makeText(this, "Correo ya registrado.", Toast.LENGTH_SHORT).show();
            return;
        }

        String fotoPerfil = fotoSeleccionada != null ? fotoSeleccionada.toString() : "DEFAULT";

        // ── Inserción con el nuevo campo de cédula ──────────
        Usuario usuario = new Usuario(
                nombre,
                correo,
                password,
                rol,
                fotoPerfil,
                esMedico ? cedula : null // Solo guardamos cédula si es médico
        );

        db.usuarioDao().insertar(usuario);

        Toast.makeText(this, "Registro exitoso como " + rol.toLowerCase() + ".", Toast.LENGTH_SHORT).show();
        finish();
    }
}
