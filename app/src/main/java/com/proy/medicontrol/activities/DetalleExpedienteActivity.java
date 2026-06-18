package com.proy.medicontrol.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.proy.medicontrol.R;

public class DetalleExpedienteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Permite que la UI se extienda detrás de las barras del sistema
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_detalle_expediente);

        View root = findViewById(android.R.id.content);

        // Ajusta el padding del contenedor raíz para que el contenido
        // no quede oculto detrás de las barras del sistema.
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(
                    v.getPaddingLeft(),
                    systemBars.top,     // Agrega padding superior = altura de la status bar
                    v.getPaddingRight(),
                    v.getPaddingBottom()
            );
            return insets;
        });

        WindowInsetsControllerCompat controller =
                new WindowInsetsControllerCompat(getWindow(), root);
        controller.setAppearanceLightStatusBars(true);

        TextView txt = findViewById(R.id.txtDetalleFull);

        String data =
                "Paciente: "      + getIntent().getStringExtra("paciente")      +
                "\n\nDiagnóstico: " + getIntent().getStringExtra("diagnostico")  +
                "\n\nTratamiento: " + getIntent().getStringExtra("tratamiento")  +
                "\n\nMedicamentos: " + getIntent().getStringExtra("medicamentos") +
                "\n\nObservaciones: " + getIntent().getStringExtra("observaciones") +
                "\n\nFecha: "       + getIntent().getStringExtra("fecha");
        txt.setText(data);
    }
}
