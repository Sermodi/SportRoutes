package com.asimov.sportroutes.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
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
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Mostrará el activity de ConfirmarRuta donde se verá la ruta recorrida en un mapa de GoogleMaps
 *  y un par de botones, de confirmar o borrar la ruta realizada.
 */
public class ConfirmarRuta extends ActivityPermisos implements OnMapReadyCallback {

    private Ruta ruta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Resources res = getResources();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar_ruta);
        TextView text = (TextView) findViewById(R.id.textoRuta);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent i = this.getIntent();
        ruta = i.getParcelableExtra("ruta");
        String nombreRuta = String.format(res.getString(R.string.guardarRuta), ruta.getNombre());
        text.setText(nombreRuta);
        Button si = (Button) findViewById(R.id.si);
        si.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardado();
            }
        });
        Button no = (Button) findViewById(R.id.no);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ignorado();
            }
        });
    }


    @Override
    protected void onResume() {
        //Comprobamos si los permisos están activados (Desde API 23 los permisos se dan al ejecutar la aplicacion)
        compruebaPermisos();
        //Es necesario comprobar también si está el GPS activo.
        comprobarGPS();
        //Se ocultará la statusBar en este activity
        ocultarStatusBar();
        super.onResume();
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
        PolylineOptions po = new PolylineOptions();
        po.color(ContextCompat.getColor(getApplicationContext(),R.color.colorAlerta));
        po.visible(true);
        CameraPosition cp = CameraPosition.fromLatLngZoom(ruta.getCoordenadas().get(0), 18F);
        po.addAll(ruta.getCoordenadas());
        CameraUpdate cu = CameraUpdateFactory.newCameraPosition(cp);
        googleMap.animateCamera(cu);
        googleMap.addPolyline(po);

    }

    /**
     * Muestra un diálogo con la información indicada por el parámetro.
     * @param resource Integer, correspondiente al número de recurso en Strigns.
     */
    private void Dialogo(int resource) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(resource)
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

    /**
     * LLama al manejador de la base de datos y a Dialogo en el que se mostrará un mensaje
     *  de "Ruta guardada"
     */
    private void guardado() {
        ManejadorBD db = new ManejadorBD(this);
        db.guardarRuta(ruta);
        Dialogo(R.string.guardada);
        //db.leerBD();//DEBUG
        db.obtenerRutas();
    }

    /**
     * LLama a Dialogo indicandole que el mensaje es que "no se ha guardado la ruta"
     */
    private void Ignorado() {
        Dialogo(R.string.ignorada);
    }
}
