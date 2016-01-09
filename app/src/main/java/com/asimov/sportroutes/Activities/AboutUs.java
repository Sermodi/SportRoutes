package com.asimov.sportroutes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.asimov.sportroutes.R;

/**
 * Activity que contiene la información referente a AcercaDe, con el nombre de los autores y colaboradores.
 */
public class AboutUs extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acerca_de);
        Button btn = (Button)findViewById(R.id.botonBackTo);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AboutUs.this, MainActivity.class));
            }
        });
    }


}
