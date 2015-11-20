package com.asimov.sportroutes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
public class ConfirmarRuta extends AppCompatActivity implements OnMapReadyCallback {

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
                Guardado();
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
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
        PolylineOptions po = new PolylineOptions();
        po.color(getResources().getColor(R.color.colorAlerta));
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
    private void Guardado() {
        ManejadorBD db = new ManejadorBD(this);
        db.guardarRuta(ruta);
        Dialogo(R.string.guardada);
        db.leerBD();//DEBUG
        //db.obtenerRutas();
    }

    /**
     * LLama a Dialogo indicandole que el mensaje es que "no se ha guardado la ruta"
     */
    private void Ignorado() {
        Dialogo(R.string.ignorada);
    }
}
