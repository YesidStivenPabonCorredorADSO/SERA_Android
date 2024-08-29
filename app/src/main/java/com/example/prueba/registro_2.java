package com.example.prueba;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class registro_2 extends AppCompatActivity {
    private EditText contrasena1, contrasena2, emailEditText;
    private Button siguiente, cancelar;
    private boolean password1Visible = false, password2Visible = false;

    // Regex para la contraseña
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!%*?&])[A-Za-z\\d$@$!%*?&]{8,15}$";
    // Regex para el correo electrónico
    private static final String EMAIL_REGEX = "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro2);

        siguiente = findViewById(R.id.Button_siguiente_re2);
        cancelar = findViewById(R.id.button_cancelar);
        contrasena1 = findViewById(R.id.edit_contra1);
        contrasena2 = findViewById(R.id.edit_contrasena2);
        emailEditText = findViewById(R.id.editTextText5);

        siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validarFormulario()) {
                    guardarDatosYEnviar();
                }
            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent e = new Intent(registro_2.this, registro.class);
                startActivity(e);
            }
        });

        contrasena1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int Right = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= contrasena1.getRight() - contrasena1.getCompoundDrawables()[Right].getBounds().width()) {
                        int selection = contrasena1.getSelectionEnd();
                        if (password1Visible) {
                            contrasena1.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_off_24, 0);
                            contrasena1.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            password1Visible = false;
                        } else {
                            contrasena1.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_24, 0);
                            contrasena1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            password1Visible = true;
                        }
                        contrasena1.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });

        contrasena2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int Right = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= contrasena2.getRight() - contrasena2.getCompoundDrawables()[Right].getBounds().width()) {
                        int selection = contrasena2.getSelectionEnd();
                        if (password2Visible) {
                            contrasena2.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_off_24, 0);
                            contrasena2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            password2Visible = false;
                        } else {
                            contrasena2.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_24, 0);
                            contrasena2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            password2Visible = true;
                        }
                        contrasena2.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private boolean validarFormulario() {
        String pass1 = contrasena1.getText().toString().trim();
        String pass2 = contrasena2.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese su correo electrónico.", Toast.LENGTH_SHORT).show();
            Log.d("Registro_2", "Correo electrónico vacío.");
            return false;
        }

        if (!Pattern.matches(EMAIL_REGEX, email)) {
            Toast.makeText(this, "El correo electrónico no es válido.", Toast.LENGTH_SHORT).show();
            Log.d("Registro_2", "Correo electrónico inválido: " + email);
            return false;
        }

        if (pass1.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese su contraseña.", Toast.LENGTH_SHORT).show();
            Log.d("Registro_2", "Contraseña 1 vacía.");
            return false;
        }

        if (pass2.isEmpty()) {
            Toast.makeText(this, "Por favor, confirme su contraseña.", Toast.LENGTH_SHORT).show();
            Log.d("Registro_2", "Contraseña 2 vacía.");
            return false;
        }

        if (!pass1.equals(pass2)) {
            Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show();
            Log.d("Registro_2", "Contraseñas no coinciden.");
            return false;
        }

        if (!Pattern.matches(PASSWORD_REGEX, pass1)) {
            Toast.makeText(this, "La contraseña debe contener al menos una letra mayúscula, una letra minúscula, un dígito y un carácter especial, y debe tener entre 8 y 15 caracteres.", Toast.LENGTH_LONG).show();
            Log.d("Registro_2", "Contraseña inválida: " + pass1);
            return false;
        }

        return true;
    }

    private void guardarDatosYEnviar() {
        // Obtener datos del primer registro desde SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserRegistration", MODE_PRIVATE);
        String nombre = sharedPreferences.getString("nombre_registro", "").trim();
        String apellido = sharedPreferences.getString("apellido_registro", "").trim();
        String fecha = sharedPreferences.getString("fecha_registro", "").trim();
        String id = sharedPreferences.getString("id_registro", "").trim(); // Obtener el ID
        String genero = sharedPreferences.getString("genero_registro", "").trim();

        // Obtener datos del segundo registro desde los EditText
        String email = emailEditText.getText().toString().trim();
        String password = contrasena1.getText().toString().trim();

        // Define el ID de rol (por ejemplo, 109 para admin)
        String idRolFk = "109"; // Cambia esto según el rol que desees asignar

        // Llamar al método para enviar datos
        ejecutarServicio("http://10.201.130.24/proyecto/insertar_datos.php", nombre, apellido, email, password, genero, fecha, id, idRolFk);
    }


    private void ejecutarServicio(String url, String nombre, String apellido, String email, String password, String genero, String fecha, String id, String idRolFk) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Registro_2", "Respuesta del servidor: " + response);
                        // Mostrar mensaje de éxito
                        Toast.makeText(getApplicationContext(), "OPERACIÓN EXITOSA", Toast.LENGTH_SHORT).show();

                        // Redirigir a MainActivity
                        Intent intent = new Intent(registro_2.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // Opcional: Finaliza esta actividad para evitar volver a ella con el botón de atrás
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Registro_2", "Error en la solicitud: " + error.toString());
                        // Mostrar mensaje de error
                        Toast.makeText(getApplicationContext(), "Error al registrar", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("nombre_registro", nombre);
                parametros.put("apellido_registro", apellido);
                parametros.put("correo_registro", email);
                parametros.put("contrasena_registro", password);
                parametros.put("genero_registro", genero);
                parametros.put("fecha_registro", fecha);
                parametros.put("id_registro", id);
                parametros.put("id_rol_fk", idRolFk); // Añadido para el rol
                return parametros;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}
