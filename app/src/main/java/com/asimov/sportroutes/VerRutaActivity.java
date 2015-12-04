package com.asimov.sportroutes;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class VerRutaActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Ruta ruta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_ruta);

        //Se oculta el statusBar para dejar la pantalla más visicble.
        ocultarStatusBar();

        //Obtenemos el intent origen para tratar la ruta mandada.
        Intent intent = getIntent();
        ruta = intent.getParcelableExtra("ruta");

        //Determinamos el título
        TextView textNombre = (TextView) findViewById(R.id.NombreRuta);
        textNombre.setText(ruta.getNombre());
        //Operaciones sobre el mapa.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);
    }

    /**
     * Oculta el statusBar del dispositivo, y si este se vuelve visible por alguna razón vuelve a
     *  ocultarlo hasta que se cambie de activity.
     */
    private void ocultarStatusBar(){
        final View decorView = getWindow().getDecorView();
        // Oculta la Status Bar
        final int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    decorView.setSystemUiVisibility(uiOptions);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
        ArrayList<LatLng> rutas = ruta.getCoordenadas();
        //Creamos la polilinea a mostrar en el mapa
        PolylineOptions polilyne = new PolylineOptions();
        polilyne.color(ContextCompat.getColor(getApplicationContext(), R.color.colorAlerta));
        polilyne.visible(true);
        //Movemos la cámara de maps a la posición inicial.
        CameraPosition camerapos = CameraPosition.fromLatLngZoom(rutas.get(0), 16F);
        polilyne.addAll(rutas);
        CameraUpdate cameraupd = CameraUpdateFactory.newCameraPosition(camerapos);
        googleMap.animateCamera(cameraupd);
        googleMap.addPolyline(polilyne);
        LocationManager locationMng;
    }
}
