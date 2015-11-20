package com.asimov.sportroutes;

import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ScrimInsetsFrameLayout sifl;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ListView ndList;
    private int ultimaPos;
    private final String[] opciones = new String[]{"Nueva ruta", "Realizar ruta", "Preferencias", "How to..."}; //TODO

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sifl = (ScrimInsetsFrameLayout) findViewById(R.id.scrimInsetsFrameLayout);

        //Toolbar

        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);



        //Menu del Navigation Drawer

        ndList = (ListView) findViewById(R.id.navdrawerlist);


        ArrayAdapter<String> ndMenuAdapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_activated_1, opciones);

        ndList.setAdapter(ndMenuAdapter);
        ndList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                Fragment fragment = null;
                PreferenceFragment fragment_preferencias = null;
                ultimaPos = pos;
                switch (pos) {
                    case 0:
                        fragment = new Fragment1();

                        break;
                    case 1:
                        fragment = new Fragment2();
                        break;
                    case 2:
                        //TODO preferencias
                        fragment_preferencias = new Fragment3();
                        break;
                    case 3:
                        fragment = new Fragment4();
                        break;
                }

                if (fragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_frame, fragment)
                            .commit();

                    ndList.setItemChecked(pos, true);

                    getSupportActionBar().setTitle(opciones[pos]);
                }
                drawerLayout.closeDrawer(sifl);
            }
        });

        //Drawer Layout

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.openDrawer, R.string.closeDrawer){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menuprincipal, menu);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            Fragment fragment = null;
            if (ultimaPos > 0) {
                fragment = new Fragment1();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .commit();

                ndList.setItemChecked(ultimaPos, true);

                getSupportActionBar().setTitle(opciones[0]);
                drawerLayout.closeDrawer(sifl);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
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
    }
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
