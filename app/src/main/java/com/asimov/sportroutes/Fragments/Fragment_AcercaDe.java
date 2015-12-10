package com.asimov.sportroutes.Fragments;

/**
 * Fragmento que muestra la informacion de la aplicacion y los desarrolladores.
 * Created by Sarekito on 16/11/15.
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.asimov.sportroutes.R;

public class Fragment_AcercaDe extends Fragment {

    public Fragment_AcercaDe() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_acerca_de, container, false);
    }
}
