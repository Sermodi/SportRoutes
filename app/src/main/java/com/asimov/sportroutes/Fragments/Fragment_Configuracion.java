package com.asimov.sportroutes.Fragments;

/**
 * Fragmento de preferencias
 * Created by Sarekito on 16/11/15.
 */

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.asimov.sportroutes.R;

public class Fragment_Configuracion extends PreferenceFragment {

    public Fragment_Configuracion() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencias);
        getPreferenceScreen().removePreference(getPreferenceScreen().findPreference("primeraVez"));
    }
}
