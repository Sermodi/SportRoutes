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

public class ConfirmarRuta extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Ruta ruta;
    private TextView text;
    private Button si;
    private Button no;
    private AlertDialog alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Resources res = getResources();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar_ruta);
        text = (TextView) findViewById(R.id.textoRuta);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent i = this.getIntent();
        ruta = i.getParcelableExtra("ruta");
        String nombreRuta = String.format(res.getString(R.string.guardarRuta), ruta.getNombre());
        text.setText(nombreRuta);
        si = (Button) findViewById(R.id.si);
        si.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Guardado();
            }
        });
        no = (Button) findViewById(R.id.no);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ignorado();
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        PolylineOptions po = new PolylineOptions();
        po.color(getResources().getColor(R.color.colorAlerta));
        po.visible(true);
        CameraPosition cp = CameraPosition.fromLatLngZoom(ruta.getCoordenadas().get(0), 18F);
        po.addAll(ruta.getCoordenadas());
        CameraUpdate cu = CameraUpdateFactory.newCameraPosition(cp);
        mMap.animateCamera(cu);
        mMap.addPolyline(po);

    }

    private void Dialogo(int i) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(i)
                .setCancelable(false)
                .setPositiveButton(R.string.continuar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getBaseContext(), MainActivity.class));
                    }
                });
        alert = builder.create();
        alert.show();

    }

    private void Guardado() {
        ManejadorBD db = new ManejadorBD(this);
        db.guardarRuta(ruta);
        Dialogo(R.string.guardada);
        //db.leerBD();
        //db.obtenerRutas();
    }

    private void Ignorado() {
        Dialogo(R.string.ignorada);
    }
}
