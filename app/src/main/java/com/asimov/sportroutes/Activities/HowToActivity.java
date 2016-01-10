package com.asimov.sportroutes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.asimov.sportroutes.R;

/**
 * Activity que ofrece una pequeña ayuda al usuario.
 */
public class HowToActivity  extends AppCompatActivity {
    private DrawerLayout drawerLayoutHowTo = null;
    private String[] opcionesHowTo;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        opcionesHowTo = new String[]{getString(R.string.accion1), getString(R.string.accion2),
                getString(R.string.accion3), getString(R.string.accion4), getString(R.string.acercaDe)};
        super.onCreate(savedInstanceState);
        setContentView(R.layout.how_to_activity);

        drawerLayoutHowTo = (DrawerLayout) findViewById(R.id.drawer_layout_how_to);
        linearLayout = (LinearLayout) findViewById(R.id.Linear1);
        ListView listViewHowTo = (ListView) findViewById(R.id.list_view);
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
                if (drawerLayoutHowTo.isDrawerOpen(linearLayout)) {
                    drawerLayoutHowTo.closeDrawers();
                } else {
                    drawerLayoutHowTo.openDrawer(linearLayout);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
