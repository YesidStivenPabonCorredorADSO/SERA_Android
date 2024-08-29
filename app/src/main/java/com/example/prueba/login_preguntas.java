package com.example.prueba;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

public class login_preguntas extends AppCompatActivity {
    Button pasar, devolver;
    private Spinner spinnerTransporte, spinnerClima;
    private int idPreguntaActual;

    private String[] documentos = {
            "Seleccione",
            "Moto",
            "Carro",
            "Bicicleta",
            "Camión"
    };

    private String[] climas = {
            "Seleccione",
            "Día soleado",
            "Día lluvioso",
            "Día nubloso",
            "Día tormenta",
            "Día neblina"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_preguntas);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spinnerTransporte = findViewById(R.id.spinner3);
        spinnerClima = findViewById(R.id.spinner_clima);
        pasar = findViewById(R.id.pasar);
        devolver = findViewById(R.id.button_devolver);

        ArrayAdapter<String> adapterTransporte = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, documentos);
        spinnerTransporte.setAdapter(adapterTransporte);

        ArrayAdapter<String> adapterClima = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, climas);
        spinnerClima.setAdapter(adapterClima);

        // Obtener el ID de pregunta inicial desde SharedPreferences (por ejemplo, 579)
        SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        idPreguntaActual = sharedPref.getInt("id_pregunta_fk", 579);
        Log.d("login_preguntas", "ID Pregunta Actual: " + idPreguntaActual);

        pasar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spinnerTransporte.getSelectedItemPosition() == 0) {
                    Toast.makeText(login_preguntas.this, "Por favor, seleccione un medio de transporte.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (spinnerClima.getSelectedItemPosition() == 0) {
                    Toast.makeText(login_preguntas.this, "Por favor, seleccione cómo se encuentra el clima.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String transporte = documentos[spinnerTransporte.getSelectedItemPosition()];
                String clima = climas[spinnerClima.getSelectedItemPosition()];

                // Obtener el ID del transporte y clima seleccionados
                int idVehiculo = getIdVehiculo(transporte);
                int idEstadoClima = getIdEstadoClima(clima);

                // Calcular el porcentaje basado en la lógica
                int porcentaje = calcularPorcentaje(idVehiculo, idEstadoClima);

                // Obtener el ID de la pregunta actual y luego incrementarlo
                int idPregunta = idPreguntaActual;
                idPreguntaActual++;

                // Guardar el nuevo valor del ID en SharedPreferences
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("id_pregunta_fk", idPreguntaActual);
                editor.apply();

                // Llamar al método para insertar los datos con el ID de la pregunta actual
                enviarDatos(idPregunta, idVehiculo, idEstadoClima, porcentaje, transporte, clima);

                // Luego de enviar los datos, redirigir al resultado con el porcentaje
                Intent intent = new Intent(login_preguntas.this, resultado.class);
                intent.putExtra("id_preguntas_fk", String.valueOf(idPregunta));
                intent.putExtra("transporte", transporte);
                intent.putExtra("clima", clima);
                intent.putExtra("porcentaje", String.valueOf(porcentaje)); // Agregar el porcentaje al Intent
                startActivity(intent);
            }
        });

        devolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent regresar = new Intent(login_preguntas.this, login_preguntas2.class);
                startActivity(regresar);
            }
        });
    }

    private int getIdVehiculo(String transporte) {
        switch (transporte) {
            case "Moto": return 5;
            case "Carro": return 2;
            case "Bicicleta": return 3;
            case "Camión": return 4;
            default: return 0;
        }
    }

    private int getIdEstadoClima(String clima) {
        switch (clima) {
            case "Día soleado": return 1;
            case "Día lluvioso": return 2;
            case "Día nubloso": return 3;
            case "Día tormenta": return 4;
            case "Día neblina": return 5;
            default: return 0;
        }
    }

    private int calcularPorcentaje(int idVehiculo, int idEstadoClima) {
        // Aquí defines la lógica para calcular el porcentaje basado en los IDs de vehículo y clima
        return (idVehiculo * idEstadoClima) % 100; // Ejemplo de cálculo
    }

    private void enviarDatos(int idPregunta, int idVehiculo, int idEstadoClima, int porcentaje, String transporte, String clima) {
        SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String idUsuario = sharedPref.getString("id_usuario", "");
        String idUbicacionFk = sharedPref.getString("id_ubicacion_fk", "");

        // Verificación de datos
        if (idUsuario == null || idUsuario.isEmpty() || !idUsuario.matches("\\d+")) {
            Toast.makeText(getApplicationContext(), "ID de usuario inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (idUbicacionFk == null || idUbicacionFk.isEmpty() || !idUbicacionFk.matches("\\d+")) {
            Toast.makeText(getApplicationContext(), "ID de ubicación inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        // URL del script PHP que procesará los datos
        String urlPreguntas = "http://10.201.130.24/proyecto/preguntas.php";
        String urlResultados = "http://10.201.130.24/proyecto/resultado.php";

        // Crear la solicitud para insertar en tb_preguntas
        StringRequest requestPreguntas = new StringRequest(Request.Method.POST, urlPreguntas,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("login_preguntas", "Respuesta de preguntas.php: " + response);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.has("status")) {
                                String status = jsonResponse.getString("status");
                                if (status.equals("success")) {
                                    String idPreguntasFk = jsonResponse.getString("id_preguntas_fk");

                                    // Crear la solicitud para insertar en tb_resultado
                                    StringRequest requestResultados = new StringRequest(Request.Method.POST, urlResultados,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    Log.d("login_preguntas", "Respuesta de resultado.php: " + response);
                                                    try {
                                                        JSONObject jsonResponse = new JSONObject(response);
                                                        if (jsonResponse.has("status")) {
                                                            String status = jsonResponse.getString("status");
                                                            if (status.equals("success")) {
                                                                Toast.makeText(getApplicationContext(), "Datos enviados correctamente.", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                String errorMessage = jsonResponse.optString("error", "Error en la respuesta del servidor.");
                                                                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                                                            }
                                                        } else {
                                                            Toast.makeText(getApplicationContext(), "Respuesta inesperada del servidor.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                        Toast.makeText(getApplicationContext(), "Error al procesar la respuesta del servidor.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            },
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    Log.e("login_preguntas", "Error en la solicitud a resultado.php: " + error.toString());
                                                    String errorMessage = "Error al enviar datos a resultado.php.";
                                                    if (error.networkResponse != null) {
                                                        errorMessage += " Código de respuesta: " + error.networkResponse.statusCode;
                                                    }
                                                    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                                                }
                                            }) {
                                        @Override
                                        protected Map<String, String> getParams() throws AuthFailureError {
                                            Map<String, String> params = new HashMap<>();
                                            params.put("id_preguntas_fk", idPreguntasFk);
                                            params.put("id_vehiculo_fk", String.valueOf(idVehiculo));
                                            params.put("id_estado_clima_fk", String.valueOf(idEstadoClima));
                                            params.put("porcentaje", String.valueOf(porcentaje));
                                            params.put("transporte", transporte);
                                            params.put("clima", clima);
                                            params.put("id_usuario", idUsuario);
                                            params.put("id_ubicacion_fk", idUbicacionFk);
                                            return params;
                                        }
                                    };

                                    // Agregar la solicitud a la cola de solicitudes
                                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                                    requestQueue.add(requestResultados);

                                } else {
                                    String errorMessage = jsonResponse.optString("error", "Error en la respuesta del servidor.");
                                    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Respuesta inesperada del servidor.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error al procesar la respuesta del servidor.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("login_preguntas", "Error en la solicitud a preguntas.php: " + error.toString());
                        String errorMessage = "Error al enviar datos a preguntas.php.";
                        if (error.networkResponse != null) {
                            errorMessage += " Código de respuesta: " + error.networkResponse.statusCode;
                        }
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id_preguntas", String.valueOf(idPregunta));
                params.put("id_vehiculo_fk", String.valueOf(idVehiculo));
                params.put("id_estado_clima_fk", String.valueOf(idEstadoClima));
                params.put("porcentaje", String.valueOf(porcentaje));
                params.put("transporte", transporte);
                params.put("clima", clima);
                params.put("id_usuario", idUsuario);
                params.put("id_ubicacion_fk", idUbicacionFk);
                return params;
            }
        };

        // Agregar la solicitud a la cola de solicitudes
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(requestPreguntas);
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
            intent = new Intent(this, apartado_usuario_perfil_historial.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.perfil) {
            intent = new Intent(this, apartado_usuario_perfil.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.home) {
            intent = new Intent(this, Login.class);
            startActivity(intent);
        }
        return true;
    }
}