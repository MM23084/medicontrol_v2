package com.proy.medicontrol.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.proy.medicontrol.database.AppDatabase;
import com.proy.medicontrol.databinding.ActivityLoginBinding;
import com.proy.medicontrol.entities.Usuario;
import com.proy.medicontrol.utils.SessionManager;
import com.proy.medicontrol.utils.ThemeManager;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ThemeManager.aplicarTema(this);

        super.onCreate(savedInstanceState);

        // Permite que la UI se extienda bajo las barras del sistema.
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);

        View decorView = getWindow().getDecorView();


        WindowInsetsControllerCompat controller =
                new WindowInsetsControllerCompat(getWindow(), decorView);
        controller.setAppearanceLightStatusBars(true);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SessionManager session = new SessionManager(this);


        if (session.estaLogueado()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        // Navegar a la pantalla de registro para nuevos usuarios.
        binding.btnRegistro.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );

        binding.btnLogin.setOnClickListener(v -> {

            String correo   = binding.txtCorreo.getText().toString().trim();
            String password = binding.txtPassword.getText().toString().trim();

            // Validaciones de campo vacío antes de consultar la BD.
            if (correo.isEmpty()) {
                Toast.makeText(this, "Ingrese su correo.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.isEmpty()) {
                Toast.makeText(this, "Ingrese su contraseña.", Toast.LENGTH_SHORT).show();
                return;
            }

            AppDatabase db = AppDatabase.getInstance(this);

            Usuario usuario = db.usuarioDao().login(correo, password);

            if (usuario == null) {


                Usuario existeCorreo = db.usuarioDao().buscarCorreo(correo);

                if (existeCorreo == null) {
                    Toast.makeText(this, "El correo no está registrado.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Contraseña incorrecta.", Toast.LENGTH_SHORT).show();
                }

                return;
            }

            session.guardarSesion(
                    usuario.getId(),
                    usuario.getNombre(),
                    usuario.getRol()
            );

            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}
