package com.asimov.sportroutes.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private boolean tomado = false;
    private boolean grabando = false;
    private Button boton;
    private Ruta ruta;
    private PolylineOptions polyline;
    private final int PERMISOS_INTERNET = 1;
    private final int PERMISOS_LOCALIZACION_F = 2;
    private final int PERMISOS_LOCALIZACION_C = 3;
    private static final int PERMISOS_PANTALLA = 4;
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
        //Comprobamos si los permisos están activados (Desde API 23 los permisos se dan al ejecutar la aplicacion)
        compruebaPermisos();
        //Es necesario comprobar también si está el GPS activo.
        comprobarGPS();
        //Se ocultará la statusBar en este activity
        ocultarStatusBar();
        //Y a continuación activamos la pantalla para dejarla encendida cuando este activity esté activo.
        activarPantalla();

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
                    if (!ruta.compruebaNombre(getBaseContext())){
                        builder.setMessage(R.string.vacio)
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
        }
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
            //toast =  new Toast(getApplicationContext());
            //toast.setText(R.string.ignorada);
            //toast.setDuration(Toast.LENGTH_LONG);
            // toast.show();
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
                    if (distancia(anteriorCoord, nuevaCoord) > 0.0001) {
                        anteriorCoord = nuevaCoord;
                        //Datos de inicialización, son diferentes.
                        CameraPosition camPos;
                        if (!grabando) {
                            camPos = CameraPosition.fromLatLngZoom(anteriorCoord, 17F);
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
            case PERMISOS_PANTALLA:
                // Si los permisos no están concedidos
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, R.string.PermisosPantallaDenegados, Toast.LENGTH_LONG).show();
                    volver();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
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
     * Cierra el activity actual para volver al activity anterior
     *  Funcionamiento semejante al boton "back"
     */
    private void volver(){
        SystemClock.sleep(2000);
        this.finish();//cierra el activity como si se pulsara el botón volver del dispositivo.
    }

    /**
     * Cálculo de la distancia euclidea entre desde el "origen" hasta el "destino"
     * @param origen ,punto desde el que se calculará la distancia.
     * @param destino ,punto hacia el que se calculará la distancia.
     * @return la distancia euclidea desde origen a destino, esta es: SQRT(destino.x - origen.x)^2 + (destino.y - origen.y)^2)
     */
    private double distancia(LatLng origen, LatLng destino){
        return Math.sqrt(
                Math.pow( (destino.latitude - origen.latitude) , 2 ) +
                        Math.pow( (destino.longitude - origen.longitude ) , 2 )
        );
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
        //Se comprueban los permisos de mantener pantalla encendida.
        controlPermisos = ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK);
        if (controlPermisos != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WAKE_LOCK},
                    PERMISOS_PANTALLA);
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

    private void activarPantalla(){
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
        Log.d("TIEMPO",""+ruta.getTiempoUltimo());
        Log.d("TIEMPO", "" + ruta.getTiempoInicio());
        ruta.calculaDuracion();
        Log.d("TIEMPO", "ULTIMO: "+ruta.getTiempoUltimo()+ "MEJOR"+ ruta.getTiempoMejor());
        long[] tiempo = ruta.tiempoUltimoToArray();
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(String.format(getResources().getString(R.string.tuTiempoes), getTiempo(tiempo)))
                .setCancelable(false)
                .setPositiveButton(R.string.continuar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        ruta.imprimeRuta();//DEBUG
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

    /**
     * Oculta el statusBar del dispositivo, y si este se vuelve visible por alguna razón vuelve a
     *  ocultarlo hasta que se cambie de activity.
     */
    private void ocultarStatusBar(){
        final View decorView = getWindow().getDecorView();
        // ulta la Status Bar
        final int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0){
                    decorView.setSystemUiVisibility(uiOptions);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menuprincipal, menu);
        return true;
    }


}
