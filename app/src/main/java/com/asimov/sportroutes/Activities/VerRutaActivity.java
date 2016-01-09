package com.asimov.sportroutes.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.asimov.sportroutes.General.ManejadorBD;
import com.asimov.sportroutes.R;
import com.asimov.sportroutes.General.Ruta;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class VerRutaActivity extends ActivityPermisos implements OnMapReadyCallback {

    private Ruta ruta;
    private float distancia;
    private TextView textDistancia;

    private Button btnComenzar, btnGuiar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_ruta);

        compruebaPermisos();

        comprobarGPS();

        builder = new AlertDialog.Builder(this);

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
        String texto = ruta.getTiempoMejorHumano();
        textMejor.setText(texto);

        //Mostramos el ultimo tiempo de la ruta.
        TextView textUltimo = (TextView) findViewById(R.id.textUltimoTiempo);
        texto = ruta.getTiempoMejorHumano();
        textUltimo.setText(texto);

        //Operaciones sobre el mapa.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);

        //Boton de comenzarRuta
        btnComenzar = (Button) findViewById(R.id.btnComenzar);
        btnComenzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("LOGD", "Se ha pulsado en comenzar ruta");
            }
        });

        //Boton guiarme a la ruta
        btnGuiar = (Button) findViewById(R.id.btnGuia);
        btnGuiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("LOGD","Se ha pulsado en Guiarme");
            }
        });

        //Acción de borrar ruta, requiere confirmación
        ImageView btnBorrar = (ImageView) findViewById(R.id.btnBorrarRuta);
        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("LOGD","Se ha pulsado en BORRAR RUTA!");
                builder.setMessage(R.string.borrarRuta)
                        .setCancelable(true)
                        .setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ManejadorBD baseDatos = new ManejadorBD(getApplicationContext());
                                baseDatos.borrarRuta(ruta.getNombre());
                                onBackPressed();
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                            }
                        });
                alert = builder.create();
                alert.show();
            }
        });

    }

    /**
     * Este metodo se activa al pulsar el boton "guiame". Prepara la navegacion desde nuestra posicion
     * actual al inicio de la ruta
     */
    private void guiado(){
        builder.setMessage(R.string.gMaps)
                .setCancelable(true)
                .setPositiveButton(R.string.deAcuerdo, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Tomamos el primer punto
                        LatLng primerPunto = ruta.getCoordenadas().get(0);
                        //Generaos una Uri que indica a maps el destino y la opcion de ir en bici
                        Uri gmmIntentUri = Uri.parse("google.navigation:q="+primerPunto.latitude+","+primerPunto.longitude+"&mode=b");
                        //Generamos la intencion y le pedimos que llame a maps
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    }
                });
        alert = builder.create();
        alert.show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        //Se oculta el statusBar para dejar la pantalla más visicble.
        ocultarStatusBar();
        //Comprobamos si los permisos están activados (Desde API 23 los permisos se dan al ejecutar la aplicacion)
        compruebaPermisos();
        //Es necesario comprobar también si está el GPS activo.
        comprobarGPS();
        //Se ocultará la statusBar en este activity
        ocultarStatusBar();
        //Y a continuación activamos la pantalla para dejarla encendida cuando este activity esté activo.
        activarPantalla();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        googleMap.setMyLocationEnabled(true);

        //Creamos el marcador de inicio de ruta
        MarkerOptions mo = new MarkerOptions();
        mo.position(ruta.getCoordenadas().get(0));
        mo.title("Inicio");
        googleMap.addMarker(mo);
        //Y se añade el marcador de fin de ruta
        mo = new MarkerOptions();
        mo.position(ruta.getCoordenadas().get(ruta.getSize()-1));
        mo.title("Fin");
        googleMap.addMarker(mo);

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

                String texto;
                if (distancia > 1000){
                    texto = getString(R.string.km);
                    texto = String.valueOf(distancia/1000) + " " + texto;
                }else{
                    texto = getString(R.string.m);
                    texto = String.valueOf(distancia) + " " + texto;
                }
                //Cambiamos el valor del textview correspondiente
                textDistancia.setText(texto);

                //Los botones deben actuar de distinto modo dependiendo de la distancia al inicio
                if (distancia > 20){
                    btnComenzar.setClickable(false);
                    btnComenzar.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.comenzarTexto));
                    btnGuiar.setClickable(true);
                    btnGuiar.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.negro));
                    btnGuiar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                           guiado();
                        }
                    });

                }else{
                    btnComenzar.setClickable(true);
                    btnComenzar.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.negro));
                    btnGuiar.setClickable(false);
                    btnGuiar.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.comenzarTexto));
                    btnComenzar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(getBaseContext(),RealizarRuta.class);
                            i.putExtra("ruta", ruta);
                            startActivity(i);
                        }
                    });
                }

            }
        });


    }

}



