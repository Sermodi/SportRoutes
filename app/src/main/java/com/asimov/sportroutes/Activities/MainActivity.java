package com.asimov.sportroutes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.asimov.sportroutes.R;

public class MainActivity extends ActivityPermisos {

    private DrawerLayout drawerLayout;
    private String[] opciones;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar

        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);

        //Iniciamos la toolbar con el boton de arriba para abrir el menu.
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        linearLayout = (LinearLayout) findViewById(R.id.Linear1);
        ListView listView = (ListView) findViewById(R.id.list_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        opciones = new String[]{getString(R.string.accion1), getString(R.string.accion2),
                getString(R.string.accion3), getString(R.string.accion4), getString(R.string.acercaDe)};

        listView.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1,
                opciones));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Toast.makeText(MainActivity.this, "Action: " + opciones[arg2],
                        Toast.LENGTH_SHORT).show();
                switch (arg2) {
                    case 0:
                        startActivity(new Intent(MainActivity.this, MapsActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(MainActivity.this, ListaDeRutasActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(MainActivity.this, OpcionesActivity.class));
                        break;
                    case 3:
                        //Manual
                        startActivity(new Intent(MainActivity.this, HowToActivity.class));
                        break;
                    case 4:
                        startActivity(new Intent(MainActivity.this, AboutUs.class));
                        break;

                }
                //asdfasd

                drawerLayout.closeDrawers();
            }
        });

    }

    @Override
    protected void onResume() {
        ocultarStatusBar();
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
            finish();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (drawerLayout.isDrawerOpen(linearLayout)) {
            drawerLayout.closeDrawers();
        } else {
            drawerLayout.openDrawer(linearLayout);
        }
        return true;
    }


    /*
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        opciones = new String[]{getString(R.string.accion1), getString(R.string.accion2),
                getString(R.string.accion3), getString(R.string.accion4), getString(R.string.acercaDe)};

        sifl = (ScrimInsetsFrameLayout) findViewById(R.id.scrimInsetsFrameLayout);

        //Toolbar

        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);



        //Menu del Navigation Drawer

        ndList = (ListView) findViewById(R.id.navdrawerlist);


        ArrayAdapter<String> ndMenuAdapter =
                new ArrayAdapter<>(this,android.R.layout.simple_list_item_activated_1, opciones);

        ndList.setAdapter(ndMenuAdapter);
        ndList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                Fragment fragment = null;
                PreferenceFragment fragment_preferencias = null; //TODO en construccion
                ultimaPos = pos;
                switch (pos) {
                    case 0:
                        fragment = new Fragment_NuevaRuta();

                        break;
                    case 1:
                        fragment = new Fragment_RealizarRuta();
                        break;
                    case 2:
                        fragment_preferencias = new Fragment_Configuracion();
                        break;
                    case 3:
                        fragment = new Fragment_Manual();
                        break;
                    case 4:
                        fragment = new Fragment_AcercaDe();

                }

                if (fragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_frame, fragment)
                            .commit();

                    ndList.setItemChecked(pos, true);

                    if(getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(opciones[pos]);
                    }
                }
                drawerLayout.closeDrawer(sifl);
            }
        });

        //Drawer Layout

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerLayout.setStatusBarBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.colorPrimaryDark));

        drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.openDrawer, R.string.closeDrawer){

        };

        drawerLayout.setDrawerListener(drawerToggle);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

    }*/
/*
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }*/
/*
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }
*/
/*
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            Fragment fragment;
            if (ultimaPos > 0) {TODO
                fragment = new Fragment_NuevaRuta();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .commit();

                ndList.setItemChecked(ultimaPos, true);

                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(opciones[0]);
                }
                drawerLayout.closeDrawer(sifl);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }*/

    /*
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.op1) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}

       /* Button btn = (Button)findViewById(R.id.buttonNueva);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
            }
        });

    }*/

    /*

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.op1:
                Log.d("MENU", "Preferencias");
                return true;

            case R.id.op2:
                Log.d("MENU", "Acerca de");
                return true;

            default:
                return false;
        }
    }

}*/
