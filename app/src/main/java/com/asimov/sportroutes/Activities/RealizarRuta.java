package com.asimov.sportroutes.Activities;
/*openweather*/

import android.content.Intent;
import android.location.Location;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.asimov.sportroutes.General.Ruta;
import com.asimov.sportroutes.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class RealizarRuta extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Ruta ruta;
    private LatLng nuevaCoord;
    private boolean inicioMuestra = true;
    private CameraPosition camPos;
    private Vibrator v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realizar_ruta);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        Intent i = getIntent();
        ruta = i.getParcelableExtra("ruta");
        v = (Vibrator) getSystemService(getApplicationContext().VIBRATOR_SERVICE);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        /* Se activa el botón de google maps que centra el mapa en la posición actual.*/
        mMap.setMyLocationEnabled(true);
        MarkerOptions mo = new MarkerOptions();
        mo.position(ruta.getCoordenadas().get(0));
        mMap.addMarker(mo);
        PolylineOptions polilyne = new PolylineOptions();
        polilyne.color(ContextCompat.getColor(getApplicationContext(), R.color.colorAlerta));
        polilyne.visible(true);
        polilyne.addAll(ruta.getCoordenadas());
        googleMap.addPolyline(polilyne);
        // El siguiente listener controla el cambio de coordenadas */
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                nuevaCoord = new LatLng(location.getLatitude(), location.getLongitude());
                /*Un marcador necesita como mínimo unas coordenadas.*/
                if(inicioMuestra){
                    camPos = CameraPosition.fromLatLngZoom(nuevaCoord, 25F);
                    inicioMuestra = false;
                }
                else{
                    camPos = CameraPosition.fromLatLngZoom(nuevaCoord, mMap.getCameraPosition().zoom);
                    if(ruta.alejado(nuevaCoord)){
                        // Get instance of Vibrator from current Context


                        // Vibrate for 400 milliseconds
                        v.vibrate(500);
                    }
                }
                CameraUpdate cu = CameraUpdateFactory.newCameraPosition(camPos);
                mMap.animateCamera(cu);
            }
        });
    }
}
