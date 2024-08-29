package com.example.prueba;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class Login extends AppCompatActivity {
    Button ir_registro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Configurar el botón de registro
        ir_registro = findViewById(R.id.Button_registro1);
        ir_registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a = new Intent(Login.this, login_preguntas2.class);
                startActivity(a);
            }
        });

        // Configurar la barra de acción
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
            intent = new Intent(Login.this, apartado_usuario_perfil.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.historial) {
            // Abrir actividad de historial
            intent = new Intent(Login.this, apartado_usuario_perfil_historial.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.home) {
        // Abrir actividad de historial
        intent = new Intent(Login.this, MainActivity.class);
        startActivity(intent);
    }
        return true;
    }
}
