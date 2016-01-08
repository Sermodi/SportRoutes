package com.asimov.sportroutes.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.asimov.sportroutes.General.Clima.Clima;
import com.asimov.sportroutes.General.Clima.ClimaClienteHttp;
import com.asimov.sportroutes.General.Clima.jsonClimaParser;
import com.asimov.sportroutes.General.ManejadorBD;
import com.asimov.sportroutes.R;
import com.asimov.sportroutes.General.Ruta;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Map;

public class ListaDeRutasActivity extends AppCompatActivity {

    boolean mostrarTiempo, mostrarTemperatura;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_de_rutas);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mostrarTiempo = prefs.getBoolean("mostrarTiempo", true);
        mostrarTemperatura = prefs.getBoolean("mostrarTemperatura", true);
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
                    //Mostramos el nombre
                    TextView nombre = (TextView) view.findViewById(R.id.textNombre);
                    if (nombre != null)
                        nombre.setText(((Ruta) entrada).getNombre());
                    //Mostramos el mejor tiempo.
                    TextView mejor = (TextView) view.findViewById(R.id.textMejor);
                    if (mejor != null)
                        mejor.setText(((Ruta) entrada).getTiempoMejorHumano());
                    //Mostramos el ultimo tiempo.
                    TextView ultimo = (TextView) view.findViewById(R.id.textUltimo);
                    if (ultimo != null)
                        ultimo.setText(((Ruta) entrada).getTiempoUltimoHumano());
                    final TextView ciudad =  (TextView) view.findViewById(R.id.textViewCiudad);
                    final ImageView img =  (ImageView) view.findViewById(R.id.imageViewClima);
                    final TextView temp = (TextView) view.findViewById(R.id.textViewTemperatura);
                    if (!mostrarTiempo){
                        img.setVisibility(View.INVISIBLE);
                    }
                    if(!mostrarTemperatura){
                        temp.setVisibility(View.INVISIBLE);
                    }

                    //lo mismo para el clima
                    LatLng coordenada = ((Ruta) entrada).getCoordenadas().get(0);
                    class ClimaAsincrono extends AsyncTask<String, Void, Clima> {

                        @Override
                        protected Clima doInBackground(String... params) {
                            Clima clima = new Clima();
                            String data = ((new ClimaClienteHttp()).getClima(params[0], params[1]));

                            try {
                                clima = jsonClimaParser.getWeather(data);

                                clima.iconDrawable = ((new ClimaClienteHttp()).getImagen(clima.currentCondition.getIcon()));
                                Log.d("LOGD", clima.currentCondition.getIcon() );

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            return clima;

                        }

                        @Override
                        protected void onPostExecute(Clima clima) {
                            super.onPostExecute(clima);
                            img.setImageDrawable(clima.iconDrawable);
                            temp.setText(String.valueOf(clima.temperature.getTemp() + " ºC"));
                            String textAux =clima.localizacion.getCiudad() + ", " + clima.localizacion.getPais();
                            ciudad.setText(textAux);
                        }
                    }
                    ClimaAsincrono peticion = new ClimaAsincrono();
                    peticion.execute(String.valueOf(coordenada.latitude),
                            String.valueOf(coordenada.longitude));
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
