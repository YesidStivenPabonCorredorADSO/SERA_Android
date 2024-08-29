package com.example.prueba;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class login_preguntas2 extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;
    private static final String TAG = "login_preguntas2";
    String idusuario ,id_pregunta;

    private EditText editTextLocation;
    private EditText editTextDestination;
    private Button buttonGPS, buttonConversion, buttonEnrutar, buttonMapa, buttonSiguiente, buttonCancelar;

    private FusedLocationProviderClient fusedLocationClient;
    private LatLng currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_preguntas2);
        SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        idusuario = sharedPref.getString("id_usuario", "");
        editTextLocation = findViewById(R.id.editTextText);
        editTextDestination = findViewById(R.id.editTextText2);
        buttonGPS = findViewById(R.id.button17);
        buttonConversion = findViewById(R.id.button4);
        buttonEnrutar = findViewById(R.id.button18);
        buttonMapa = findViewById(R.id.Button_mapa);
        buttonSiguiente = findViewById(R.id.Button_siguiente);
        buttonCancelar = findViewById(R.id.button19);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        obtenerIdPregunta(idusuario);
        Log.d("ID DEL USUARIO: ", idusuario);
        buttonGPS.setOnClickListener(v -> getCurrentLocation());
        buttonConversion.setOnClickListener(v -> {
            convertAddressToCoordinates(editTextLocation);
            convertAddressToCoordinates(editTextDestination);
        });
        buttonEnrutar.setOnClickListener(v -> openMapWithRoute());
        buttonMapa.setOnClickListener(v -> openMap());

        // Configuración de la barra de acción
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Mostrar el botón de "volver"



        buttonSiguiente.setOnClickListener(v -> {
            String ubicacionActual = editTextLocation.getText().toString().trim();
            String ubicacionDestino = editTextDestination.getText().toString().trim();

            if (ubicacionActual.isEmpty()) {
                editTextLocation.setError("La ubicación actual es obligatoria");
                editTextLocation.requestFocus();
            } else if (ubicacionDestino.isEmpty()) {
                editTextDestination.setError("La ubicación de destino es obligatoria");
                editTextDestination.requestFocus();
            } else {
                // Si ambos campos están llenos, se envían los datos y se procede a la siguiente actividad
                enviarDatosUbicacion();
                Intent intent = new Intent(login_preguntas2.this, login_preguntas.class);
                startActivity(intent);
            }
        });


        buttonCancelar.setOnClickListener(v -> {
            Intent intent = new Intent(login_preguntas2.this, Login.class);
            startActivity(intent);
        });
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        Task<Location> locationTask = fusedLocationClient.getLastLocation();
        locationTask.addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                editTextLocation.setText(currentLocation.latitude + ", " + currentLocation.longitude);
            } else {
                Toast.makeText(login_preguntas2.this, "No se pudo obtener la ubicación actual.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(login_preguntas2.this, "Error al obtener la ubicación: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error al obtener la ubicación: " + e.getMessage(), e);
        });
    }

    private void convertAddressToCoordinates(EditText editText) {
        String address = editText.getText().toString();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address location = addresses.get(0);
                LatLng coordinates = new LatLng(location.getLatitude(), location.getLongitude());
                editText.setText(coordinates.latitude + ", " + coordinates.longitude);
            } else {
                Toast.makeText(login_preguntas2.this, "Dirección no encontrada.", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(login_preguntas2.this, "Error al convertir la dirección: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error al convertir la dirección: " + e.getMessage(), e);
        }
    }

    private void openMapWithRoute() {
        String originText = editTextLocation.getText().toString();
        String destinationText = editTextDestination.getText().toString();

        if (!originText.isEmpty() && !destinationText.isEmpty()) {
            try {
                String[] originCoordinates = originText.split(", ");
                String[] destinationCoordinates = destinationText.split(", ");

                if (originCoordinates.length == 2 && destinationCoordinates.length == 2) {
                    double originLat = Double.parseDouble(originCoordinates[0].trim());
                    double originLng = Double.parseDouble(originCoordinates[1].trim());
                    double destinationLat = Double.parseDouble(destinationCoordinates[0].trim());
                    double destinationLng = Double.parseDouble(destinationCoordinates[1].trim());

                    Intent intent = new Intent(login_preguntas2.this, MapsActivity.class);
                    intent.putExtra("originLat", originLat);
                    intent.putExtra("originLng", originLng);
                    intent.putExtra("destinationLat", destinationLat);
                    intent.putExtra("destinationLng", destinationLng);
                    startActivity(intent);
                } else {
                    Toast.makeText(login_preguntas2.this, "Coordenadas no válidas.", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(login_preguntas2.this, "Error al convertir las coordenadas: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error al convertir las coordenadas: " + e.getMessage(), e);
            }
        } else {
            Toast.makeText(login_preguntas2.this, "Por favor, ingrese todas las ubicaciones necesarias.", Toast.LENGTH_SHORT).show();
        }
    }
    public void obtenerIdPregunta(String idUsuario) {
        String url = "http://10.201.130.24/proyecto/ubicacion.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String status = jsonResponse.getString("status");

                            if (status.equals("success")) {
                                id_pregunta = jsonResponse.getString("id_pregunta");
                                Log.d("ID DEL PREGUNTA: ", id_pregunta);
                                // Aquí puedes hacer algo con el id_pregunta, como guardarlo o usarlo en tu aplicación.
                            } else {
                                String message = jsonResponse.optString("message", "Error desconocido");
                                // Mostrar mensaje de error
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getApplicationContext(), "Error en la solicitud: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("id_usuario", idUsuario);
                return parametros;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void openMap() {
        String locationCoordinates = editTextLocation.getText().toString();
        Uri gmmIntentUri = Uri.parse("geo:" + locationCoordinates + "?q=" + locationCoordinates);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }


    private void enviarDatosUbicacion() {
        String url = "http://10.201.130.24/proyecto/ubicacion.php";
        String direccionOrigen = editTextLocation.getText().toString();
        String direccionDestino = editTextDestination.getText().toString();

        if (direccionOrigen.isEmpty() || direccionDestino.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese todas las ubicaciones necesarias.", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Respuesta del servidor: " + response);

                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.getString("status").equals("success")) {
                                String idUbicacionFk = jsonResponse.getString("id_ubicacion_fk");

                                // Guardar idUbicacionFk y las direcciones en SharedPreferences
                                SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("id_ubicacion_fk", idUbicacionFk);
                                editor.putString("direccion_origen", direccionOrigen);
                                editor.putString("direccion_destino", direccionDestino);
                                editor.apply();

                                enviarDatosHistorial(idUbicacionFk);
                                // Iniciar la actividad de historial y pasar los datos
                                Intent intent = new Intent(login_preguntas2.this, login_preguntas.class);
                                startActivity(intent);

                            } else {
                                String errorMessage = jsonResponse.optString("message", "Error en la respuesta del servidor.");
                                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
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
                        Log.e(TAG, "Error en la solicitud: " + error.toString());
                        Toast.makeText(getApplicationContext(), "Error al enviar datos de ubicación.", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("direccion_origen", direccionOrigen);
                parametros.put("direccion_destino", direccionDestino);
                return parametros;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }



    private void enviarDatosHistorial(String idUbicacionFk) {
        String url = "http://10.201.130.24/proyecto/historial.php";

        // Guardar en SharedPreferences para persistencia
        SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("id_ubicacion_fk", idUbicacionFk);
        editor.apply();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Respuesta del servidor historial: " + response);

                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.getString("status").equals("success")) {
                                Toast.makeText(getApplicationContext(), "Historial guardado exitosamente.", Toast.LENGTH_SHORT).show();
                            } else {
                                String errorMessage = jsonResponse.optString("message", "Error en la respuesta del servidor.");
                                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
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
                        Log.e(TAG, "Error en la solicitud historial: " + error.toString());
                        Toast.makeText(getApplicationContext(), "Error al enviar datos de historial.", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("id_ubicacion_fk", idUbicacionFk);
                Log.d(TAG, "Parámetros enviados: " + parametros.toString());
                return parametros;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
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
            intent = new Intent(login_preguntas2.this, apartado_usuario_perfil_historial.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.perfil) {
            // Abrir actividad de historial
            intent = new Intent(login_preguntas2.this, apartado_usuario_perfil.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.home) {
            // Abrir actividad de historial
            intent = new Intent(login_preguntas2.this, Login.class);
            startActivity(intent);
        }
        return true;
    }
}