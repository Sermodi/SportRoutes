package com.asimov.sportroutes.Fragments;

/**
 * Fragmento que mostrara el boton NuevaRuta y una imagen consecuente.
 * Created by Sarekito on 16/11/15.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.asimov.sportroutes.Activities.MapsActivity;
import com.asimov.sportroutes.R;

public class Fragment_NuevaRuta extends Fragment {

    public Fragment_NuevaRuta() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View oneView = inflater.inflate(R.layout.fragment_fragment1, container, false);

        Button btn = (Button) oneView.findViewById(R.id.buttonNueva);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MapsActivity.class));
            }
        });
        // Inflate the layout for this fragment
        return oneView;
    }
}
