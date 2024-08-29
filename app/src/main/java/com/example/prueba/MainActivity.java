package com.example.prueba;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText etn, etp;
    TextView recuperar;
    boolean passwordVisible;
    Button login;
    Button registro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etn = findViewById(R.id.txt_correo);
        etp = findViewById(R.id.txt_password);
        login = findViewById(R.id.button_ingresar);
        registro = findViewById(R.id.Button_registro);

        passwordVisible = false;

        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a = new Intent(MainActivity.this, registro.class);
                startActivity(a);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String correo = etn.getText().toString();
                String contrasena = etp.getText().toString();
                if (validarEntradas(correo, contrasena)) {
                    verificarLogin(correo, contrasena);
                }
            }
        });

        etp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int Right = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= etp.getRight() - etp.getCompoundDrawables()[Right].getBounds().width()) {
                        int selection = etp.getSelectionEnd();
                        if (passwordVisible) {
                            etp.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_off_24, 0);
                            etp.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible = false;
                        } else {
                            etp.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_24, 0);
                            etp.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible = true;
                        }
                        etp.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private boolean validarEntradas(String correo, String contrasena) {
        if (correo.isEmpty()) {
            Toast.makeText(this, "Debes ingresar un correo", Toast.LENGTH_LONG).show();
            return false;
        }
        if (contrasena.isEmpty()) {
            Toast.makeText(this, "Debes ingresar una contraseña", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    private void verificarLogin(final String correo, final String contrasena) {
        String url = "http://10.201.130.24/proyecto/login.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Verifica si la respuesta contiene un error
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.has("error")) {
                                // Manejo del error
                                Toast.makeText(MainActivity.this, jsonResponse.getString("error"), Toast.LENGTH_LONG).show();
                            } else {
                                // Procesa la respuesta JSON
                                String idUsuario = jsonResponse.getString("id_usuario");
                                String nombre = jsonResponse.getString("nombre_registro");
                                String apellido = jsonResponse.getString("apellido_registro");
                                String idRol = jsonResponse.getString("id_rol_fk");
                                String estadoCuenta = jsonResponse.getString("estado_cuenta");

                                // Guarda los datos en SharedPreferences
                                SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("id_usuario", idUsuario);
                                editor.putString("nombre", nombre);
                                editor.putString("apellido", apellido);
                                editor.putString("correo", correo);
                                editor.putString("contrasena", contrasena);
                                editor.putString("id_rol_fk", idRol);
                                editor.putString("estado_cuenta", estadoCuenta);
                                editor.apply();

                                // Redirige basado en el rol, el estado de la cuenta y credenciales específicas
                                if (estadoCuenta.equals("activado")) {
                                    if (idRol.equals("109") && correo.equals("admin@example.com") && contrasena.equals("AdminPass123")) {
                                        // Redirigir al panel de administración solo para el administrador específico
                                        Intent intent = new Intent(MainActivity.this, admin.class);
                                        startActivity(intent);
                                        finish(); // Opcional, para cerrar la MainActivity
                                    } else {
                                        // Redirigir al panel de usuario
                                        Intent intent = new Intent(MainActivity.this, Login.class);
                                        startActivity(intent);
                                        finish(); // Opcional, para cerrar la MainActivity
                                    }
                                } else {
                                    Toast.makeText(MainActivity.this, "Cuenta desactivada", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Error al procesar la respuesta", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error en la conexión: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("correo_registro", correo);
                params.put("contrasena_registro", contrasena);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}
