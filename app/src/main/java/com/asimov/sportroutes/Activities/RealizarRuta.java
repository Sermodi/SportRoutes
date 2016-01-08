package com.asimov.sportroutes.activities;
/*openweather*/

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Vibrator;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;

import com.asimov.sportroutes.General.ManejadorBD;
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
    private boolean inicioMuestra = true, fin = false;
    private CameraPosition camPos;
    private Vibrator v;
    private Location antiguaLocation;
    private boolean vibracionActiva;

    private final int METRO = 1;
    private Polyline linea;
    private Marker markSiguiente;
    private int coordInicial; //indice de la ruta que marca la siguiente coordenada por la que debe pasar el usuario
    private long tiempo;
    private ManejadorBD db;
    private boolean vibracion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realizar_ruta);

        Intent i = getIntent();
        ruta = i.getParcelableExtra("ruta");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        vibracion = prefs.getBoolean("avisoSalidaRuta", true);

        nuevaCoord = new LatLng(37.2227777778,115.814444444); //Se busca una localización donde no pueda haber ningún individuo.

        coordInicial = 0;

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

        db = new ManejadorBD(this);

        super.onResume();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        PolylineOptions polilyne = new PolylineOptions();
        polilyne.color(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        polilyne.visible(true);
        polilyne.addAll(ruta.getCoordenadas());
        polilyne.zIndex(1);
        mMap.addPolyline(polilyne);

        dibujamMap();
        
        tiempo = System.currentTimeMillis();

        // El siguiente listener controla el cambio de coordenadas */
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                antiguaLocation = new Location("");
                antiguaLocation.setLatitude(nuevaCoord.latitude);
                antiguaLocation.setLongitude(nuevaCoord.longitude);

                //Si el usuario se ha movido al menos 1 metro
                float distancia = location.distanceTo(antiguaLocation);
                if (distancia >= METRO && !fin) {
                    nuevaCoord = new LatLng(location.getLatitude(), location.getLongitude());
                    camPos = CameraPosition.fromLatLngZoom(nuevaCoord, 25F);
                /*Un marcador necesita como mínimo unas coordenadas.*/
                    if (inicioMuestra) {
                        inicioMuestra = false;
                    } else {
                        int indice = ruta.pasa_por(new LatLng(location.getLatitude(), location.getLongitude()));
                        if (indice > -1){
                            /*Hay una coordenada cercana de la ruta */
                            if (indice >= ruta.getSize() - 1){
                                tiempo = System.currentTimeMillis() - tiempo;
                                tiempo /= 1000;
                                finDeRuta();
                            }else{
                                coordInicial = indice + 1;
                                dibujamMap();
                            }
                        }else{
                            //Vibra por 500 milisegundos
                            if (vibracion) {
                                v.vibrate(500);
                            }
                        }
//                        //TODO borrar
//                        do {
//                            //obtenemos la location
//                            LatLng puntoLatLng = ruta.getCoordenadas().get(coordInicial + i);
//                            punto.setLongitude(puntoLatLng.longitude);
//                            punto.setLatitude(puntoLatLng.latitude);
//                            //obtenemos la distancia desde posicion actual a punto de ruta
//                            cercania = location.distanceTo(punto);
//                            //Si estamos a menos de 1 Metro de un punto de la ruta, se elimina ese
//                            // punto y todos los anteriores
//                            if (cercania < 3*METRO){
//                                if(coordInicial + i == ruta.getCoordenadas().size()) {
//                                    finDeRuta();
//                                }else{
//                                    for (int j = 0; j <= i; j++) {
//                                        coordInicial++;
//                                        menorDistancia = 0;
//                                        Log.d("LOGD", "Coincide, se borra el elemento");
//                                    }
//                                }
//                                dibujamMap();
//                            }else if (cercania < menorDistancia){
//                                menorDistancia = cercania;
//                                Log.d("LOGD","Está más cerca");
//                            }
//                            i++;
//                        }while((coordInicial + i)<ruta.getSize() &&     //Mientras sigan quedando coordenadas y
//                                !(cercania < METRO) &&                  // el usuario no esté en el punto y
//                                cercania < 5*KILOMETRO);                // el usuario esté a menos de 5 km
//
//                        if (menorDistancia >= 15) {
//                            //Vibra por 500 milisegundos
//                            v.vibrate(500);
//                        }
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
        polilyne.addAll(ruta.getCoordenadas(coordInicial));
        polilyne.zIndex(10);
        linea = mMap.addPolyline(polilyne);
        Log.d("LOGD","" + ruta.getSize());
    }

    private void finDeRuta(){
        final Dialog dialog = new Dialog(RealizarRuta.this);
        dialog.setContentView(R.layout.alert_fin_ruta);
        dialog.setTitle(R.string.TituloRutaEnd);

        dialog.setCanceledOnTouchOutside(true);

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                db.actualizarRuta(ruta.getNombre(),ruta.getTiempoUltimo(), (tiempo < ruta.getTiempoMejor()));
                Intent i = new Intent(getApplicationContext(), ListaDeRutasActivity.class);
                startActivity(i);
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                Intent i = new Intent(getApplicationContext(), ListaDeRutasActivity.class);
                startActivity(i);
            }
        });

        TextView text = (TextView) dialog.findViewById(R.id.text);
        text.setText(R.string.finDeRuta);

        TextView textRecord = (TextView) dialog.findViewById(R.id.textRecord);
        if (tiempo < ruta.getTiempoMejor()) {
            textRecord.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.gris));
            textRecord.setText(R.string.record);
        }else{
            textRecord.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.gris));
            textRecord.setText("");
        }
        ruta.setTiempoUltimo(tiempo);

        TextView textTiempo = (TextView) dialog.findViewById(R.id.tiempo);
        textTiempo.setText(ruta.getTiempoUltimoHumano());

        dialog.show();

        fin = true;

    }
}
