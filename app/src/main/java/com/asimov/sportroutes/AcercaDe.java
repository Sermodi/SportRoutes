package com.asimov.sportroutes;

/**
 * Created by Sarekito on 16/11/15.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class AcercaDe extends Fragment {

    public AcercaDe() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View oneView = inflater.inflate(R.layout.activity_acerca_de, container, false);
        // Inflate the layout for this fragment
        return oneView;
    }
}
