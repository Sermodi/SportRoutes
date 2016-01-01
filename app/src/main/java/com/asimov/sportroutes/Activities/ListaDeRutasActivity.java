package com.asimov.sportroutes.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.asimov.sportroutes.General.ManejadorBD;
import com.asimov.sportroutes.R;
import com.asimov.sportroutes.General.Ruta;

import java.util.ArrayList;

public class ListaDeRutasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_de_rutas);
    }

    @Override
    protected void onResume() {
        //Se recargan las rutas en la tabla.
        rutasToTabla();
        super.onResume();
    }

    /**
     * En la tabla del layout se imprimen todas las rutas del sistema.
     */
    private void rutasToTabla(){
        //Cargamos base de datos.
        ManejadorBD db = new ManejadorBD(getApplicationContext());
        //Obtenemos todas las rutas de la base de datos
        ArrayList<Ruta> rutas = db.obtenerRutas();

        ListView lista = (ListView) findViewById(R.id.ListView_listado);
        //Cada ruta estará almacenada en una listview que mostrará la información de la misma.
        lista.setAdapter(new com.asimov.sportroutes.General.ListAdapter(this, R.layout.info_ruta, rutas) {
            @Override
            public void onEntrada(Object entrada, View view) {
                if (entrada != null) {
                    TextView nombre = (TextView) view.findViewById(R.id.textNombre);
                    if (nombre != null)
                        nombre.setText(((Ruta) entrada).getNombre());

                    TextView mejor = (TextView) view.findViewById(R.id.textMejor);
                    if (mejor != null)
                        mejor.setText(((Ruta) entrada).getTiempoMejorHumano());

                    TextView ultimo = (TextView) view.findViewById(R.id.textUltimo);
                    if (ultimo != null)
                        ultimo.setText(((Ruta) entrada).getTiempoUltimoHumano());

                    //TODO lo mismo para el clima
                }
            }
        });
        //Indicamos lo que le pasa al elemento al ser clicado
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Ruta rPulsada = (Ruta) parent.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(),VerRutaActivity.class);
                intent.putExtra("ruta", rPulsada);
                startActivity(intent);
            }
        });
    }
}
