package com.example.prueba;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng origin;
    private LatLng destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtén el SupportMapFragment y solicita la notificación cuando el mapa esté listo para ser usado.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Obtén las coordenadas de la Intent
        double originLat = getIntent().getDoubleExtra("originLat", 0);
        double originLng = getIntent().getDoubleExtra("originLng", 0);
        double destinationLat = getIntent().getDoubleExtra("destinationLat", 0);
        double destinationLng = getIntent().getDoubleExtra("destinationLng", 0);

        // Verifica que las coordenadas sean válidas antes de usarlas
        if (originLat == 0 && originLng == 0 || destinationLat == 0 && destinationLng == 0) {
            // Manejo de error si las coordenadas no son válidas
            // Por ejemplo, mostrar un mensaje al usuario o usar coordenadas predeterminadas
        }

        origin = new LatLng(originLat, originLng);
        destination = new LatLng(destinationLat, destinationLng);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Agrega marcadores para el origen y el destino
        mMap.addMarker(new MarkerOptions().position(origin).title("Origin"));
        mMap.addMarker(new MarkerOptions().position(destination).title("Destination"));

        // Dibuja la ruta entre el origen y el destino
        drawRoute();

        // Mueve la cámara al origen con un nivel de zoom de 10
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 10));
    }

    private void drawRoute() {
        if (origin != null && destination != null) {
            PolylineOptions polylineOptions = new PolylineOptions()
                    .add(origin)
                    .add(destination)
                    .width(5)
                    .color(android.graphics.Color.BLUE);
            Polyline polyline = mMap.addPolyline(polylineOptions);
        }
    }
}
