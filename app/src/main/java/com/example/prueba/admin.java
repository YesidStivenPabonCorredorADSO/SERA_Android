package com.example.prueba;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class admin extends AppCompatActivity implements adaptador.OnUserStatusChangeListener {
    private RecyclerView recyclerView;
    private adaptador adapter;
    private List<elemento> datosList;
    Button cerrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        cerrar = findViewById(R.id.button_Cerrar);
        cerrar.setOnClickListener(view -> {
            Intent intent = new Intent(admin.this, MainActivity.class);
            startActivity(intent);
        });

        recyclerView = findViewById(R.id.recyclerView1);

        // Inicializa la lista de datos
        datosList = new ArrayList<>();

        // Configura el RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Crea y configura el adaptador, pasando 'datosList' y 'this'
        adapter = new adaptador(datosList, this);
        recyclerView.setAdapter(adapter);

        // Obtiene los datos de la API
        obtenerDatos();
    }

    @Override
    public void onStatusChange(elemento datos, String nuevoEstado) {
        // Llama a actualizarEstadoUsuario con el id y nuevoEstado
        actualizarEstadoUsuario(datos.getId(), nuevoEstado);
    }

    private void obtenerDatos() {
        String url = "http://10.201.130.24/proyecto/enlistar.php"; // Cambia esta URL según tu API

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    datosList.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            String id = jsonObject.optString("id_usuario", "N/A");
                            String nombre = jsonObject.optString("nombre_registro", "N/A");
                            String apellido = jsonObject.optString("apellido_registro", "N/A");
                            String correo = jsonObject.optString("correo_registro", "N/A");
                            String contrasena = jsonObject.optString("contrasena_registro", "N/A");
                            String genero = jsonObject.optString("genero_registro", "N/A");
                            String estadoCuenta = jsonObject.optString("estado_cuenta", "N/A");

                            elemento elemento = new elemento(id, nombre, apellido, correo, contrasena, genero, estadoCuenta);
                            datosList.add(elemento);

                        } catch (JSONException e) {
                            Log.e("JSON Parsing", "Error al parsear JSON: " + e.getMessage());
                        }
                    }

                    // Notificar al adaptador que los datos han cambiado
                    adapter.notifyDataSetChanged();
                },
                error -> {
                    Log.e("Volley", "Error en la solicitud: " + error.getMessage());
                    Toast.makeText(admin.this, "Error al obtener datos.", Toast.LENGTH_SHORT).show();
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private void actualizarEstadoUsuario(final String idUsuario, final String nuevoEstado) {
        String url = "http://192.168.1.10/proyecto/actualizar_estado.php"; // Cambia esta URL según tu API

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("Response", "Received response: " + response); // Muestra la respuesta completa en el Logcat

                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        String message = jsonResponse.optString("message", "Funciona");


                        Toast.makeText(admin.this, message, Toast.LENGTH_SHORT).show();

                        // Refresca los datos en el RecyclerView después de actualizar el estado
                        obtenerDatos(); // Llama a obtenerDatos para actualizar la lista
                    } catch (JSONException e) {
                        Log.e("JSON Error", "JSON parsing error: " + e.getMessage()); // Muestra detalles del error en el Logcat
                        e.printStackTrace();
                        Toast.makeText(admin.this, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("Volley", "Error en la solicitud: " + error.getMessage());
                    Toast.makeText(admin.this, "Error al enviar la solicitud", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_usuario", idUsuario);
                params.put("estado_cuenta", nuevoEstado);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
