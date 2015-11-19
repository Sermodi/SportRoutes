package com.asimov.sportroutes;

/**
 * Created by Sarekito on 16/11/15.
 */

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class Fragment3 extends PreferenceFragment {

    public Fragment3() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencias);
/*
        btnPreferencias = (Button)findViewById(R.id.BtnPreferencias);

        btnPreferencias.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,
                        OpcionesActivity.class));
            }
        });*/
    }
}
