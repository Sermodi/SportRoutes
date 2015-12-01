package com.asimov.sportroutes;

import android.app.ActionBar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

public class ListaDeRutasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_de_rutas);
        TableLayout tabla = (TableLayout) findViewById(R.id.tablaRutas);
        //Cargamos base de datos.
        ManejadorBD db = new ManejadorBD(getApplicationContext());
        //Obtenemos todas las rutas de la base de datos
        ArrayList<Ruta> rutas = db.obtenerRutas();

        //Para cada ruta creamos una nueva row con la info de la ruta
        // |Nombre | Ultimo Tiempo | Mejor Tiempo | Clima| <---cada fila
        for (int i=0;i<rutas.size();i++){
            TableRow fila = new TableRow(this);
            Ruta ruta = rutas.get(i);

            TextView nombre = new TextView(this);
            nombre.setText(ruta.getNombre());
            fila.addView(nombre);

            TextView ultimo = new TextView(this);
            ultimo.setText("" + ruta.getTiempoUltimo());
            fila.addView(ultimo);

            TextView mejor = new TextView(this);
            mejor.setText("" + ruta.getTiempoMejor());
            fila.addView(mejor);

            //TODO Lo mismo para CLIMA!

            tabla.addView(fila);
        }


    }
}
