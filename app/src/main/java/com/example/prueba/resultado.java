package com.example.prueba;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;

public class resultado extends AppCompatActivity {

    private static final int ANIMATION_DURATION_MS = 5000; // Duración en milisegundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Ajustar márgenes para la vista Edge-to-Edge
        setContentView(R.layout.activity_resultado);

        // Inicializar y cargar la animación en LottieAnimationView
        LottieAnimationView animationView = findViewById(R.id.animationView);
        animationView.setAnimation(R.raw.animation_2);
        animationView.playAnimation();

        // Inicializar el contenido que debe mostrarse después de la animación
        View contentView = findViewById(R.id.scrollView4);

        // Ocultar el contenido inicialmente
        contentView.setVisibility(View.GONE);

        // Programar el ocultamiento de la animación y la visualización del contenido después de un tiempo determinado
        new Handler().postDelayed(() -> {
            animationView.setVisibility(View.GONE);
            contentView.setVisibility(View.VISIBLE);

            // Mostrar el porcentaje en el EditText
            EditText resultadoEditText = findViewById(R.id.editTextText11);
            String porcentaje = getIntent().getStringExtra("porcentaje"); // Obtener el porcentaje enviado a través del Intent
            if (porcentaje != null) {
                resultadoEditText.setText(porcentaje);
            } else {
                resultadoEditText.setText("Porcentaje no disponible");
            }
        }, ANIMATION_DURATION_MS);

        // Ajustar los márgenes de la vista principal
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar botones y añadir los OnClickListeners
        Button buttonConsejo = findViewById(R.id.Button_consejo);
        Button buttonCancelar = findViewById(R.id.butoon_cancelar);

        buttonConsejo.setOnClickListener(v -> {
            Intent intent = new Intent(resultado.this, consejos.class);
            startActivity(intent);
        });

        buttonCancelar.setOnClickListener(v -> {
            Intent intent = new Intent(resultado.this, login_preguntas.class);
            startActivity(intent);
        });
    }
}
