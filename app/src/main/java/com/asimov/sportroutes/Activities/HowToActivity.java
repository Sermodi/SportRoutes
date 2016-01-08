package com.asimov.sportroutes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.asimov.sportroutes.R;

/**
 * Created by Sarekito on 5/1/16.
 */
public class HowToActivity  extends AppCompatActivity {
    DrawerLayout drawerLayoutHowTo = null;
    ListView listViewHowTo = null;
    String[] opcionesHowTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        opcionesHowTo = new String[]{getString(R.string.accion1), getString(R.string.accion2),
                getString(R.string.accion3), getString(R.string.accion4), getString(R.string.acercaDe)};
        super.onCreate(savedInstanceState);
        setContentView(R.layout.how_to_activity);
        drawerLayoutHowTo = (DrawerLayout) findViewById(R.id.drawer_layout_how_to);
        listViewHowTo = (ListView) findViewById(R.id.list_view_how_to);
        listViewHowTo.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1,
                opcionesHowTo));
        listViewHowTo.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Toast.makeText(HowToActivity.this, "Action: " + opcionesHowTo[arg2],
                        Toast.LENGTH_SHORT).show();
                switch (arg2) {
                    case 0:
                        startActivity(new Intent(HowToActivity.this, MapsActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(HowToActivity.this, ListaDeRutasActivity.class));
                        break;
                    case 2:
                        //Preferencias
                        //startActivity(new Intent(MainActivity.this, ))
                        break;
                    case 3:
                        //Manual
                        startActivity(new Intent(HowToActivity.this, HowToActivity.class));
                        break;
                    case 4:
                        startActivity(new Intent(HowToActivity.this, AboutUs.class));
                        break;
                }

                drawerLayoutHowTo.closeDrawers();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (drawerLayoutHowTo.isDrawerOpen(listViewHowTo)) {
                    drawerLayoutHowTo.closeDrawers();
                } else {
                    drawerLayoutHowTo.openDrawer(listViewHowTo);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
