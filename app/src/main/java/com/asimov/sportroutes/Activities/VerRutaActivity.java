package com.asimov.sportroutes.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.PolylineOptions;

import java.net.URI;
import java.util.ArrayList;

public class VerRutaActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Ruta ruta;
    private float distancia;
    private TextView textDistancia;
    private VerRutaActivity verRuta = this;

    private Button btnComenzar, btnGuiar;

    private  AlertDialog alert = null;
    private  AlertDialog.Builder builder;

    private static final int PERMISOS_LOCALIZACION_C = 3;
    private static final int PERMISOS_LOCALIZACION_F = 2 ;
    private static final int PERMISOS_INTERNET = 1;

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
        String texto = String.valueOf(ruta.getTiempoMejor()) + " " + getString(R.string.seg);
        textMejor.setText(texto);

        //Mostramos el ultimo tiempo de la ruta.
        TextView textUltimo = (TextView) findViewById(R.id.textUltimoTiempo);
        texto = String.valueOf(ruta.getTiempoMejor()) + " " + getString(R.string.seg);
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
    public void guiado (){
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
                    btnComenzar.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.common_google_signin_btn_text_dark_default));
                    btnGuiar.setClickable(true);
                    btnGuiar.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.common_google_signin_btn_text_light_default));
                    btnGuiar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                           guiado();
                        }
                    });

                }else{
                    btnComenzar.setClickable(true);
                    btnComenzar.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.common_plus_signin_btn_text_light_default));
                    btnGuiar.setClickable(false);
                    btnGuiar.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.common_google_signin_btn_text_dark_default));
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

    /**
     * Comprueba los permisos necesarios para que funcione la aplicación en este punto
     */
    private void compruebaPermisos() {

        int controlPermisos ;
        //Primero se comprueba el permiso de acceso a INTERNET
        controlPermisos = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        if (controlPermisos != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    PERMISOS_INTERNET);
        }
        //Se comprueban los permisos de Localización Fina.
        controlPermisos = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (controlPermisos != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISOS_LOCALIZACION_F);
        }

        //Se comprueban los permisos de Localización Gruesa.
        controlPermisos = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (controlPermisos != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISOS_LOCALIZACION_C);
        }
        // ^--El resultado de "requestPermissions" se comprobará en el método "onRequestPermissionsResult"

    }

    /**
     * Comprueba la disponibilidad del sistema GPS en el dispositivo, si no está disponible se
     *  ejecuta una alerta (alertaNoGPS).
     */
    private void comprobarGPS() {
        LocationManager locManag = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //Si el GPS no está activado se lanza una alerta.
        if(!locManag.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            alertaNoGPS();
        }
    }

    /**
     * Crea un alertDialog que informa al usuario de que no hay GPS pidiendole activarlo.
     */
    private void alertaNoGPS() {
        builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.alertaGpsDesactivado)
                .setCancelable(false)
                .setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alert = builder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISOS_INTERNET:
                // Si los permisos no están concedidos
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, R.string.PermisosInternetDENEGADOS, Toast.LENGTH_LONG).show();
                    this.finish();
                }
                break;
            case PERMISOS_LOCALIZACION_F:
                // Si los permisos no están concedidos
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, R.string.PermisosGpsDENEGADOS, Toast.LENGTH_LONG).show();
                    volver();
                }
                break;
            case PERMISOS_LOCALIZACION_C:
                // Si los permisos no están concedidos
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, R.string.PermisosGpsDENEGADOS, Toast.LENGTH_LONG).show();
                    volver();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * Cierra el activity actual para volver al activity anterior
     *  Funcionamiento semejante al boton "back"
     */
    private void volver(){
        SystemClock.sleep(2000);
        this.finish();//cierra el activity como si se pulsara el botón volver del dispositivo.
    }



}
