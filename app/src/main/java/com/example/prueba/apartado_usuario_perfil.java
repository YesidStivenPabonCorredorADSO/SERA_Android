package com.example.prueba;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class apartado_usuario_perfil extends AppCompatActivity {

    private EditText nombreEditText;
    private EditText apellidoEditText;
    private EditText correoEditText;
    private EditText contrasenaEditText;
    private EditText idUsuarioEditText;
    private Button guardarButton;
    private Button editarButton;
    private Button cerrar_sesion;
    private Button eliminarButton; // Nuevo botón para eliminar
    private boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_apartado_usuario_perfil);

        // Configurar la barra de acción
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Ajustar márgenes con respecto a las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.configuracion), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Referencias a los campos de texto
        nombreEditText = findViewById(R.id.nombreTextView);
        apellidoEditText = findViewById(R.id.apellidoTextView);
        correoEditText = findViewById(R.id.correoTextView);
        contrasenaEditText = findViewById(R.id.contrasenaTextView);
        idUsuarioEditText = findViewById(R.id.editTextIdUsuario);

        // Referencias a los botones
        guardarButton = findViewById(R.id.buttion_guardar);
        editarButton = findViewById(R.id.button_editar);
        eliminarButton = findViewById(R.id.button_eliminar); // Inicialización del botón eliminar
        cerrar_sesion =findViewById(R.id.button_cerrar);


        cerrar_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a = new Intent(apartado_usuario_perfil.this, MainActivity.class);
                startActivity(a);
            }
        });
        // Hacer que el campo ID sea solo lectura
        idUsuarioEditText.setEnabled(false);

        // Hacer que los demás campos sean solo lectura
        nombreEditText.setEnabled(false);
        apellidoEditText.setEnabled(false);
        correoEditText.setEnabled(false);
        contrasenaEditText.setEnabled(false);

        // Recuperar datos desde SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String nombre = sharedPref.getString("nombre", "No disponible");
        String apellido = sharedPref.getString("apellido", "No disponible");
        String correo = sharedPref.getString("correo", "No disponible");
        String contrasena = sharedPref.getString("contrasena", "No disponible");
        String idUsuario = sharedPref.getString("id_usuario", "No disponible");

        // Mostrar los datos en la vista
        nombreEditText.setText(nombre);
        apellidoEditText.setText(apellido);
        correoEditText.setText(correo);
        contrasenaEditText.setText(contrasena);
        idUsuarioEditText.setText(idUsuario);

        // Mostrar/Ocultar contraseña
        contrasenaEditText.setOnTouchListener((v, event) -> {
            final int Right = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= contrasenaEditText.getRight() - contrasenaEditText.getCompoundDrawables()[Right].getBounds().width()) {
                    int selection = contrasenaEditText.getSelectionEnd();
                    if (passwordVisible) {
                        contrasenaEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_off_24, 0);
                        contrasenaEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        passwordVisible = false;
                    } else {
                        contrasenaEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_24, 0);
                        contrasenaEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        passwordVisible = true;
                    }
                    contrasenaEditText.setSelection(selection);
                    return true;
                }
            }
            return false;
        });

        // Configurar botón "Guardar"
        guardarButton.setOnClickListener(v -> guardarDatos());

        // Configurar botón "Editar"
        editarButton.setOnClickListener(v -> {
            // Habilitar campos para edición
            nombreEditText.setEnabled(true);
            apellidoEditText.setEnabled(true);
            correoEditText.setEnabled(true);
            contrasenaEditText.setEnabled(true);
            guardarButton.setEnabled(true); // Habilitar botón guardar
            editarButton.setEnabled(false); // Deshabilitar botón editar
        });

        // Configurar botón "Eliminar"
        eliminarButton.setOnClickListener(v -> eliminarDatos());



        // Inicialmente deshabilitar botón guardar
        guardarButton.setEnabled(false);
    }

    private void guardarDatos() {
        // Obtener datos de los campos
        String id = idUsuarioEditText.getText().toString();
        String nombre = nombreEditText.getText().toString();
        String apellido = apellidoEditText.getText().toString();
        String correo = correoEditText.getText().toString();
        String contrasena = contrasenaEditText.getText().toString();

        // Crear el objeto JSON con los datos
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id_registro", id);
            jsonObject.put("nombre_registro", nombre);
            jsonObject.put("apellido_registro", apellido);
            jsonObject.put("correo_registro", correo);
            jsonObject.put("contrasena_registro", contrasena);
            // Aquí puedes incluir "genero_registro" y "fecha_registro" si tienes esos datos disponibles
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(apartado_usuario_perfil.this, "Error al crear el JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        // Enviar la solicitud al servidor
        String url = "http://10.201.130.24/proyecto/editar.php"; // Reemplaza con tu URL
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PATCH, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.has("error")) {
                                String errorMessage = response.getString("error");
                                Toast.makeText(apartado_usuario_perfil.this, errorMessage, Toast.LENGTH_SHORT).show();
                            } else {
                                String message = response.getString("message");
                                Toast.makeText(apartado_usuario_perfil.this, message, Toast.LENGTH_SHORT).show();
                                // Deshabilitar campos y botones después de guardar
                                nombreEditText.setEnabled(false);
                                apellidoEditText.setEnabled(false);
                                correoEditText.setEnabled(false);
                                contrasenaEditText.setEnabled(false);
                                guardarButton.setEnabled(false);
                                editarButton.setEnabled(true);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(apartado_usuario_perfil.this, "Error al procesar la respuesta: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(apartado_usuario_perfil.this, "Error al guardar datos: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    private void eliminarDatos() {
        String id = idUsuarioEditText.getText().toString();
        Log.d("EliminarDatos", "ID de usuario: " + id); // Verificar el ID

        // Crear el objeto JSON con el ID del usuario
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id_registro", id); // Asegúrate de que este nombre coincida con el del PHP
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(apartado_usuario_perfil.this, "Error al crear el JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        // Enviar la solicitud al servidor
        String url = "http://10.201.130.24/proyecto/eliminar.php"; // Reemplaza con tu URL
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Usar StringRequest para solicitudes POST con parámetros
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("EliminarDatos", "Respuesta del servidor: " + response); // Verificar la respuesta
                        if (response.contains("Registro eliminado exitosamente")) {
                            Toast.makeText(apartado_usuario_perfil.this, "Cuenta eliminada exitosamente", Toast.LENGTH_SHORT).show();
                            // Limpiar campos y regresar a la pantalla anterior
                            nombreEditText.setText("");
                            apellidoEditText.setText("");
                            correoEditText.setText("");
                            contrasenaEditText.setText("");
                            idUsuarioEditText.setText("");
                            SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.clear();
                            editor.apply();
                            Intent intent = new Intent(apartado_usuario_perfil.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(apartado_usuario_perfil.this, "Error al eliminar datos: " + response, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(apartado_usuario_perfil.this, "Error al eliminar datos: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_registro", id); // Asegúrate de que este nombre coincida con el del PHP
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        if (item.getItemId() == R.id.historial) {
            // Abrir actividad de perfil
            intent = new Intent(apartado_usuario_perfil.this, apartado_usuario_perfil_historial.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.home) {
            // Abrir actividad de historial
            intent = new Intent(apartado_usuario_perfil.this, Login.class);
            startActivity(intent);
        }
        return true;
    }
}

