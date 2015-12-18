package com.asimov.sportroutes.Activities;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.asimov.sportroutes.General.ManejadorBD;
import com.asimov.sportroutes.R;
import com.asimov.sportroutes.General.Ruta;

import java.util.ArrayList;

public class ListaDeRutasActivity extends AppCompatActivity {

    private TableLayout tabla;
    private ArrayList<Ruta> rutas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_de_rutas);
        tabla = (TableLayout) findViewById(R.id.tablaRutas);
    }

    @Override
    protected void onResume() {
        if(tabla != null && rutas != null){
            tabla.removeViews(2,rutas.size());}
        //Se recargan las rutas en la tabla.
        rutasToTabla();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * En la tabla del layout se imprimen todas las rutas del sistema.
     */
    private void rutasToTabla(){
        //Cargamos base de datos.
        ManejadorBD db = new ManejadorBD(getApplicationContext());
        //Obtenemos todas las rutas de la base de datos
        rutas = db.obtenerRutas();

        //Para cada ruta creamos una nueva row con la info de la ruta
        // |Nombre | Ultimo Tiempo | Mejor Tiempo | Clima| <---cada fila
        for (int i=0;i< rutas.size();i++){
            TableRow fila = new TableRow(this);
            final Ruta ruta = rutas.get(i);

            TextView nombre = new TextView(this);
            nombre.setText(ruta.getNombre());
            addPropiedades(nombre);
            fila.addView(nombre);

            TextView ultimo = new TextView(this);
            ultimo.setText(String.valueOf(ruta.getTiempoUltimo()));
            addPropiedades(ultimo);
            fila.addView(ultimo);

            TextView mejor = new TextView(this);
            mejor.setText(String.valueOf(ruta.getTiempoMejor()));
            addPropiedades(mejor);
            fila.addView(mejor);

            //TODO Lo mismo para CLIMA!

            //Creamos un evento OnClick para la fila actual.
            fila.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getBaseContext(),VerRutaActivity.class);
                    intent.putExtra("ruta", ruta);
                    startActivity(intent);
                }
            });
            //Añadimos la fila a la tabla
            tabla.addView(fila);
        }
    }

    /**
     * Dado un TextView le añade los margenes, paddings, tamaño de texto
     *  y color de fondo
     * @param view TextView que se desea cambiar.
     */
    private void addPropiedades(TextView view) {
        //El tamaño del texto será de 21sp
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21);

        TextView ejemplo = (TextView) findViewById(R.id.tituloRuta);
        //Determinamos el padding y margin copiando los de "ejemplo"
        if (ejemplo != null) {
            view.setLayoutParams(ejemplo.getLayoutParams());
        }
        //El fondo del texto será gris
        view.setBackgroundColor(ContextCompat.getColor(
                getApplicationContext(),
                R.color.grisClaro));
    }
}
