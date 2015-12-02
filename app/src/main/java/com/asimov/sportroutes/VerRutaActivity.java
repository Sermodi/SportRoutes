package com.asimov.sportroutes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class VerRutaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_ruta);
        Intent intent = getIntent();
        Ruta ruta = intent.getParcelableExtra("ruta");
        Toast toast = Toast.makeText(getApplicationContext(),ruta.toString(),Toast.LENGTH_SHORT);
        toast.show();
    }
}
