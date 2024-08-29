package com.example.prueba;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class apartado_usuario_perfil_historial extends AppCompatActivity {

    private static final String TAG = "ApartadoUsuarioPerfil";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apartado_usuario_perfil_historial);

        // Configurar la barra de acción
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Recuperar los datos desde SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String direccionOrigen = sharedPref.getString("direccion_origen", "Origen no disponible");
        String direccionDestino = sharedPref.getString("direccion_destino", "Destino no disponible");

        // Log para verificar los datos recuperados
        Log.d(TAG, "Dirección de origen: " + direccionOrigen);
        Log.d(TAG, "Dirección de destino: " + direccionDestino);

        // Obtener referencias a los TextView y establecer los valores
        TextView textViewOrigen = findViewById(R.id.textVieworigen);
        TextView textViewDestino = findViewById(R.id.textViewdestino);
        TextView textPorcentaje = findViewById(R.id.TextPorcentaje);

        textViewOrigen.setText(direccionOrigen);
        textViewDestino.setText(direccionDestino);

        // Recuperar el porcentaje pasado por el Intent
        Intent intent = getIntent();
        if (intent != null) {
            String porcentaje = intent.getStringExtra("porcentaje");
            if (porcentaje != null) {
                textPorcentaje.setText(porcentaje + "%");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflar el menú; esto añade los elementos a la barra de acción si está presente.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        if (item.getItemId() == R.id.perfil) {
            // Abrir actividad de perfil
            intent = new Intent(apartado_usuario_perfil_historial.this, apartado_usuario_perfil.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.home) {
            // Abrir actividad de historial
            intent = new Intent(apartado_usuario_perfil_historial.this, Login.class);
            startActivity(intent);
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
