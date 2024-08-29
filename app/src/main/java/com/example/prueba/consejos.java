package com.example.prueba;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class consejos extends AppCompatActivity {
    Button irhome;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consejos);
        irhome=findViewById(R.id.Siguiente_home);
        irhome.setOnClickListener(v -> {
            Intent intent = new Intent(consejos.this, Login.class);
            startActivity(intent);
        });;
    }
}