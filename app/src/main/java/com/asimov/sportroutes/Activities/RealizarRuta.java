package com.asimov.sportroutes.activities;
/*openweather*/

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Vibrator;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class RealizarRuta extends ActivityPermisos implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Ruta ruta;
    private LatLng nuevaCoord;
    private boolean inicioMuestra = true;
    private CameraPosition camPos;
    private Vibrator v;
    private Location antiguaLocation;

    private final int METRO = 1;
    private final int KILOMETRO = 1000;
    private Polyline linea;
    private Marker markSiguiente;
    private int coordInicial; //indice de la ruta que marca la siguiente coordenada por la que debe pasar el usuario

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realizar_ruta);

        Intent i = getIntent();
        ruta = i.getParcelableExtra("ruta");

        nuevaCoord = new LatLng(37.2227777778,115.814444444); //Se busca una localización donde no pueda haber ningún individuo.

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        //Comprobamos si los permisos están activados (Desde API 23 los permisos se dan al ejecutar la aplicacion)
        compruebaPermisos();
        //Es necesario comprobar también si está el GPS activo.
        comprobarGPS();
        //Se ocultará la statusBar en este activity
//        ocultarStatusBar();
        //Y a continuación activamos la pantalla para dejarla encendida cuando este activity esté activo.
        activarPantalla();

        super.onResume();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        PolylineOptions polilyne = new PolylineOptions();
        polilyne.color(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        polilyne.visible(true);
        polilyne.addAll(ruta.getCoordenadas());
        linea = mMap.addPolyline(polilyne);

        dibujamMap();

        // El siguiente listener controla el cambio de coordenadas */
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                antiguaLocation = new Location("");
                antiguaLocation.setLatitude(nuevaCoord.latitude);
                antiguaLocation.setLongitude(nuevaCoord.longitude);

                //Si el usuario se ha movido al menos 1 metro
                float distancia = location.distanceTo(antiguaLocation);
                if (distancia >= METRO) {
                    nuevaCoord = new LatLng(location.getLatitude(), location.getLongitude());
                    camPos = CameraPosition.fromLatLngZoom(nuevaCoord, 25F);
                /*Un marcador necesita como mínimo unas coordenadas.*/
                    if (inicioMuestra) {
                        inicioMuestra = false;
                    } else {
                        float cercania;
                        float menorDistancia = KILOMETRO;//Distancia al punto más cercano
                        int i = coordInicial;
                        Location punto = new Location("punto");
                        do {
                            //obtenemos la location
                            LatLng puntoLatLng = ruta.getCoordenadas().get(coordInicial + i);
                            punto.setLongitude(puntoLatLng.longitude);
                            punto.setLatitude(puntoLatLng.latitude);
                            //obtenemos la distancia desde posicion actual a punto de ruta
                            cercania = location.distanceTo(punto);
                            //Si estamos a menos de 1 Metro de un punto de la ruta, se elimina ese
                            // punto y todos los anteriores
                            if (cercania < 3*METRO){
                                if(coordInicial + i == ruta.getCoordenadas().size()) {
                                    finDeRuta();
                                }else{
                                    for (int j = 0; j <= i; j++) {
                                        coordInicial++;
                                        menorDistancia = 0;
                                        Log.d("LOGD", "Coincide, se borra el elemento");
                                    }
                                }
                                dibujamMap();
                            }else if (cercania < menorDistancia){
                                menorDistancia = cercania;
                                Log.d("LOGD","Está más cerca");
                            }
                            i++;
                        }while((coordInicial + i)<ruta.getSize() &&     //Mientras sigan quedando coordenadas y
                                !(cercania < METRO) &&                  // el usuario no esté en el punto y
                                cercania < 5*KILOMETRO);                // el usuario esté a menos de 5 km

                        if (menorDistancia >= 15) {
                            //Vibra por 500 milisegundos
                            v.vibrate(500);
                        }
                    }
                    CameraUpdate cu = CameraUpdateFactory.newCameraPosition(camPos);
                    mMap.animateCamera(cu);
                }
            }
        });
    }

    private void dibujamMap(){
        //Borramos la linea anterior, si existe
        if (linea != null) {
            linea.remove();
            markSiguiente.remove();
            Log.d("LOGD", "Se borra la linea");
        }
        /* Se activa el botón de google maps que centra el mapa en la posición actual.*/
        mMap.setMyLocationEnabled(true);
        //Creamos el marcador de inicio de ruta
        MarkerOptions mo = new MarkerOptions();
        mo.position(ruta.getCoordenadas().get(coordInicial));
        mo.title("Siguiente");
        markSiguiente = mMap.addMarker(mo); //Se almacena porque lo iremos borrando
        //Y se añade el marcador de fin de ruta
        mo = new MarkerOptions();
        mo.position(ruta.getCoordenadas().get(ruta.getSize()-1));
        mo.title("Fin");
        mMap.addMarker(mo);//Es el último, no necesita borrarse.

        PolylineOptions polilyne = new PolylineOptions();
        polilyne.color(ContextCompat.getColor(getApplicationContext(), R.color.colorAlerta));
        polilyne.visible(true);
        polilyne.addAll(ruta.getCoordenadas());
        linea = mMap.addPolyline(polilyne);
        Log.d("LOGD","" + ruta.getSize());
    }

    private void finDeRuta(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.finDeRuta)
                .setCancelable(false)
                .setPositiveButton(R.string.continuar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getBaseContext(), MainActivity.class));
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }
}
