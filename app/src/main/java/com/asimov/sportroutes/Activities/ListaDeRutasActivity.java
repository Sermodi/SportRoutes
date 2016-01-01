package com.asimov.sportroutes.activities;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.asimov.sportroutes.General.ManejadorBD;
import com.asimov.sportroutes.R;
import com.asimov.sportroutes.General.Ruta;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ListaDeRutasActivity extends AppCompatActivity {

    private TableLayout tabla;
    private ArrayList<Ruta> rutas;
    private ListView lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_de_rutas);
        lista = (ListView) findViewById(R.id.ListView_listado);
//        tabla = (TableLayout) findViewById(R.id.tablaRutas);
    }

    @Override
    protected void onResume() {
        if(tabla != null && rutas != null){
            tabla.removeViews(2,rutas.size());}
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
        rutas = db.obtenerRutas();

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
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Ruta rPulsada = (Ruta) parent.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(),VerRutaActivity.class);
                intent.putExtra("ruta", rPulsada);
                startActivity(intent);
            }
        });

//        for (int i=0;i< rutas.size();i++){
//            TableRow fila = new TableRow(this);
//            final Ruta ruta = rutas.get(i);
//
//            TextView nombre = new TextView(this);
//            nombre.setText(ruta.getNombre());
//            addPropiedades(nombre);
//            fila.addView(nombre);
//
//            TextView ultimo = new TextView(this);
//            ultimo.setText(ruta.getTiempoUltimoHumano());
//            addPropiedades(ultimo);
//            fila.addView(ultimo);
//
//            TextView mejor = new TextView(this);
//            mejor.setText(String.valueOf(ruta.getTiempoMejorHumano()));
//            addPropiedades(mejor);
//            fila.addView(mejor);
//
//            //TODO Lo mismo para CLIMA!
//
//            //Creamos un evento OnClick para la fila actual.
//            fila.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(getBaseContext(),VerRutaActivity.class);
//                    intent.putExtra("ruta", ruta);
//                    startActivity(intent);
//                }
//            });
//            //Añadimos la fila a la tabla
//            tabla.addView(fila);
//        }
    }

//    /**
//     * Dado un TextView le añade los margenes, paddings, tamaño de texto
//     *  y color de fondo
//     * @param view TextView que se desea cambiar.
//     */
//    private void addPropiedades(TextView view) {
//        //El tamaño del texto será de 21sp
//        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21);
//
//        TextView ejemplo = (TextView) findViewById(R.id.tituloRuta);
//        //Determinamos el padding y margin copiando los de "ejemplo"
//        if (ejemplo != null) {
//            view.setLayoutParams(ejemplo.getLayoutParams());
//        }
//        //El fondo del texto será gris
//        view.setBackgroundColor(ContextCompat.getColor(
//                getApplicationContext(),
//                R.color.grisClaro));
//    }
}
