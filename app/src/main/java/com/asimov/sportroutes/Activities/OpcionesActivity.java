package com.asimov.sportroutes.Activities;

import android.app.Activity;
import android.os.Bundle;

import com.asimov.sportroutes.Fragments.Fragment_Configuracion;

/**
 * Muestra el activity de opciones.
 * Created by Sarekito on 17/11/15.
 */
public class OpcionesActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new Fragment_Configuracion())
                .commit();
    }
}
