package com.asimov.sportroutes.Fragments;

/**
 * Fragmento que muestra el boton realizar ruta
 * Created by Sarekito on 16/11/15.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.asimov.sportroutes.Activities.ListaDeRutasActivity;
import com.asimov.sportroutes.R;

public class Fragment_RealizarRuta extends Fragment {

    public Fragment_RealizarRuta() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflador = inflater.inflate(R.layout.fragment_fragment2, container, false);
        Button boton = (Button) inflador.findViewById(R.id.buttonRealizar);
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),ListaDeRutasActivity.class);
                startActivity(intent);
            }
        });
        // Inflate the layout for this fragment
        return inflador;
    }
}