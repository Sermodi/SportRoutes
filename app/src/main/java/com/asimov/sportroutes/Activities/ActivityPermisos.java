package com.asimov.sportroutes.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.asimov.sportroutes.R;

/**
 * Descripción: Clase pensada para ser el padre de todas las activities que requieran permisos, en
 *  esta clase se comprobarán los permisos de internet, GPS fino y GPS "grueso" (los más usados en
 *  la aplicación) y tiene un metodo para ocultar la barra de estado.
 *
 * Autor: Sergio.
 *
 * Fecha de creación: 19/12/2015.
 */
public class ActivityPermisos extends AppCompatActivity{

    protected AlertDialog alert = null;
    protected AlertDialog.Builder builder;

    protected static final int PERMISOS_LOCALIZACION_C = 3;
    protected static final int PERMISOS_LOCALIZACION_F = 2 ;
    protected static final int PERMISOS_INTERNET = 1;
    /**
     * Comprueba los permisos necesarios para que funcione la aplicación en este punto
     */
    protected void compruebaPermisos() {

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
    protected void comprobarGPS() {
        LocationManager locManag = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //Si el GPS no está activado se lanza una alerta.
        if(!locManag.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            alertaNoGPS();
        }
    }

    /**
     * Crea un alertDialog que informa al usuario de que no hay GPS pidiendole activarlo.
     */
    protected void alertaNoGPS() {
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
     * Oculta el statusBar del dispositivo, y si este se vuelve visible por alguna razón vuelve a
     *  ocultarlo hasta que se cambie de activity.
     */
    protected void ocultarStatusBar(){
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

    /**
     * Mantiene la pantalla encendida.
     */
    protected void activarPantalla(){
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
