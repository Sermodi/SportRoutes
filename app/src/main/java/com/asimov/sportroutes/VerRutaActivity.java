package com.asimov.sportroutes;

import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
    private float distancia;
    private TextView textDistancia;

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

        //Determinamos la distancia, hasta no obtener señal GPS se indica el string "calculando..."
        textDistancia = (TextView) findViewById(R.id.textDistancia);
        textDistancia.setText(R.string.calculando);

        //Mostramos el mejor tiempo de la ruta.
        TextView textMejor = (TextView) findViewById(R.id.textMejorTiempo);
        textMejor.setText(String.valueOf(ruta.getTiempoMejor()) + " " + getString(R.string.seg));

        //Mostramos el ultimo tiempo de la ruta.
        TextView textUltimo = (TextView) findViewById(R.id.textUltimoTiempo);
        textUltimo.setText(String.valueOf(ruta.getTiempoMejor()) + " " + getString(R.string.seg));

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
        //añadimos la cámara al mapa
        googleMap.animateCamera(cameraupd);
        //añadimos la polilyne al mapa.
        googleMap.addPolyline(polilyne);

        googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                /*Es necesario saber la distancia desde la posición actual al punto,
                    para ello se necesita la primera coordenada y la posción actual.
                 */

                //Se obtiene la primera coordenada de la ruta.
                LatLng primeraCoord = ruta.getCoordenadas().get(0);
                Location locPrimera = new Location("");
                locPrimera.setLongitude(primeraCoord.longitude);
                locPrimera.setLatitude(primeraCoord.latitude);

                //Obtenemos la distancia.
                distancia = location.distanceTo(locPrimera);

                String medida;
                if (distancia > 1000){
                    distancia = distancia /1000;
                    medida = getString(R.string.km);
                }else{
                    medida = getString(R.string.m);
                }
                //Cambiamos el valor del textview correspondiente
                textDistancia.setText(String.valueOf(distancia) + " " + medida);
            }
        });


    }
}
