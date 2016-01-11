package com.asimov.sportroutes.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class ListaDeRutasActivity extends ActivityPermisos {

    private boolean mostrarTiempo;
    private boolean mostrarTemperatura;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_de_rutas);

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);

        //Iniciamos la toolbar con el boton de arriba para abrir el menu.
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        linearLayout = (LinearLayout) findViewById(R.id.Linear1);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ListView listView = (ListView) findViewById(R.id.list_view);
        String[] opciones = new String[]{getString(R.string.accion1), getString(R.string.accion2),
                getString(R.string.accion3), getString(R.string.accion4), getString(R.string.acercaDe)};

        listView.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1,
                opciones));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                switch (arg2) {
                    case 0:
                        startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(getApplicationContext(), ListaDeRutasActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(getApplicationContext(), OpcionesActivity.class));
                        break;
                    case 3:
                        //Manual
                        startActivity(new Intent(getApplicationContext(), HowToActivity.class));
                        break;
                    case 4:
                        startActivity(new Intent(getApplicationContext(), AboutUs.class));
                        break;

                }
                drawerLayout.closeDrawers();


            }
        });
    }

    @Override
    protected void onResume() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mostrarTiempo = prefs.getBoolean("mostrarTiempo", true);
        mostrarTemperatura = prefs.getBoolean("mostrarTemperatura", true);

        ocultarStatusBar();

        //Se recargan las rutas en la tabla.
        rutasToTabla();
        super.onResume();
    }

    /** Implementación del listener del boton fisico del menu de algunos moviles */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (drawerLayout.isDrawerOpen(linearLayout)) {
                drawerLayout.closeDrawers();
            } else {
                drawerLayout.openDrawer(linearLayout);
            }
            return true;
        }else if (keyCode == KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        }
        return super.onKeyUp(keyCode, event);
    }

    /**
     * mostramos/ocultamos el menu al presionar el icono de la aplicacion
     * ubicado en la barra
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (drawerLayout.isDrawerOpen(linearLayout)) {
                    drawerLayout.closeDrawers();
                } else {
                    drawerLayout.openDrawer(linearLayout);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
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
