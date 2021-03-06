package com.asimov.sportroutes.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

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

public class MapsActivity extends ActivityPermisos implements OnMapReadyCallback {
    private GoogleMap mMap;
    private boolean tomado = false;
    private boolean grabando = false;
    private Button boton;
    private Ruta ruta;
    private PolylineOptions polyline;
    private LatLng nuevaCoord, anteriorCoord = new LatLng(0,0); //Coordenadas, necesario inicializar la variable
    private AlertDialog alert = null;
    private EditText nombre;
    private AlertDialog.Builder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        nombre = (EditText) findViewById(R.id.nombreRuta);
        polyline = new PolylineOptions();
        polyline.color(ContextCompat.getColor(getApplicationContext(),R.color.colorAlerta));
        polyline.visible(true);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        //Contenido autogenerado por AndroidStudio
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        boton = (Button) findViewById(R.id.button4);
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!grabando) {
                    ruta = new Ruta(nombre.getText().toString());
                    int valido = ruta.nombreValido(getBaseContext());
                    if (valido < 1){
                        String mensaje;
                        if (valido == 0){
                            mensaje = getString(R.string.vacio);
                        }else{
                            mensaje = getString(R.string.existe);
                        }
                        builder.setMessage(mensaje)
                                .setCancelable(false)
                                .setPositiveButton(R.string.continuar, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                        alert = builder.create();
                        alert.show();

                    }else {
                        boton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.colorAlerta));
                        boton.setText(getResources().getString(R.string.parar));
                        nombre.setVisibility(View.INVISIBLE);
                        ruta.setTiempoInicio(System.currentTimeMillis());
                        ruta.addCoordenada(nuevaCoord);//Es la primera coordenada que necesita ruta.
                        polyline.add(nuevaCoord);
                        tomado = false;
                        grabando = true;
                    }
                } else {
                    ruta.setTiempoUltimo(System.currentTimeMillis());
                    if (ruta.getSize() < 10) {//Una ruta con menos de 10 puntos es una ruta muy corta, conviene informar al usuario.
                        if (ruta.getSize() > 2)
                            alertaRutaPequena();
                        else
                            alertaRutaNull();
                    } else {
                        goToConfirmarRuta();
                    }
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(grabando){
            //Si se está grabando ruta el color del boton debe tener una estética distinta.
            boton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.colorAlerta));
            boton.setText(getResources().getString(R.string.parar));
            nombre.setVisibility(View.INVISIBLE);
        }
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
    protected void onDestroy() {
        super.onDestroy();
        if (alert != null){
            alert.dismiss();
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //SI pulsamos atras durante la generacion de una ruta,  al menú principal
        if(keyCode == KeyEvent.KEYCODE_BREAK){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
        * Este método se ejecuta solo cuando el mapa está listo para usar
        * Si los servicios de google no están instalados se muestra un layout p
        * */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        /* Se activa el botón de google maps que centra el mapa en la posición actual.*/
        mMap.setMyLocationEnabled(true);
        Log.d("LocDebug", "Mapa Listo");
        // El siguiente listener controla el cambio de coordenadas */
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                /*Un marcador necesita como mínimo unas coordenadas.*/
                nuevaCoord = new LatLng(location.getLatitude(), location.getLongitude());
                if (!tomado) {
                    //DEBUG Log.d("LocDebug", "Posicion: " + location.getLatitude() + " , " + location.getLongitude());


                    //DEBUG Log.d("LocDebug", "Distancia es de --> " + distancia(anteriorCoord, nuevaCoord));
                    //Una coordenada se considerará válida si como mínimo se ha movido una distancia de 0.0001 grados.
                    if (Ruta.calculaDistancia(anteriorCoord, nuevaCoord) > 0.0001) {
                        anteriorCoord = nuevaCoord;
                        //Datos de inicialización, son diferentes.
                        CameraPosition camPos;
                        if (!grabando) {
                            camPos = CameraPosition.fromLatLngZoom(anteriorCoord, 16F);
                            tomado = true;
                        } else {
                            camPos = CameraPosition.fromLatLngZoom(anteriorCoord, mMap.getCameraPosition().zoom);
                            ruta.addCoordenada(anteriorCoord);
                            polyline.add(anteriorCoord);
                            mMap.addPolyline(polyline);
                        }
                        CameraUpdate cu = CameraUpdateFactory.newCameraPosition(camPos);
                        mMap.animateCamera(cu);
                    }
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Guardamos Ruta para mantener los datos.
        //Si ruta no existe no podemos guardarla
        if (ruta != null) {
            outState.putBoolean("hayRuta",true);
            outState.putParcelable("ruta",ruta);
        }else{
            outState.putBoolean("hayRuta",false);
        }
        outState.putBoolean("tomado", tomado);
        outState.putBoolean("grabando", grabando);
    }

    @Override
    protected void onRestoreInstanceState(Bundle inState){
        super.onRestoreInstanceState(inState);
        if (inState.getBoolean("hayRuta")){
            ruta = inState.getParcelable("ruta");
            assert ruta != null; //En el if ya se comprueba si ruta no es null, pero Lint no lo sabe..
            polyline.addAll(ruta.getCoordenadas());
            Log.d("LOGD", "Hay Ruta " + ruta.getSize() + "puntos    " + polyline.getPoints());
        }else{
            Log.d("LOGD", "NO hay ruta!");
        }
        tomado = inState.getBoolean("tomado");
        grabando = inState.getBoolean("grabando");
    }


    /**
     * Crea un alertDialog informando al usuario de que su ruta es muy pequeña (menos de
     *  10 puntos y más de 2) y preguntandole por como actuar.
     */
    private void alertaRutaPequena() {
        builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.alertaRutaPequena)
                .setCancelable(false)
                .setPositiveButton(R.string.continuar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goToConfirmarRuta();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("LOGDebug", "Se quiere continuar guardando rutas, tal vez un boton pulsado sin querer");
                    }
                });
        alert = builder.create();
        alert.show();
    }

    /**
     * Crea un alertDialog que informa al usuario de que la ruta no es más grande de 2 puntos.
     */
    private void alertaRutaNull() {
        builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.alertaRutaNull)
                .setCancelable(true)
                .setPositiveButton(R.string.continuar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        alert = builder.create();
        alert.show();
    }
    /**
     * Accede a la activity confirmaRuta realizando las acciones pertinentes.
     */
    private void goToConfirmarRuta(){
        tomado = true;
        ruta.calculaDuracion();
        Log.d("TIEMPO", "ULTIMO: "+ruta.getTiempoUltimo()+ "MEJOR"+ ruta.getTiempoMejor());
        long[] tiempo = ruta.tiempoUltimoToArray();
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(String.format(getResources().getString(R.string.tuTiempoes), getTiempo(tiempo)))
                .setCancelable(false)
                .setPositiveButton(R.string.continuar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(getBaseContext(), ConfirmarRuta.class);
                        i.putExtra("ruta", ruta);
                        startActivity(i);
                    }
                });
        alert = builder.create();
        alert.show();
    }

    /**
     * Imprime el tiempo en horas minutos y segundos en un formato en condiciones.
     * @param tiempo long[], tiempo en milisegundos.
     * @return String, tiempo en horas, minutos y segundos.
     */
    private String getTiempo(long[] tiempo) {
        String tiempos;
        tiempos = getResources().getQuantityString(R.plurals.horas, (int)tiempo[0], (int)tiempo[0]);
        tiempos = tiempos + " "+ getResources().getQuantityString(R.plurals.minutos, (int)tiempo[1],(int)tiempo[1]);
        tiempos = tiempos+" "+ getResources().getQuantityString(R.plurals.segundos,  (int)tiempo[2],(int)tiempo[2]);
        return tiempos;

    }

}
